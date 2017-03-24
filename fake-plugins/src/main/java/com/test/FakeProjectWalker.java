package com.test;

import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.ProjectWalker;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.SourceCode;

import java.util.ArrayList;
import java.util.List;

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
            public SourceCode getActual() {
                return SourceCode.createFromContent("" +
                        "package com.test; " +
                        "import com.dpforge.tellon.annotations.NotifyChanges; " +
                        "@NotifyChanges(\"test@test.com\")" +
                        "class Foo {" +
                        "    @NotifyChanges(\"test@test.com\")" +
                        "    Integer changed;" +
                        "    @NotifyChanges(\"test@test.com\")" +
                        "    String added;" +
                        "}");
            }

            @Override
            public SourceCode getPrevious() {
                return SourceCode.createFromContent("" +
                        "package com.test; " +
                        "import com.dpforge.tellon.annotations.NotifyChanges; " +
                        "@NotifyChanges(\"test@test.com\")" +
                        "final class Foo {" +
                        "    @NotifyChanges(\"test@test.com\")" +
                        "    int changed;" +
                        "    @NotifyChanges(\"test@test.com\")" +
                        "    boolean deleted;" +
                        "}");
            }
        });
    }

    @Override
    public void init(String args) {
        System.out.println("Initialized with args: " + args);
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
