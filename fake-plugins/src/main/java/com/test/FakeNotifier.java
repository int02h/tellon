package com.test;

import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.ProjectItem;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ProjectInfo;
import com.dpforge.tellon.core.parser.AnnotatedBlock;

public class FakeNotifier implements ChangesNotifier {
    @Override
    public String getName() {
        return "fake";
    }

    public String getDescription() {
        return "Fake implementation for test and debug purposes";
    }

    @Override
    public void onStartProject(ProjectInfo projectInfo) {
        System.out.format("***** %s *****", projectInfo.getName());
        System.out.println();
    }

    @Override
    public void onFinishedProject() {
        System.out.println("**********");
    }

    @Override
    public void notifyChanges(ProjectItem item, Changes changes) {
        System.out.println("[" + item.getDescription() + "]");

        if (changes.hasAdded()) {
            System.out.println("Added:");
            for (AnnotatedBlock block : changes.getAdded()) {
                System.out.println(block.toString());
            }
            System.out.println();
        }

        if (changes.hasDeleted()) {
            System.out.println("Deleted:");
            for (AnnotatedBlock block : changes.getDeleted()) {
                System.out.println(block.toString());
            }
            System.out.println();
        }

        if (changes.hasUpdated()) {
            System.out.println("Updated:");
            for (Changes.Update update : changes.getUpdated()) {
                System.out.format("Old block: %s\n", update.getOldBlock());
                System.out.format("New block: %s\n", update.getNewBlock());
            }
            System.out.println();
        }
    }

    @Override
    public void notifyItemAdded(ProjectItem item) {
        System.out.println("Item added: " + item.getDescription());
    }

    @Override
    public void notifyItemDeleted(ProjectItem item) {
        System.out.println("Item deleted: " + item.getDescription());
    }
}
