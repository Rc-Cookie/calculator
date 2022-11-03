package com.github.rccookie.math;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public final class Unit {


    public static final Unit ONE       = new Unit();
    public static final Unit KILOGRAMM = new Unit(SiUnit.MASS);
    public static final Unit METER     = new Unit(SiUnit.LENGTH);
    public static final Unit SECOND    = new Unit(SiUnit.TIME);
    public static final Unit AMPERE    = new Unit(SiUnit.CURRENT);
    public static final Unit KELVIN    = new Unit(SiUnit.TEMPERATURE);
    public static final Unit MOL       = new Unit(SiUnit.AMOUNT);
    public static final Unit CANDELA   = new Unit(SiUnit.LUMINANCE);


    private final byte[] units = new byte[7];

    public Unit(SiUnit... siUnits) {
        this(siUnits, new SiUnit[0]);
    }

    public Unit(SiUnit[] upperUnits, SiUnit[] lowerUnits) {
        for(SiUnit unit : upperUnits)
            units[unit.ordinal()]++;
        for(SiUnit unit : lowerUnits)
            units[unit.ordinal()]--;
    }

    private Unit() { }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Unit u && Arrays.equals(units, u.units);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(units);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int scale) {
        StringBuilder upper = new StringBuilder(), lower = new StringBuilder();

        for(int i=0; i<7; i++)
            if(units[i] != 0)
                appendString(units[i] > 0 ? upper : lower, i);

        if(upper.isEmpty()) upper.append(1);
        else upper.deleteCharAt(0);

        if(lower.isEmpty()) return upper.toString();
        lower.deleteCharAt(0);

        return upper.append('/').append(lower).toString();
    }

    private void appendString(StringBuilder str, int u) {
        if(units[u] == 0) return;
        str.append('*');
        int c = Math.abs(units[u]);
        str.append(SiUnit.get(u).getSymbol());
        if(c != 1) appendSuperscript(str, c);
    }

    private static void appendSuperscript(StringBuilder str, int x) {
        String s = x + "";
        for(int i=0; i<s.length(); i++) str.append(switch(s.charAt(i)) {
            case '0' -> '\u2070';
            case '1' -> '\u00B9';
            case '2' -> '\u00B2';
            case '3' -> '\u00B3';
            case '4' -> '\u2074';
            case '5' -> '\u2075';
            case '6' -> '\u2076';
            case '7' -> '\u2077';
            case '8' -> '\u2078';
            case '9' -> '\u2079';
            case '-' -> '\u207B';
            default -> s.charAt(i);
        });
    }

    @NotNull
    public Unit add(Unit u) {
        if(!equals(u))
            throw new IllegalArgumentException("Cannot add values because [" + this + "] != [" + u + "]");
        return this;
    }

    @NotNull
    public Unit subtract(Unit u) {
        if(!equals(u))
            throw new IllegalArgumentException("Cannot subtract values because [" + this + "] != [" + u + "]");
        return this;
    }

    @NotNull
    public Unit multiply(Unit u) {
        Unit x = new Unit();
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] + u.units[i]);
        return x;
    }

    @NotNull
    public Unit divide(Unit u) {
        Unit x = new Unit();
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] + u.units[i]);
        return x;
    }

    @NotNull
    public Unit invert() {
        Unit x = new Unit();
        for(int i=0; i<7; i++)
            x.units[i] = (byte) -units[i];
        return x;
    }

    @NotNull
    public Unit raise(int exp) {
        if(exp == 0) return ONE;
        if(exp == 1) return this;
        Unit x = new Unit();
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] * exp);
        return x;
    }
}
