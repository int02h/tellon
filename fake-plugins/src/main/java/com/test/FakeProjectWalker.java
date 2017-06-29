package com.test;

import com.dpforge.tellon.core.walker.ProjectItem;
import com.dpforge.tellon.core.walker.ProjectWalker;
import com.dpforge.tellon.core.walker.ProjectWalkerException;
import com.dpforge.tellon.core.walker.Revision;
import com.dpforge.tellon.core.walker.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeProjectWalker implements ProjectWalker {
    private final ProjectInfo projectInfo = new ProjectInfo.Builder().name("Test Project").build();

    private final List<ProjectItem> items = new ArrayList<>();

    private int index = 0;

    public FakeProjectWalker() {
        items.add(new ProjectItem() {
            @Override
            public String getDescription() {
                return "Foo.java";
            }

            @Override
            public boolean hasActual() {
                return true;
            }

            @Override
            public SourceCode getActual() {
                return SourceCode.createFromContent(new String[] {
                        "package com.test; ",
                        "import com.dpforge.tellon.annotations.NotifyChanges; ",
                        "@NotifyChanges(\"test@test.com\")",
                        "class Foo {",
                        "    @NotifyChanges(\"test@test.com\")",
                        "    Integer changed;",
                        "    @NotifyChanges(\"test@test.com\")",
                        "    String added;",
                        "}"});
            }

            @Override
            public Revision getActualRevision() {
                return new Revision.Builder("v1").build();
            }

            @Override
            public boolean hasPrevious() {
                return true;
            }

            @Override
            public SourceCode getPrevious() {
                return SourceCode.createFromContent(new String[] {
                        "package com.test; ",
                        "import com.dpforge.tellon.annotations.NotifyChanges; ",
                        "@NotifyChanges(\"test@test.com\")",
                        "final class Foo {",
                        "    @NotifyChanges(\"test@test.com\")",
                        "    int changed;",
                        "    @NotifyChanges(\"test@test.com\")",
                        "    boolean deleted;",
                        "}"});
            }

            @Override
            public Revision getPreviousRevision() {
                return new Revision.Builder("v0").build();
            }
        });
    }

    @Override
    public void init(Map<String, String> args) throws ProjectWalkerException {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> arg : args.entrySet()) {
            builder.append(arg.getKey()).append("=").append(arg.getValue()).append('\n');
        }
        System.out.println("Initialized with args: \n" + builder.toString());
    }

    @Override
    public String getName() {
        return "fake";
    }

    @Override
    public String getDescription() {
        return "Fake project walker";
    }

    @Override
    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    @Override
    public boolean hasNext() {
        return index < items.size();
    }

    @Override
    public ProjectItem next() {
        return hasNext() ? items.get(index++) : null;
    }
}
