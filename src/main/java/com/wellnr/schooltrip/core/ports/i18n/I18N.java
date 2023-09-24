package com.wellnr.schooltrip.core.ports.i18n;

import com.wellnr.common.Operators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Locale;

public class I18N {

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, Locale locale) {
        var isDE = locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY);

        return (T) Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class[]{clazz},
            (proxy, method, args) -> {
                if (method.getName().equals("getClass")) {
                    return clazz;
                }

                var annotation = method.getAnnotation(DE.class);

                if (isDE && annotation != null) {
                    return String.format(annotation.value(), args);
                } else if (isDE) {
                    var deTemplate = Operators.ignoreExceptionsToOptional(
                        () -> clazz.getMethod(method.getName() + "$DE", method.getParameterTypes())
                    );

                    if (deTemplate.isPresent()) {
                        return deTemplate.get().invoke(proxy, args);
                    }
                }

                if (method.isDefault()) {
                    return InvocationHandler.invokeDefault(proxy, method, args);
                } else {
                    return method.getDefaultValue();
                }
            }
        );
    }

}
