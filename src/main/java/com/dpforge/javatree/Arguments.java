package com.dpforge.javatree;

import org.apache.commons.cli.*;

class Arguments {
    private static final String APP_NAME = "tellon";

    private static final String PROJECT_WALKER_ARGS = "walker-args";
    private static final String PROJECT_WALKER_ARGS_DESCRIPTION = "Argument string for project walker";

    private static final String PROJECT_WALKER_NAME = "walker";
    private static final String PROJECT_WALKER_NAME_SHORT = "w";
    private static final String PROJECT_WALKER_NAME_DESCRIPTION = "Name of walker to use (if you have multiple)";

    private static final String VERIFY = "verify";
    private static final String VERIFY_DESCRIPTION = "Show information and environment";

    private final Options options = new Options();

    private CommandLine cmd;

    Arguments() {
        options.addOption(Option.builder()
                .longOpt(PROJECT_WALKER_ARGS)
                .desc(PROJECT_WALKER_ARGS_DESCRIPTION)
                .numberOfArgs(1)
                .build());

        options.addOption(Option.builder(PROJECT_WALKER_NAME_SHORT)
                .longOpt(PROJECT_WALKER_NAME)
                .desc(PROJECT_WALKER_NAME_DESCRIPTION)
                .numberOfArgs(1)
                .build());

        options.addOption(Option.builder()
                .longOpt(VERIFY)
                .desc(VERIFY_DESCRIPTION)
                .build());
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

    boolean hasVerify() {
        checkParsed();
        return cmd.hasOption(VERIFY);
    }

    private void checkParsed() {
        if (cmd == null) {
            throw new IllegalStateException("Arguments not yet parsed");
        }
    }
}
