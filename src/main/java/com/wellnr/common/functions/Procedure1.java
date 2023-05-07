package com.wellnr.common.functions;

import com.wellnr.common.Operators;

@FunctionalInterface
public interface Procedure1<T> {

    void apply(T t) throws Exception;

    default void run(T t) {
        Operators.suppressExceptions(() -> this.apply(t));
    }

}
