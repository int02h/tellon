package com.dpforge.tellon.app.commands;

import org.apache.commons.cli.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class NotifyArguments {

    private static final String PROJECT_OBSERVER_ARGS = "observer-args";
    private static final String PROJECT_OBSERVER_ARGS_DESCRIPTION = "Arguments for project observer in the form of 'key=value'";

    private static final String PROJECT_OBSERVER_NAME = "observer";
    private static final String PROJECT_OBSERVER_NAME_SHORT = "o";
    private static final String PROJECT_OBSERVER_NAME_DESCRIPTION = "Name of project observer to use (if you have multiple)";

    private static final String PROJECT_NOTIFIER_NAMES = "notifiers";
    private static final String PROJECT_NOTIFIER_NAMES_SHORT = "n";
    private static final String PROJECT_NOTIFIER_NAMES_DESCRIPTION = "Names of project notifiers to use. If you want to use all available notifiers omit this argument.";

    private static final String MASTER_WATCHERS = "masters";
    private static final String MASTER_WATCHERS_DESCRIPTION = "Master watchers that will be notified when the error occurs in Tellon";

    private static final int LEFT_PAD = 2;
    private static final int DESC_PAD = 4;

    private final Options options = new Options();

    private final Map<String, String> observerArgs = new HashMap<>();
    private final Set<String> enabledNotifiers = new HashSet<>();
    private final List<String> masterWatchers = new ArrayList<>();

    private CommandLine cmd;

    void parse(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);

        observerArgs.clear();
        final String[] argPairs = cmd.getOptionValues(PROJECT_OBSERVER_ARGS);
        if (argPairs != null && argPairs.length % 2 == 0) {
            for (int i = 0; i < argPairs.length; i += 2) {
                observerArgs.put(argPairs[i], argPairs[i + 1]);
            }
        }

        masterWatchers.clear();
        final String[] watchers = cmd.getOptionValues(MASTER_WATCHERS);
        if (watchers != null) {
            Collections.addAll(masterWatchers, watchers);
        }

        enabledNotifiers.clear();
        final String[] notifiers = cmd.getOptionValues(PROJECT_NOTIFIER_NAMES);
        if (notifiers != null) {
            Collections.addAll(enabledNotifiers, notifiers);
        }
    }

    NotifyArguments() {
        options.addOption(Option.builder()
                .longOpt(PROJECT_OBSERVER_ARGS)
                .desc(PROJECT_OBSERVER_ARGS_DESCRIPTION)
                .hasArgs()
                .valueSeparator('=')
                .build());

        options.addOption(Option.builder(PROJECT_OBSERVER_NAME_SHORT)
                .longOpt(PROJECT_OBSERVER_NAME)
                .desc(PROJECT_OBSERVER_NAME_DESCRIPTION)
                .numberOfArgs(1)
                .build());

        options.addOption(Option.builder(PROJECT_NOTIFIER_NAMES_SHORT)
                .longOpt(PROJECT_NOTIFIER_NAMES)
                .desc(PROJECT_NOTIFIER_NAMES_DESCRIPTION)
                .hasArgs()
                .build());

        options.addOption(Option.builder()
                .longOpt(MASTER_WATCHERS)
                .desc(MASTER_WATCHERS_DESCRIPTION)
                .hasArgs()
                .build());
    }

    void printHelp(final PrintStream stream) {
        final HelpFormatter formatter = new HelpFormatter();
        final PrintWriter writer = new PrintWriter(stream);
        formatter.printHelp(writer, HelpFormatter.DEFAULT_WIDTH, "tellon notify", null, options,
                LEFT_PAD, DESC_PAD, null);
        writer.flush();
    }

    String getProjectObserverName() {
        checkParsed();
        return cmd.getOptionValue(PROJECT_OBSERVER_NAME, null);
    }

    Map<String, String> getProjectObserverArgs() {
        checkParsed();
        return Collections.unmodifiableMap(observerArgs);
    }

    Set<String> getEnabledNotifiers() {
        checkParsed();
        return Collections.unmodifiableSet(enabledNotifiers);
    }

    List<String> getMasterWatchers() {
        checkParsed();
        return Collections.unmodifiableList(masterWatchers);
    }

    private void checkParsed() {
        if (cmd == null) {
            throw new IllegalStateException("Arguments not yet parsed");
        }
    }
}
