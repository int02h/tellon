package com.dpforge.tellon.parser;

import java.util.Arrays;

public class WatcherList {
    private final String[] watchers;

    public WatcherList(String[] watchers) {
        this.watchers = watchers;
    }

    public int size() {
        return watchers.length;
    }

    public String get(int index) {
        return watchers[index];
    }

    @Override
    public String toString() {
        return Arrays.toString(watchers);
    }
}
