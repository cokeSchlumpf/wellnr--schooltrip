package com.wellnr.common.functions;

import com.wellnr.common.Operators;

@FunctionalInterface
public interface Procedure2<T1, T2> {

    void apply(T1 t1, T2 t2) throws Exception;

    default void run(T1 t1, T2 t2) {
        Operators.suppressExceptions(() -> this.apply(t1, t2));
    }

}
