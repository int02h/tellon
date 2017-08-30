package com.dpforge.lognotifier;

import com.dpforge.tellon.core.Changes;
import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.observer.ProjectInfo;
import com.dpforge.tellon.core.observer.ProjectItem;
import com.dpforge.tellon.core.parser.AnnotatedBlock;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogChangeNotifier implements ChangesNotifier {

    private final Logger logger = new Logger();

    @Override
    public void onStartProject(ProjectInfo projectInfo) {
    }

    @Override
    public void onFinishedProject() {
    }

    @Override
    public void notifyChanges(ProjectItem item, Changes changes) {
        for (Changes.Update update : changes.getUpdated()) {
            notifyChanges("changed", item, update.getNewBlock(), update.getOldBlock().getWatchers());
        }
        notifyItemAdded(item, changes);
        notifyItemDeleted(item, changes);
    }

    private void notifyChanges(String suffix, ProjectItem item, AnnotatedBlock block) {
        notifyChanges(suffix, item, block, Collections.emptyList());
    }

    private void notifyChanges(String suffix, ProjectItem item, AnnotatedBlock block, Collection<String> additionalWatchers) {
        Collection<String> filteredWatchers = filterWatchers(block.getWatchers(), additionalWatchers);
        if (filteredWatchers.isEmpty()) {
            return;
        }
        String watchers = String.join(" ", filteredWatchers);
        String lines = (block.getStartPosition().getLine() + 1) + "" + (block.getEndPosition().getLine() + 1);
        String message = suffix + ": " + item.getDescription() + ": " + lines + ": " + block.getName();
        logger.log(watchers + ": " + message);
    }

    private Set<String> filterWatchers(Collection<String> watchers, Collection<String> moreWatchers) {
        return Stream.concat(watchers.stream(), moreWatchers.stream())
                .filter(watcher -> watcher.startsWith(LogNotifier.PREFIX))
                .map(watcher -> watcher.substring(LogNotifier.PREFIX.length()))
                .collect(Collectors.toSet());
    }

    @Override
    public void notifyItemAdded(ProjectItem item, Changes changes) {
        for (AnnotatedBlock block : changes.getAdded()) {
            notifyChanges("added", item, block);
        }
    }

    @Override
    public void notifyItemDeleted(ProjectItem item, Changes changes) {
        for (AnnotatedBlock block : changes.getDeleted()) {
            notifyChanges("deleted", item, block);
        }
    }
}
