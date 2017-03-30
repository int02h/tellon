package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.SourceCode;

import java.io.IOException;

public interface ProjectItem {
    String getDescription();

    boolean hasActual();

    SourceCode getActual() throws IOException;

    boolean hasPrevious();

    SourceCode getPrevious() throws IOException;
}
