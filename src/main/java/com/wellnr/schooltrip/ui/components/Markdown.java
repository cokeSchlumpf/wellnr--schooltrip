package com.wellnr.schooltrip.ui.components;

import com.vaadin.flow.component.Html;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Markdown extends Html {

    private static final Parser PARSER;
    private static final HtmlRenderer RENDERER;

    static {
        PARSER = Parser.builder().build();
        RENDERER = HtmlRenderer.builder().build();
    }

    private Markdown(String html) {
        super(html);
    }

    public static Markdown apply(String markdown) {
        var html = RENDERER.render(PARSER.parse(markdown));

        if (html.split("\n").length > 1) {
            return new Markdown("<div>" + html + "</div>");
        } else {
            return new Markdown(html);
        }
    }

}
