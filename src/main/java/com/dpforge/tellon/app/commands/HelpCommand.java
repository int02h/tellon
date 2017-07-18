package com.dpforge.tellon.app.commands;

import java.io.PrintStream;

public class HelpCommand extends Command {

    private String helpCommand;

    HelpCommand() {
    }

    @Override
    public boolean parseArguments(final String[] args) {
        if (args.length == 1) {
            helpCommand = args[0];
        }
        return true;
    }

    @Override
    public void printHelp(final PrintStream stream) {
        stream.println("usage: tellon help command");
        stream.println("Where 'command' is the name of the command you want to know about");
    }

    @Override
    public void execute(final CommandContext context) throws CommandExecutionException {
        final PrintStream log = context.getLog();

        if (helpCommand != null) {
            final Command command = CommandFactory.create(helpCommand);
            command.printHelp(log);
        } else {
            log.println("usage: tellon <command> [<args>]");
            log.println();
            log.println("These command are supported:");
            log.println("  notify    Notify watchers about source code changes");
            log.println("  verify    Verify the environment and application settings");
            log.println("  help      Print this help");
            log.println();
            log.println("You can get detailed information about any command with the following:");
            log.println("  tellon help command");
            log.println("  tellon command --help");
            log.println("Where 'command' is the name of the command listed above");
        }
    }
}
