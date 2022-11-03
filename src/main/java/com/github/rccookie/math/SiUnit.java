package com.github.rccookie.math;

import java.util.Map;

import com.github.rccookie.util.Utils;

public enum SiUnit {
    MASS("g"),
    TIME("s"),
    LENGTH("m"),
    CURRENT("A"),
    TEMPERATURE("K"),
    AMOUNT("mol"),
    LUMINANCE("cd");

    private static final SiUnit[] UNITS = values();
    private static final Map<Integer,String> PREFIXES = Utils.map(
            -24, "y",
            -21, "z",
            -18, "a",
            -15, "f",
            -12, "p",
            -9,  "n",
            -6,  "\u00B5",
            -3,  "m",
            0,   "",
            3,   "k",
            6,   "M",
            9,   "G",
            12,  "T",
            15,  "P",
            18,  "E",
            21,  "Z",
            24,  "Y"
    );

    private final String symbol;

    SiUnit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        StringBuilder str = new StringBuilder();
        appendSymbol(str);
        return str.toString();
    }

    public void appendSymbol(StringBuilder str) {
        appendSymbol(str, 0);
    }

    public int appendSymbol(StringBuilder str, int scale) {
        if(this == MASS) scale += 3;
        if(scale == 0) {
            str.append(symbol);
            return 0;
        }
        if(this == LENGTH) {
            if(scale > 0) {
                str.append("m");
                return scale;
            }
            if(scale == -2) {
                str.append("cm");
                return 0;
            }
            if(scale == -1) {
                str.append("cm");
                return 1;
            }
        }
        else if(this == MASS && scale >= 3) {
            str.append("kg");
            return scale - 3;
        }
        else if((this == TIME || this == CURRENT || this == TEMPERATURE) && scale > 0) {
            str.append(symbol);
            return scale;
        }
        int prefixScale = scale > 0 ?
                Math.min((scale / 3) * 3, 24) :
                Math.max(Math.ceilDiv(-scale, 3) * -3, -24);
        str.append(PREFIXES.get(prefixScale)).append(symbol);
        return scale - prefixScale;
    }


    public static SiUnit get(int index) {
        return UNITS[index];
    }
}
