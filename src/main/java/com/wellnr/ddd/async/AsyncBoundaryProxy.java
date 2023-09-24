package com.wellnr.ddd.async;

import com.wellnr.common.Operators;
import com.wellnr.common.functions.Function1;
import com.wellnr.common.markup.Done;
import com.wellnr.common.markup.Either;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import org.slf4j.LoggerFactory;
import org.springframework.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public final class AsyncBoundaryProxy implements MethodHandler {

    private final Object delegate;

    private final ExecutorService executors;

    private final ReadWriteLock stateLock;

    private State state;

    private static AsyncBoundaryProxy apply(Object delegate) {
        return apply(
            delegate,
            Executors.newFixedThreadPool(10),
            new ReentrantReadWriteLock(),
            State.apply(Mode.IDLE, new ConcurrentLinkedQueue<>(), 0)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T delegate, Class<T> interfaceType) {
        var factory = new ProxyFactory();
        factory.setSuperclass(interfaceType);

        var proxyClass = factory.createClass();
        var objenesis = new ObjenesisStd();
        var proxy = objenesis.newInstance(proxyClass);

        ((Proxy) proxy).setHandler(AsyncBoundaryProxy.apply(delegate));

        return (T) proxy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(T delegate) {
        return createProxy(delegate, (Class<T>) delegate.getClass());
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method method, Object[] args) throws Throwable {
        var maybeAsyncLock = Optional.ofNullable(method.getAnnotation(AsyncLock.class));
        var asyncLock = maybeAsyncLock.orElseGet(() -> {
            LoggerFactory.getLogger(delegate.getClass()).warn(
                "Method `{}` is not annotated with @AsyncMethod. Its highly recommended to annotate " +
                    "methods of classes wrapped by {},",
                method.getName(), this.getClass().getSimpleName()
            );

            /*
             * Simulate default annotation, if annotation was not found.
             */
            return new AsyncLock() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AsyncLock.class;
                }

                @Override
                public LockType value() {
                    return LockType.READ_WRITE_INSTANCE;
                }

            };
        });

        var result = new CompletableFuture<>();
        this.addToInbox(Call.apply(method, args, result, asyncLock));

        if (method.getReturnType().isAssignableFrom(CompletionStage.class)) {
            return result;
        } else {
            try {
                return result.get();
            } catch (Throwable ex) {
                if (Objects.isNull(ex.getCause())) {
                    throw ex;
                } else {
                    throw ex.getCause();
                }
            }
        }
    }

    private void addToInbox(Call call) {
        this.updateState(currentState -> {
            currentState.inbox.add(call);
            return currentState;
        });

        this.process();
    }

    private void execute(Call call) {
        CompletableFuture.runAsync(() -> {
            try {

                var result = call.method.invoke(delegate, call.args);

                if (!Objects.isNull(result) && result instanceof CompletionStage<?> cs) {
                    cs
                        .handle(Either::apply)
                        .thenApply(r -> r
                            .ifLeft(call.result::complete)
                            .ifRight(call.result::completeExceptionally));
                } else {
                    call.result.complete(result);
                }
            } catch (InvocationTargetException ex) {
                call.result.completeExceptionally(ex.getCause());
            } catch (Exception ex) {
                call.result.completeExceptionally(ex);
            }

            call
                .result
                .handle((i1, i2) -> Done.getInstance())
                .thenAccept(i -> {
                    this.updateState(s -> {
                        s = s.withRunningCalls(s.runningCalls - 1);

                        if (s.runningCalls <= 0) {
                            s = s.withMode(Mode.IDLE);
                        }

                        return s;
                    });

                    this.process();
                });
        }, this.executors);
    }

    private void process() {
        this.updateState(currentState -> {
            if (currentState.getInbox().isEmpty()) {
                return currentState;
            }

            var nextCall = currentState.getInbox().element();

            if (
                nextCall.getMode().value().equals(LockType.READ_ONLY) && (currentState.getMode()
                    .equals(Mode.IDLE) || currentState.getMode()
                    .equals(Mode.PURE))
            ) {
                /*
                 * Pure methods can be executed along with others.
                 */
                var call = currentState.getInbox().poll();
                this.execute(call);

                return currentState
                    .withRunningCalls(currentState.runningCalls + 1)
                    .withMode(Mode.PURE);
            } else if (currentState.getMode().equals(Mode.IDLE)) {
                var call = currentState.getInbox().poll();
                this.execute(call);

                return currentState
                    .withRunningCalls(currentState.runningCalls + 1)
                    .withMode(Mode.WRITING);
            } else {
                return currentState;
            }
        });
    }

    private void updateState(Function1<State, State> updateFn) {
        this.stateLock.writeLock().lock();

        this.state = Operators.ignoreExceptionsWithDefault(
            () -> updateFn.get(this.state), state
        );

        this.stateLock.writeLock().unlock();
    }

    private enum Mode {
        /**
         * Nothing is currently executed.
         */
        IDLE,

        /**
         * A read operation is currently executing.
         * Additional read operations may be executed.
         * Writes are not allowed, to ensure consistent reads.
         */
        PURE,

        /**
         * A write operation is currently executing.
         * Additional operations must wait.
         */
        WRITING
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class Call {

        Method method;

        Object[] args;

        CompletableFuture<Object> result;

        AsyncLock mode;

    }

    @With
    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class State {

        Mode mode;

        Queue<Call> inbox;

        int runningCalls;

    }

}
