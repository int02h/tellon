package com.dpforge.lognotifier;

import com.dpforge.tellon.core.notifier.ChangesNotifier;
import com.dpforge.tellon.core.notifier.ProjectNotifier;
import com.dpforge.tellon.core.notifier.ProjectNotifierException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LogNotifier implements ProjectNotifier {

    static final String PREFIX = "log:";

    private final Logger logger = new Logger();

    @Override
    public String getName() {
        return "log-notifier";
    }

    @Override
    public String getDescription() {
        return "Print changes to standard output as well as the provided label/email/anything. " +
                "Use 'log:' prefix before label. " +
                "For example: @NotifyChanges(\"log:changes@example.com\").";
    }

    @Override
    public void init() throws ProjectNotifierException {
    }

    @Override
    public void reportError(Collection<String> watchers, String errorMessage) {
        List<String> matcherWatchers = watchers.stream()
                .filter(watcher -> watcher.startsWith(PREFIX))
                .collect(Collectors.toList());
        logger.log(String.join(" ", matcherWatchers) + ": " + errorMessage);
    }

    @Override
    public ChangesNotifier getChangesNotifier() {
        return new LogChangeNotifier();
    }

}
