package com.wellnr.ddd;

import org.springframework.data.domain.AbstractAggregateRoot;

import java.net.URI;
import java.text.MessageFormat;

public abstract class AggregateRoot<T, SELF extends AggregateRoot<T, SELF>> extends AbstractAggregateRoot<SELF> {

    public abstract T getId();

    public URI getUri() {
        return URI.create(MessageFormat.format(
            "urn:{0}:{1}",
            this.getClass().getSimpleName().toLowerCase(),
            this.getId()
        ));
    }

}
