package com.github.rccookie.math.calculator;

import com.github.rccookie.json.JsonArray;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonSerializable;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

record Version(int a, int b, int c) implements Comparable<Version>, JsonSerializable {

    static {
        JsonDeserialization.register(Version.class, json -> new Version(json.get(0).asInt(), json.get(1).asInt(), json.get(2).asInt()));
    }

    Version {
        Arguments.checkRange(a, 0, null);
        Arguments.checkRange(b, 0, null);
        Arguments.checkRange(c, 0, null);
    }

    @Override
    public int compareTo(@NotNull Version o) {
        if(a != o.a) return a < o.a ? -1 : 1;
        if(b != o.b) return b < o.b ? -1 : 1;
        if(c != o.c) return c < o.c ? -1 : 1;
        return 0;
    }

    @Override
    public String toString() {
        return a + "." + b + "." + c;
    }

    public static Version parse(String str) {
        String[] abc = str.split("\\.");
        if(abc.length != 3)
            throw new IllegalArgumentException("Expected xxx.xxx.xxx");
        return new Version(
                Integer.parseInt(abc[0]),
                Integer.parseInt(abc[1]),
                Integer.parseInt(abc[2])
        );
    }

    @Override
    public Object toJson() {
        return new JsonArray(a, b, c);
    }
}
