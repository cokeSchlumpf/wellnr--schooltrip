package com.wellnr.common;

import com.vaadin.flow.internal.CaseUtil;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;

import java.lang.reflect.UndeclaredThrowableException;

public class Operators {

    private Operators() {

    }

    public static String camelCaseToKebabCase(String s) {
        return s
            .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
            .replaceAll("([A-Z])([A-Z])(?=[a-z])", "$1-$2")
            .toLowerCase();
    }

    public static String stringToCamelCase(String s) {
        return CaseUtils.toCamelCase(s, false);
    }

    public static String stringToTechFriendlyName(String s) {
        return camelCaseToKebabCase(stringToCamelCase(s));
    }

    public static void ignoreExceptions(ExceptionalRunnable runnable, Logger log) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (log != null) {
                log.warn("An exception occurred but will be ignored", e);
            }
        }
    }

    public static void ignoreExceptions(ExceptionalRunnable runnable) {
        ignoreExceptions(runnable, null);
    }

    public static <T> T ignoreExceptionsWithDefault(ExceptionalSupplier<T> supplier, T defaultValue, Logger log) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (log != null) {
                log.warn("An exception occurred but will be ignored", e);
            }

            return defaultValue;
        }
    }

    public static <T> T ignoreExceptionsWithDefault(ExceptionalSupplier<T> supplier, T defaultValue) {
        return ignoreExceptionsWithDefault(supplier, defaultValue, null);
    }

    @SuppressWarnings({"CatchMayIgnoreException", "ResultOfMethodCallIgnored"})
    public static void suppressExceptions(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable e) {
            wrapAndThrow(e);
        }
    }

    public static void suppressExceptions(ExceptionalRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    public static <T> T suppressExceptions(ExceptionalSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (e instanceof RuntimeException re) {
                throw re;
            } else {
                return wrapAndThrow(e);
            }
        }
    }

    public static <T> T suppressExceptions(ExceptionalSupplier<T> supplier, String message) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(message, e);
        }
    }

    public static <R> R wrapAndThrow(final Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }

        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        throw new UndeclaredThrowableException(throwable);
    }

    @FunctionalInterface
    public interface ExceptionalRunnable {

        void run() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalConsumer<T> {

        void accept(T param) throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalSupplier<T> {

        T get() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalFunction<I, R> {

        R apply(I in) throws Exception;

    }

}
