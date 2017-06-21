package com.dpforge.mailnotifier.format;

import com.dpforge.mailnotifier.FileUtils;
import com.dpforge.tellon.core.parser.AnnotatedBlock;

import java.io.IOException;
import java.net.URL;

public class SourceCodeHtmlFormatter {

    private static final String RESOURCE_NAME = "patterns/source-code.html";
    private static final String LINE_SEPARATOR = "\n";
    private static final int MAX_LINES = 15;
    private static final String MORE_CODE_MARKER = "…";

    private final String pattern;

    private SourceCodeHtmlFormatter(String pattern) {
        this.pattern = pattern;
    }

    public String getHtml(final AnnotatedBlock block) {
        final String[] lines = cleanUpIndent(block.getSourceCode().asFragment().split("\n"));
        final StringBuilder lineNumberBuilder = new StringBuilder();
        final StringBuilder sourceBuilder = new StringBuilder();
        final int startLine = block.getStartPosition().getLine();

        for (int i = 0; i < lines.length; i++) {
            if (i >= MAX_LINES) {
                lineNumberBuilder.append(MORE_CODE_MARKER);
                sourceBuilder.append(MORE_CODE_MARKER);
                break;
            }
            lineNumberBuilder.append(startLine + i + 1).append(LINE_SEPARATOR);
            sourceBuilder.append(lines[i]).append(LINE_SEPARATOR);
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

    private static String[] cleanUpIndent(final String[] lines) {
        if (lines.length == 0) {
            return lines;
        }

        int min = countWhitespaceAtStart(lines[0]);
        for (int i = 1; i < lines.length; i++) {
            min = Math.min(min, countWhitespaceAtStart(lines[i]));
        }

        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].substring(min);
        }

        return lines;
    }

    private static int countWhitespaceAtStart(final String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return 0;
    }
}
