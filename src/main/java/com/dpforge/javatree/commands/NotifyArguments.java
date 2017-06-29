package com.dpforge.javatree.commands;

import org.apache.commons.cli.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class NotifyArguments {

    private static final String PROJECT_OBSERVER_ARGS = "observer-args";
    private static final String PROJECT_OBSERVER_ARGS_DESCRIPTION = "Arguments for project observer in the form of 'key=value'";

    private static final String PROJECT_OBSERVER_NAME = "observer";
    private static final String PROJECT_OBSERVER_NAME_SHORT = "o";
    private static final String PROJECT_OBSERVER_NAME_DESCRIPTION = "Name of project observer to use (if you have multiple)";

    private static final int LEFT_PAD = 2;
    private static final int DESC_PAD = 4;

    private final Options options = new Options();

    private final Map<String, String> observerArgs = new HashMap<>();

    private CommandLine cmd;

    void parse(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);

        observerArgs.clear();
        String[] values = cmd.getOptionValues(PROJECT_OBSERVER_ARGS);
        if (values != null && values.length % 2 == 0) {
            for (int i = 0; i < values.length; i += 2) {
                observerArgs.put(values[i], values[i + 1]);
            }
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

    private void checkParsed() {
        if (cmd == null) {
            throw new IllegalStateException("Arguments not yet parsed");
        }
    }
}
