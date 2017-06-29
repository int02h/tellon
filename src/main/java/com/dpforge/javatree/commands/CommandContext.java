package com.dpforge.javatree.commands;

import com.dpforge.tellon.core.walker.ProjectWalker;
import com.dpforge.tellon.core.notifier.ChangesNotifier;

import java.io.PrintStream;
import java.util.List;

public class CommandContext {
    private final List<ProjectWalker> walkers;
    private final List<ChangesNotifier> notifiers;
    private final PrintStream log;

    private CommandContext(Builder builder) {
        this.walkers = builder.walkers;
        this.notifiers = builder.notifiers;
        this.log = builder.log;
    }

    public List<ProjectWalker> getWalkers() {
        return walkers;
    }

    public List<ChangesNotifier> getNotifiers() {
        return notifiers;
    }

    public PrintStream getLog() {
        return log;
    }

    public static class Builder {
        private List<ProjectWalker> walkers;
        private List<ChangesNotifier> notifiers;
        private PrintStream log;

        public Builder walkers(final List<ProjectWalker> walkers) {
            this.walkers = walkers;
            return this;
        }

        public Builder notifiers(final List<ChangesNotifier> notifiers) {
            this.notifiers = notifiers;
            return this;
        }

        public Builder log(final PrintStream log) {
            this.log = log;
            return this;
        }

        public CommandContext build() {
            if (walkers == null || notifiers == null || log == null) {
                throw new IllegalArgumentException("Incomplete data for command context");
            }
            return new CommandContext(this);
        }
    }
}
