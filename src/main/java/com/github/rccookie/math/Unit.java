package com.github.rccookie.math;

import java.util.Arrays;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public final class Unit {


    public static final Unit ONE       = new Unit();
    public static final Unit KILOGRAMM = new Unit(1, SiUnit.MASS);
    public static final Unit METER     = new Unit(SiUnit.LENGTH);
    public static final Unit SECOND    = new Unit(SiUnit.TIME);
    public static final Unit AMPERE    = new Unit(SiUnit.CURRENT);
    public static final Unit KELVIN    = new Unit(SiUnit.TEMPERATURE);
    public static final Unit MOL       = new Unit(SiUnit.AMOUNT);
    public static final Unit CANDELA   = new Unit(SiUnit.LUMINANCE);


    private final byte[] units = new byte[7];
    private final byte scale;

    public Unit(SiUnit... siUnits) {
        this(0, siUnits);
    }

    public Unit(int scale, SiUnit... siUnits) {
        this(scale, siUnits, new SiUnit[0]);
    }

    public Unit(int scale, SiUnit[] upperUnits, SiUnit[] lowerUnits) {
        this(scale);
        for(SiUnit unit : upperUnits)
            units[unit.ordinal()]++;
        for(SiUnit unit : lowerUnits)
            units[unit.ordinal()]--;
    }

    private Unit(int scale, byte[] units) {
        this(scale);
        System.arraycopy(units, 0, this.units, 0, 7);
    }

    private Unit(int scale) {
        this.scale = (byte) scale;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Unit u && scale == u.scale && Arrays.equals(units, u.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(units), scale);
    }

    @Override
    public String toString() {
        StringBuilder upper
    }

    @NotNull
    public Unit scale(int exp) {
        return exp == 0 ? this : new Unit(scale + exp, units);
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
        Unit x = new Unit(scale + u.scale);
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] + u.units[i]);
        return x;
    }

    @NotNull
    public Unit divide(Unit u) {
        Unit x = new Unit(scale + u.scale);
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] + u.units[i]);
        return x;
    }

    @NotNull
    public Unit invert() {
        Unit x = new Unit(scale);
        for(int i=0; i<7; i++)
            x.units[i] = (byte) -units[i];
        return x;
    }

    @NotNull
    public Unit raise(int exp) {
        if(exp == 0) return ONE;
        if(exp == 1) return this;
        Unit x = new Unit(scale * exp);
        for(int i=0; i<7; i++)
            x.units[i] = (byte) (units[i] * exp);
        return x;
    }
}
