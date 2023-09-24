package com.wellnr.ddd.spring;

import com.wellnr.ddd.AggregateRoot;
import com.wellnr.ddd.DomainRepository;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DomainRepositoryBeanPostProcessor implements BeanPostProcessor {

    public Object createDomainRepositoryProxy(Object bean) {
        var factory = new ProxyFactory();
        factory.setSuperclass(bean.getClass());

        var proxyClass = factory.createClass();
        var objenesis = new ObjenesisStd();
        var proxy = objenesis.newInstance(proxyClass);

        ((Proxy) proxy).setHandler((self, thisMethod, proceed, args) -> {
            var result = thisMethod.invoke(bean, args);

            if (Objects.isNull(result)) {
                return null;
            } else if (result instanceof AggregateRoot<?, ?> agg) {
                return createAggregateRootProxy(agg);
            } else if (result instanceof List<?> col) {
                return col
                    .stream()
                    .map(this::createAggregateRootProxyForObject)
                    .collect(Collectors.toList());
            } else if (result instanceof Set<?> col) {
                return col
                    .stream()
                    .map(this::createAggregateRootProxyForObject)
                    .collect(Collectors.toSet());
            } else if (result instanceof Stream<?> col) {
                return col
                    .map(this::createAggregateRootProxyForObject);
            } else if (result instanceof Optional<?> opt) {
                return opt.map(this::createAggregateRootProxyForObject);
            } else {
                return result;
            }
        });

        return proxy;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {

        if (bean instanceof DomainRepository) {
            return createDomainRepositoryProxy(bean);
        } else {
            return bean;
        }
    }

    private <T extends AggregateRoot<?, ?>> T createAggregateRootProxy(T agg) {
        return agg;
    }

    @SuppressWarnings("unchecked")
    private <T> T createAggregateRootProxyForObject(T obj) {
        if (obj instanceof AggregateRoot<?, ?> agg) {
            return (T) createAggregateRootProxy(agg);
        } else {
            return obj;
        }
    }

}
