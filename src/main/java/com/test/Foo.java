package com.test;

import com.dpforge.tellon.annotations.*;

@NotifyChanges({"mailto:d.popov@corp.mail", "icq:449221964"})
public class Foo {
    static String CONST1 = "";

    @com.dpforge.tellon.annotations.NotifyChanges("test")
    static String CONST2 = "";

    @NotifyChanges("test2")
    private final int someField = 123, abc = -123;

    private String string;

    public Foo() {

    }

    @NotifyChanges("test3")
    public Foo(int a) {

    }

    @com.dpforge.tellon.annotations.NotifyChanges("test4")
    public void bar() {
        int a = 123;
        int b = -456;
        System.out.println(a + b);
    }

    public void test() {

    }

    public void notAnnotated() {

    }

    @NotifyChanges("non-stastic@inner")
    public class NonStaticInnerFoo {
        @NotifyChanges("non-stastic@inner")
        private int nonStaticInnerInt;
    }

    @NotifyChanges("stastic@inner")
    public static class StaticInnerFoo {
        @NotifyChanges("stastic@inner")
        private int staticInnerInt;
    }
}
