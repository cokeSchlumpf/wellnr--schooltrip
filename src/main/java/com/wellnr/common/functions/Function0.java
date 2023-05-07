package com.wellnr.common.functions;


import com.wellnr.common.Operators;

@FunctionalInterface
public interface Function0<R> {

    R apply() throws Exception;

    default R get() {
        return Operators.suppressExceptions(this::apply);
    }

}
