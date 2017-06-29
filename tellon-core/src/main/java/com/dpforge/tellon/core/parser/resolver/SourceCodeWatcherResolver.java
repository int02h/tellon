package com.dpforge.tellon.core.parser.resolver;

import com.dpforge.tellon.core.observer.SourceCodeProvider;
import com.dpforge.tellon.core.parser.ConstantParser;
import com.dpforge.tellon.core.parser.SourceCode;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SourceCodeWatcherResolver implements WatcherResolver {

    private final SourceCodeProvider sourceCodeProvider;
    private final ConstantParser constantParser = new ConstantParser();

    public SourceCodeWatcherResolver(SourceCodeProvider sourceCodeProvider) {
        if (sourceCodeProvider == null) {
            throw new NullPointerException("Source code retriever cannot be null");
        }
        this.sourceCodeProvider = sourceCodeProvider;
    }

    @Override
    public List<String> resolveLiteral(String value) {
        return Collections.singletonList(value);
    }

    @Override
    public List<String> resolveReference(String qualifiedName, String field) throws IOException {
        final SourceCode code = sourceCodeProvider.getSourceCode(qualifiedName);
        final Map<String, List<String>> watcherMap = constantParser.parse(code);
        final List<String> addresses = watcherMap.get(field);
        verifyAddresses(field, addresses);
        return Collections.unmodifiableList(addresses);
    }

    private static void verifyAddresses(final String field, final List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            throw new RuntimeException("Addresses for field '" + field + "' is null or empty");
        }
        for (String address : addresses) {
            if (address == null || address.isEmpty()) {
                throw new RuntimeException("Address for field '" + field + "' is null or empty");
            }
        }
    }
}
