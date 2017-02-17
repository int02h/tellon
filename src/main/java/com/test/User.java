package com.test;

import com.dpforge.tellon.annotations.NotifyChanges;
import com.sun.istack.internal.NotNull;

public class User {
    @NotifyChanges("firstName")
    private String firstName;

    @NotifyChanges("lastName")
    @NotNull
    @Deprecated
    private String lastName = "Ivanoff", coo = "Coo";

    @NotifyChanges("qwe")
    @Override
    public String toString() {
        return super.toString();
    }
}
