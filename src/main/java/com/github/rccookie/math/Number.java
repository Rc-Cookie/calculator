package com.github.rccookie.math;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public interface Number {

    @NotNull static Number ZERO() { return Fraction.ZERO; }
    @NotNull static Number ONE() { return Fraction.ONE; }
    @NotNull static Number MINUS_ONE() { return Fraction.MINUS_ONE; }
    @NotNull static Number TWO() { return Fraction.TWO; }
    @NotNull static Number HALF() { return Fraction.HALF; }
    @NotNull static Number ABOUT_ONE() { return Decimal.ABOUT_ONE; }
    @NotNull static Number PI() { return Decimal.PI; }
    @NotNull static Number E() { return Decimal.E; }

    @NotNull static Number RAD_TO_DEG() { return Decimal.RAD_TO_DEG; }
    @NotNull static Number DEG_TO_RAD() { return Decimal.DEG_TO_RAD; }



    @NotNull
    Number add(Number x);

    @NotNull
    default Number add(long x) {
        return add(new Fraction(x));
    }

    @NotNull
    default Number add(double x) {
        return add(new Decimal(x));
    }

    @NotNull
    Number subtract(Number x);

    @NotNull
    default Number subtract(long x) {
        return subtract(new Fraction(x));
    }

    @NotNull
    default Number subtract(double x) {
        return subtract(new Decimal(x));
    }

    @NotNull
    Number subtractFrom(Number x);

    @NotNull
    default Number subtractFrom(long x) {
        return subtractFrom(new Fraction(x));
    }

    @NotNull
    default Number subtractFrom(double x) {
        return subtractFrom(new Decimal(x));
    }

    @NotNull
    Number multiply(Number x);

    @NotNull
    default Number multiply(long x) {
        return multiply(new Fraction(x));
    }

    @NotNull
    default Number multiply(double x) {
        return multiply(new Decimal(x));
    }

    @NotNull
    Number divide(Number x);

    @NotNull
    default Number divide(long x) {
        return divide(new Fraction(x));
    }

    @NotNull
    default Number divide(double x) {
        return divide(new Decimal(x));
    }

    @NotNull
    Number divideOther(Number x);

    @NotNull
    default Number divideOther(long x) {
        return divideOther(new Fraction(x));
    }

    @NotNull
    default Number divideOther(double x) {
        return divideOther(new Decimal(x));
    }

    @NotNull
    Number raise(Number x);

    @NotNull
    default Number raise(long x) {
        return raise(new Fraction(x));
    }

    @NotNull
    default Number raise(double x) {
        return raise(new Decimal(x));
    }

    @NotNull
    Number raiseOther(Number base);

    @NotNull
    default Number raiseOther(long x) {
        return raiseOther(new Fraction(x));
    }

    @NotNull
    default Number raiseOther(double x) {
        return raiseOther(new Decimal(x));
    }

    @NotNull
    default Number sqrt() {
        return raise(HALF());
    }

    @NotNull
    Number abs();

    @NotNull
    Number negate();

    @NotNull
    Number invert();

    @NotNull
    Number equalTo(Number x);

    @NotNull
    Number lessThan(Number x);

    @NotNull
    Number greaterThan(Number x);

    default Number lessThanOrEqual(Number x) {
        return greaterThan(x).negate();
    }

    default Number greaterThanOrEqual(Number x) {
        return lessThan(x).negate();
    }



    @NotNull
    static Number parse(String str) {
        if(str.startsWith("[") || str.contains(",")) {
            if(str.charAt(0) == '[' && str.charAt(str.length()-1) == ']')
                str = str.substring(1, str.length()-1);
            str = str.replace(" ", "");

            List<Number> components = new ArrayList<>();
            StringBuilder s = new StringBuilder(str);
            StringBuilder num = new StringBuilder();
            int d = 0;
            while(!s.isEmpty()) {
                char c = s.charAt(0);
                s.deleteCharAt(0);
                if(d == 0 && c == ',') {
                    if(s.isEmpty())
                        throw new NumberFormatException("Trailing comma");
                    components.add(parse(num.toString()));
                    num.delete(0, num.length());
                }
                else {
                    num.append(c);
                    if(c == '[') d++;
                    else if(c == ']' && --d == 0) {
                        if(!s.isEmpty() && s.charAt(0) != ',')
                            throw new NumberFormatException("',' expected between vector components");
                        components.add(parse(num.toString()));
                        num.delete(0, num.length());
                    }
                }
            }
            if(!num.isEmpty())
                components.add(parse(num.toString()));
            return new Vector(components.toArray(new Number[0]));
        }
        if(str.contains("."))
            return new Decimal(Double.parseDouble(str));
        int i = str.indexOf('/');
        if(i == -1)
            return new Fraction(Long.parseLong(str));
        return new Fraction(Long.parseLong(str, 0, i, 10),
                Long.parseLong(str, i+1, str.length(), 10));
    }



    double toDouble();
}
