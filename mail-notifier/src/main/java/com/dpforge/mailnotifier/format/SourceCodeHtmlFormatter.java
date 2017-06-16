package com.dpforge.mailnotifier.format;

import com.dpforge.mailnotifier.FileUtils;

import java.io.IOException;
import java.net.URL;

public class SourceCodeHtmlFormatter {

    private static final String RESOURCE_NAME = "patterns/source-code.html";
    private static final String LINE_SEPARATOR = "\n";
    private static final int MAX_LINES = 10;

    private final String pattern;

    private SourceCodeHtmlFormatter(String pattern) {
        this.pattern = pattern;
    }

    public String getHtml(final String src) {
        final String[] lines = src.split(LINE_SEPARATOR);
        final StringBuilder lineNumberBuilder = new StringBuilder();
        final StringBuilder sourceBuilder = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            lineNumberBuilder.append(i + 1).append(LINE_SEPARATOR);
            sourceBuilder.append(lines[i]).append(LINE_SEPARATOR);
            if (i >= MAX_LINES) {
                lineNumberBuilder.append("…");
                sourceBuilder.append("…");
                break;
            }
        }

        return String.format(pattern, lineNumberBuilder.toString(), sourceBuilder.toString());
    }

    public static SourceCodeHtmlFormatter create() throws IOException {
        final URL resource = SourceCodeHtmlFormatter.class
                .getClassLoader()
                .getResource(RESOURCE_NAME);

        if (resource == null) {
            throw new IOException("Could not find resource " + RESOURCE_NAME);
        }

        final String pattern = FileUtils.readFile(resource.getFile());
        return new SourceCodeHtmlFormatter(pattern);
    }
}
