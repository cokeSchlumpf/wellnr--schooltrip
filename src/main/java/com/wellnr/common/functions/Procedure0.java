package com.wellnr.common.functions;

import com.wellnr.common.Operators;

@FunctionalInterface
public interface Procedure0 {

    void apply() throws Exception;

    default void run() {
        Operators.suppressExceptions(this::apply);
    }

}
