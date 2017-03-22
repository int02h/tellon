package com.dpforge.tellon.core.provider;

import com.dpforge.tellon.core.parser.SourceCode;

import java.io.File;

public interface SourceCodeProvider {
    void init(String param);

    String getName();

    String getDescription();

    SourceCode getActual(final File file);

    SourceCode getPrevious(final File file);
}
