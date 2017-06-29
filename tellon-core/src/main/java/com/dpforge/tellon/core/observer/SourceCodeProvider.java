package com.dpforge.tellon.core.observer;

import com.dpforge.tellon.core.parser.SourceCode;

import java.io.IOException;

public interface SourceCodeProvider {
    SourceCode getSourceCode(String qualifiedName) throws IOException;
}
