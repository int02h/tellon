package com.dpforge.tellon.core;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ChangesNotifierException;
import com.dpforge.tellon.core.walker.*;
import com.dpforge.tellon.core.parser.BlockType;
import com.dpforge.tellon.core.parser.SourceCode;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TellonTest {
    @Test
    public void simple() throws Exception {
        final Tellon tellon = new Tellon();

        final TestObserver observer = new TestObserver();
        observer.init(Collections.emptyMap());

        final TestNotifier notifier = new TestNotifier();
        tellon.addNotifier(notifier);

        tellon.process(observer);

        assertTrue(notifier.finished);
        assertEquals("Test Project", notifier.projectName);

        assertEquals(1, notifier.changesList.size());
        final Changes changes = notifier.changesList.get(0);

        // Updated block
        assertEquals(1, changes.getUpdated().size());
        final Changes.Update update = changes.getUpdated().get(0);
        assertEquals(BlockType.FIELD, update.getOldBlock().getType());
        assertEquals(BlockType.FIELD, update.getNewBlock().getType());
        assertEquals("value", update.getOldBlock().getName());
        assertEquals("value", update.getNewBlock().getName());

        // Addded block
        assertEquals(1, changes.getAdded().size());
        assertEquals(BlockType.METHOD, changes.getAdded().get(0).getType());
        assertEquals("added", changes.getAdded().get(0).getName());

        // Removed block
        assertEquals(1, changes.getAdded().size());
        assertEquals(BlockType.ANNOTATION, changes.getDeleted().get(0).getType());
        assertEquals("Removed", changes.getDeleted().get(0).getName());

        // Added Item
        assertEquals(1, notifier.addedList.size());
        final Changes addedItem = notifier.addedList.get(0);
        assertFalse(addedItem.hasDeleted());
        assertFalse(addedItem.hasUpdated());
        assertEquals(1, addedItem.getAdded().size());
        assertEquals(BlockType.TYPE, addedItem.getAdded().get(0).getType());
        assertEquals("NewClass", addedItem.getAdded().get(0).getName());

        // Deleted Item
        assertEquals(1, notifier.deletedList.size());
        final Changes deletedItem = notifier.deletedList.get(0);
        assertFalse(deletedItem.hasAdded());
        assertFalse(deletedItem.hasUpdated());
        assertEquals(1, deletedItem.getDeleted().size());
        assertEquals(BlockType.ANNOTATION, deletedItem.getDeleted().get(0).getType());
        assertEquals("FooBarAnnotation", deletedItem.getDeleted().get(0).getName());
    }

    private static class TestObserver implements ProjectObserver {

        private ProjectItem[] items;

        @Override
        public void init(Map<String, String> args) throws ProjectObserverException {
            items = new ProjectItem[] {
                    new UpdatedProjectItem(),
                    new AddedProjectItem(),
                    new DeletedProjectItem()
            };
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
    }

    private static class TestWalker implements ProjectWalker {

        private final ProjectItem[] items;

        private int index = -1;

        private TestWalker(ProjectItem[] items) {
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

    private static class UpdatedProjectItem implements ProjectItem {

        @Override
        public String getDescription() {
            return "updated project item";
        }

        @Override
        public boolean hasActual() {
            return true;
        }

        @Override
        public SourceCode getActual() throws IOException {
            return SourceCode.createFromContent(new String[]{
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "class Foo",
                    "    { @NotifyChanges(\"test@test.com\")",
                    "    String value = null;",
                    "    @NotifyChanges(\"added@test.com\")",
                    "    int added() { return   0  ; }",
                    "}"});
        }

        @Override
        public Revision getActualRevision() throws IOException {
            return new Revision.Builder("v1").build();
        }

        @Override
        public boolean hasPrevious() {
            return true;
        }

        @Override
        public SourceCode getPrevious() throws IOException {
            return SourceCode.createFromContent(new String[] {
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "class Foo {",
                    "    @NotifyChanges(\"test@test.com\")",
                    "    String ",
                    "        value;",
                    "    @NotifyChanges(\"removed@test.com\")",
                    "    @interface Removed {}",
                    "}"});
        }

        @Override
        public Revision getPreviousRevision() throws IOException {
            return new Revision.Builder("v0").build();
        }
    }

    private static class AddedProjectItem implements ProjectItem {

        @Override
        public String getDescription() {
            return "added project item";
        }

        @Override
        public boolean hasActual() {
            return true;
        }

        @Override
        public SourceCode getActual() throws IOException {
            return SourceCode.createFromContent(new String[]{
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "@NotifyChanges(\"new@test.com\")",
                    "class NewClass {}"});
        }

        @Override
        public Revision getActualRevision() throws IOException {
            return new Revision.Builder("v1").build();
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public SourceCode getPrevious() throws IOException {
            throw new IllegalStateException();
        }

        @Override
        public Revision getPreviousRevision() throws IOException {
            throw new IllegalStateException();
        }
    }

    private static class DeletedProjectItem implements ProjectItem {

        @Override
        public String getDescription() {
            return "deleted project item";
        }

        @Override
        public boolean hasActual() {
            return false;
        }

        @Override
        public SourceCode getActual() throws IOException {
            throw new IllegalStateException();
        }

        @Override
        public Revision getActualRevision() throws IOException {
            throw new IllegalStateException();
        }

        @Override
        public boolean hasPrevious() {
            return true;
        }

        @Override
        public SourceCode getPrevious() throws IOException {
            return SourceCode.createFromContent(new String[]{
                    "package com.test;",
                    "import com.dpforge.tellon.annotations.NotifyChanges;",
                    "@NotifyChanges(\"foobar@test.com\")",
                    "@interface FooBarAnnotation {}"});
        }

        @Override
        public Revision getPreviousRevision() throws IOException {
            return new Revision.Builder("v1").build();
        }
    }

    private static class TestNotifier implements ChangesNotifier {

        private String projectName;
        private boolean finished;

        private final List<Changes> changesList = new ArrayList<>();
        private final List<Changes> addedList = new ArrayList<>();
        private final List<Changes> deletedList = new ArrayList<>();

        @Override
        public String getName() {
            return "test notifier";
        }

        @Override
        public String getDescription() {
            return "for test purposes";
        }

        @Override
        public void init() throws ChangesNotifierException {

        }

        @Override
        public void onStartProject(ProjectInfo projectInfo) {
            projectName = projectInfo.getName();
        }

        @Override
        public void onFinishedProject() {
            finished = true;
        }

        @Override
        public void notifyChanges(ProjectItem item, Changes changes) {
            changesList.add(changes);
        }

        @Override
        public void notifyItemAdded(ProjectItem item, Changes changes) {
            addedList.add(changes);
        }

        @Override
        public void notifyItemDeleted(ProjectItem item, Changes changes) {
            deletedList.add(changes);
        }
    }
}