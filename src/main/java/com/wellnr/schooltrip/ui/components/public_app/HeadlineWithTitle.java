package com.wellnr.schooltrip.ui.components.public_app;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;

public class HeadlineWithTitle extends H2 {

    public HeadlineWithTitle(String title, String header) {
        var headlineHeaderTitle = new Span(title);

        headlineHeaderTitle.addClassName("app__headline-with-title__title");

        this.add(
            headlineHeaderTitle,
            new Span(header)
        );

        this.addClassName("app__headline-with-title__header");
    }

}
