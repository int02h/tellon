package com.dpforge.tellon.core;

import com.dpforge.tellon.core.parser.SourceCode;

public interface ProjectItem {
    String getDescription();

    SourceCode getActual();

    SourceCode getPrevious();
}
