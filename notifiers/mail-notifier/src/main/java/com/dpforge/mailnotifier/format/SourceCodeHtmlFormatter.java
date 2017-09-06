package com.dpforge.mailnotifier.format;

import com.dpforge.mailnotifier.FileUtils;
import com.dpforge.tellon.core.parser.AnnotatedBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        final List<String> lines = cleanUpIndent(block.getSourceCode().asFragment());
        final StringBuilder lineNumberBuilder = new StringBuilder();
        final StringBuilder sourceBuilder = new StringBuilder();
        final int startLine = block.getStartPosition().getLine();

        for (int i = 0; i < lines.size(); i++) {
            if (i >= MAX_LINES) {
                lineNumberBuilder.append(MORE_CODE_MARKER);
                sourceBuilder.append(MORE_CODE_MARKER);
                break;
            }
            lineNumberBuilder.append(startLine + i + 1).append(LINE_SEPARATOR);
            sourceBuilder.append(lines.get(i)).append(LINE_SEPARATOR);
        }

        return String.format(pattern, lineNumberBuilder.toString(), sourceBuilder.toString());
    }

    public static SourceCodeHtmlFormatter create() throws IOException {
        final String pattern = FileUtils.readResource(RESOURCE_NAME);
        return new SourceCodeHtmlFormatter(pattern);
    }

    private static List<String> cleanUpIndent(final List<String> lines) {
        if (lines.isEmpty()) {
            return lines;
        }

        int min = countWhitespaceAtStart(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            min = Math.min(min, countWhitespaceAtStart(lines.get(i)));
        }

        final List<String> cleaned = new ArrayList<>(lines.size());
        for (String line : lines) {
            cleaned.add(line.substring(min));
        }

        return cleaned;
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
