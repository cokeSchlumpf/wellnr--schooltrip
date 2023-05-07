package com.wellnr.common.functions;

import com.wellnr.common.Operators;

@FunctionalInterface
public interface Function2<T1, T2, R> {

    R apply(T1 t1, T2 t2) throws Exception;

    default R get(T1 t1, T2 t2) {
        return Operators.suppressExceptions(() -> this.apply(t1, t2));
    }

}
