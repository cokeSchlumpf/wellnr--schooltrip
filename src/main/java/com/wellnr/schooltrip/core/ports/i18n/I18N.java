package com.wellnr.schooltrip.core.ports.i18n;

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

                    DE annotation = method.getAnnotation(DE.class);
                    if (isDE && annotation!= null) {
                        return String.format(annotation.value(), args);
                    } else if (method.isDefault()) {
                        return InvocationHandler.invokeDefault(proxy, method, args);
                    } else {
                        return method.getDefaultValue();
                    }
                }
        );
    }

}
