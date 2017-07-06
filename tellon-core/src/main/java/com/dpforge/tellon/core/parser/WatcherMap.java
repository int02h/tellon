package com.dpforge.tellon.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WatcherMap extends HashMap<String, List<String>> {
    public String get(final String key, final int index) {
        return get(key).get(index);
    }

    public List<String> getUnmodifiable(final String key) {
        return Collections.unmodifiableList(super.get(key));
    }

    List<String> put(final String key, final String singleWatcher) {
        return super.put(key, Collections.singletonList(singleWatcher));
    }
}
