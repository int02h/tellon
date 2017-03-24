package com.dpforge.javatree;

import org.apache.commons.cli.*;

class Arguments {
    private static final String APP_NAME = "tellon";

    private static final String PROJECT_WALKER_ARGS = "walker-args";
    private static final String PROJECT_WALKER_ARGS_DESCRIPTION = "Argument string for project walker";

    private static final String PROJECT_WALKER_NAME = "walker";
    private static final String PROJECT_WALKER_NAME_SHORT = "w";
    private static final String PROJECT_WALKER_NAME_DESCRIPTION = "Name of walker to use (if you have multiple)";

    private final Options options = new Options();

    private CommandLine cmd;

    Arguments() {
        Option opt;

        opt = new Option(
                null,
                PROJECT_WALKER_ARGS,
                true,
                PROJECT_WALKER_ARGS_DESCRIPTION);
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option(
                PROJECT_WALKER_NAME_SHORT,
                PROJECT_WALKER_NAME,
                true,
                PROJECT_WALKER_NAME_DESCRIPTION);
        opt.setRequired(false);
        options.addOption(opt);
    }

    void parse(String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
    }

    void printHelp() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(APP_NAME, options);
    }

    String getProjectWalkerArgs() {
        checkParsed();
        return cmd.getOptionValue(PROJECT_WALKER_ARGS, "");
    }

    String getProjectWalkerName() {
        checkParsed();
        return cmd.getOptionValue(PROJECT_WALKER_NAME, null);
    }

    private void checkParsed() {
        if (cmd == null) {
            throw new IllegalStateException("Arguments not yet parsed");
        }
    }
}
