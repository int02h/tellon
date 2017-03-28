package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.SourceCode;

import java.io.IOException;

public interface ProjectItem {
    String getDescription();

    SourceCode getActual() throws IOException;

    SourceCode getPrevious() throws IOException;
}
