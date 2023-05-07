package com.wellnr.ddd;

import java.lang.reflect.Method;

public interface BeanValidation {

    void validateObject(Object object);

    void validateParameters(Object object, Method method, Object... args);

}
