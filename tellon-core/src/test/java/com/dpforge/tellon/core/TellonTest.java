package com.dpforge.tellon.core;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.observer.*;
import com.dpforge.tellon.core.parser.SourceCode;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

public class TellonTest {

    @Test
    public void emptyWalker() throws Exception {
        final List<String> notifierCalls = process();
        assertArrayEquals(new String[]{"onStartProject", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void actualAndPrevious() throws Exception {
        final List<String> notifierCalls = process(item(
                code("class Foo { @NotifyChanges(\"test\") int a; }"),
                code("class Foo { @NotifyChanges(\"test\") Integer a; }")
        ));
        assertArrayEquals(new String[]{"onStartProject", "notifyChanges", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void actualAndPreviousNoChanges() throws Exception {
        final List<String> notifierCalls = process(item(
                code("class Foo { @NotifyChanges(\"test\") int a; }"),
                code("class Foo { @NotifyChanges(\"test\") int a; }")
        ));

        assertArrayEquals(new String[]{"onStartProject", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void onlyActual() throws Exception {
        final List<String> notifierCalls = process(item(
                null,
                code("class Foo { @NotifyChanges(\"test\") Integer a; }")
        ));
        assertArrayEquals(new String[]{"onStartProject", "notifyItemAdded", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void onlyActualNoAnnotation() throws Exception {
        final List<String> notifierCalls = process(item(
                null,
                code("class Foo { Integer a; }")
        ));
        assertArrayEquals(new String[]{"onStartProject", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void onlyPrevious() throws Exception {
        final List<String> notifierCalls = process(item(
                code("class Foo { @NotifyChanges(\"test\") int a; }"),
                null
        ));
        assertArrayEquals(new String[]{"onStartProject", "notifyItemDeleted", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void onlyPreviousNoAnnotated() throws Exception {
        final List<String> notifierCalls = process(item(
                code("class Foo { int a; }"),
                null
        ));
        assertArrayEquals(new String[]{"onStartProject", "onFinishedProject"}, notifierCalls.toArray());
    }

    @Test
    public void manyNotifiers() throws Exception {
        final Tellon tellon = new Tellon();
        final List<String> firstCalls = new ArrayList<>();
        final List<String> secondCalls = new ArrayList<>();

        tellon.addNotifiers(Arrays.asList(createNotifier(firstCalls), createNotifier(secondCalls)));
        tellon.process(new TestObserver());

        assertArrayEquals(firstCalls.toArray(), secondCalls.toArray());
        assertArrayEquals(new String[]{"onStartProject", "onFinishedProject"}, firstCalls.toArray());
    }

    private static List<String> process(final TestProjectItem... items) throws IOException {
        final Tellon tellon = new Tellon();
        final List<String> notifierCalls = new ArrayList<>();
        tellon.addNotifier(createNotifier(notifierCalls));
        tellon.process(new TestObserver(items));
        return notifierCalls;
    }

    private static ChangesNotifier createNotifier(final List<String> calls) {
        return (ChangesNotifier) Proxy.newProxyInstance(TellonTest.class.getClassLoader(),
                new Class[]{ChangesNotifier.class},
                (proxy, method, args) -> {
                    calls.add(method.getName());
                    return null;
                });
    }

    private static TestProjectItem item(final String prevCode, final String actualCode) {
        return new TestProjectItem(prevCode, actualCode);
    }

    private static String code(final String clazz) {
        return "package com.test;" +
                "import com.dpforge.tellon.annotations.NotifyChanges;" +
                clazz;
    }

    private static class TestObserver implements ProjectObserver {

        private final ProjectItem[] items;

        TestObserver(final TestProjectItem... items) {
            this.items = items;
        }

        @Override
        public void init(Map<String, String> args) throws ProjectObserverException {
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getDescription() {
            return "for test purposes";
        }

        @Override
        public ProjectInfo getProjectInfo() {
            return new ProjectInfo.Builder().name("Test Project").build();
        }

        @Override
        public ProjectWalker createWalker() {
            return new TestWalker(items);
        }

        @Override
        public SourceCode getSourceCode(String qualifiedName) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private static class TestWalker implements ProjectWalker {

        private final ProjectItem[] items;

        private int index = -1;

        private TestWalker(final ProjectItem[] items) {
            this.items = items;
        }

        @Override
        public boolean hasNext() {
            return index + 1 < items.length;
        }

        @Override
        public ProjectItem next() {
            return items[++index];
        }
    }

    private static class TestProjectItem implements ProjectItem {
        final String prevCode;
        final String actualCode;

        private TestProjectItem(String prevCode, String actualCode) {
            this.prevCode = prevCode;
            this.actualCode = actualCode;
        }

        @Override
        public String getDescription() {
            return "test description";
        }

        @Override
        public boolean hasActual() {
            return actualCode != null;
        }

        @Override
        public SourceCode getActual() throws IOException {
            if (hasActual()) {
                return SourceCode.createFromContent(actualCode);
            }
            throw new IllegalStateException();
        }

        @Override
        public Revision getActualRevision() throws IOException {
            if (hasActual()) {
                return new Revision.Builder("v2").build();
            }
            throw new IllegalStateException();
        }

        @Override
        public boolean hasPrevious() {
            return prevCode != null;
        }

        @Override
        public SourceCode getPrevious() throws IOException {
            if (hasPrevious()) {
                return SourceCode.createFromContent(prevCode);
            }
            throw new IllegalStateException();
        }

        @Override
        public Revision getPreviousRevision() throws IOException {
            if (hasPrevious()) {
                return new Revision.Builder("v1").build();
            }
            throw new IllegalStateException();
        }
    }
}