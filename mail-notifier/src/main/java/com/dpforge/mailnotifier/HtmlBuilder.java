package com.dpforge.mailnotifier;

public class HtmlBuilder {
    private final StringBuilder builder = new StringBuilder();

    public HtmlBuilder text(final String text) {
        builder.append(text);
        return this;
    }

    public HtmlBuilder line(final String text) {
        return text(text).br();
    }

    public HtmlBuilder line(final String text, final Object... args) {
        return line(String.format(text, args));
    }

    public HtmlBuilder bold(final String text) {
        builder.append("<b>").append(text).append("</b>");
        return this;
    }

    public HtmlBuilder br() {
        builder.append("<br/>");
        return this;
    }

    public HtmlBuilder clear() {
        builder.setLength(0);
        return this;
    }

    public String build() {
        return builder.toString();
    }
}
