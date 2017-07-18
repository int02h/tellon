package com.dpforge.tellon.app.commands;

import com.dpforge.tellon.core.notifier.ProjectNotifier;
import com.dpforge.tellon.core.observer.ProjectObserver;

import java.io.PrintStream;
import java.util.List;

public class CommandContext {
    private final List<ProjectObserver> observers;
    private final List<ProjectNotifier> notifiers;
    private final PrintStream log;

    private CommandContext(Builder builder) {
        this.observers = builder.observers;
        this.notifiers = builder.notifiers;
        this.log = builder.log;
    }

    public List<ProjectObserver> getObservers() {
        return observers;
    }

    public List<ProjectNotifier> getNotifiers() {
        return notifiers;
    }

    public PrintStream getLog() {
        return log;
    }

    public static class Builder {
        private List<ProjectObserver> observers;
        private List<ProjectNotifier> notifiers;
        private PrintStream log;

        public Builder observers(final List<ProjectObserver> observers) {
            this.observers = observers;
            return this;
        }

        public Builder notifiers(final List<ProjectNotifier> notifiers) {
            this.notifiers = notifiers;
            return this;
        }

        public Builder log(final PrintStream log) {
            this.log = log;
            return this;
        }

        public CommandContext build() {
            if (observers == null || notifiers == null || log == null) {
                throw new IllegalArgumentException("Incomplete data for command context");
            }
            return new CommandContext(this);
        }
    }
}
