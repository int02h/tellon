package com.dpforge.javatree.commands;

import org.apache.commons.cli.*;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

class NotifyArguments {

    private static final String PROJECT_WALKER_ARG = "warg";
    private static final String PROJECT_WALKER_ARG_DESCRIPTION = "Argument string for project walker";

    private static final String PROJECT_WALKER_NAME = "walker";
    private static final String PROJECT_WALKER_NAME_SHORT = "w";
    private static final String PROJECT_WALKER_NAME_DESCRIPTION = "Name of walker to use (if you have multiple)";

    private static final int LEFT_PAD = 2;
    private static final int DESC_PAD = 4;

    private final Options options = new Options();

    private final Map<String, String> walkerArgs = new HashMap<>();

    private CommandLine cmd;

    void parse(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);

        walkerArgs.clear();
        String[] values = cmd.getOptionValues(PROJECT_WALKER_ARG);
        if (values != null && values.length % 2 == 0) {
            for (int i = 0; i < values.length; i += 2) {
                walkerArgs.put(values[i], values[i + 1]);
            }
        }
    }

    NotifyArguments() {
        options.addOption(Option.builder()
                .longOpt(PROJECT_WALKER_ARG)
                .desc(PROJECT_WALKER_ARG_DESCRIPTION)
                .hasArgs()
                .valueSeparator('=')
                .build());

        options.addOption(Option.builder(PROJECT_WALKER_NAME_SHORT)
                .longOpt(PROJECT_WALKER_NAME)
                .desc(PROJECT_WALKER_NAME_DESCRIPTION)
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

    String getProjectWalkerName() {
        checkParsed();
        return cmd.getOptionValue(PROJECT_WALKER_NAME, null);
    }

    Map<String, String> getProjectWalkerArgs() {
        checkParsed();
        return new HashMap<>(walkerArgs);
    }

    private void checkParsed() {
        if (cmd == null) {
            throw new IllegalStateException("Arguments not yet parsed");
        }
    }
}
