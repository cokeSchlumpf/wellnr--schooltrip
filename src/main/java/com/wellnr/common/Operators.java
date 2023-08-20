package com.wellnr.common;

import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Optional;

public class Operators {

    private Operators() {

    }

    public static String camelCaseToHumanReadable(String s) {
        var result = s.replaceAll(
            String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ),
            " "
        );

        if (result.length() > 1) {
            return result.substring(0, 1).toUpperCase() + result.substring(1);
        } else {
            return result.toUpperCase();
        }
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

    public static boolean fuzzyEquals(String s1, String s2) {
        return s1.strip().equalsIgnoreCase(s2.strip());
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getCauseByType(Throwable chain, Class<T> exType) {
        if (exType.isInstance(chain)) {
            return Optional.of((T) chain);
        } else if (chain.getCause() != null) {
            return getCauseByType(chain.getCause(), exType);
        } else {
            return Optional.empty();
        }
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
