package com.test;

import com.dpforge.tellon.core.parser.SourceCode;
import com.dpforge.tellon.core.provider.SourceCodeProvider;

import java.io.File;

public class FakeSourceProvider implements SourceCodeProvider {
    @Override
    public void init(String param) {

    }

    @Override
    public String getName() {
        return "fake";
    }

    @Override
    public String getDescription() {
        return "Fake implementation for test and debug purposes";
    }

    @Override
    public SourceCode getActual(File file) {
        return SourceCode.createFromContent("" +
                "package com.test; " +
                "import com.dpforge.tellon.annotations.NotifyChanges; " +
                "class " + getClassName(file) + " {" +
                "    @NotifyChanges(\"test\")" +
                "    Integer changed;" +
                "    @NotifyChanges(\"test\")" +
                "    String added;" +
                "}");
    }

    @Override
    public SourceCode getPrevious(File file) {
        return SourceCode.createFromContent("" +
                "package com.test; " +
                "import com.dpforge.tellon.annotations.NotifyChanges; " +
                "class " + getClassName(file) + " {" +
                "    @NotifyChanges(\"test\")" +
                "    int changed;" +
                "    @NotifyChanges(\"test\")" +
                "    boolean deleted;" +
                "}");
    }

    private static String getClassName(final File file) {
        final String name = file.getName();
        final int index = name.indexOf('.');
        return (index > 0)
                ? name.substring(0, index - 1)
                : "Foo";
    }
}
