package com.github.rccookie.math;

import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonSerializable;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.math.rendering.RenderableExpression;

import org.jetbrains.annotations.NotNull;

public interface Number extends JsonSerializable {

    @NotNull static SimpleNumber ZERO() { return Rational.ZERO; }
    @NotNull static SimpleNumber ONE() { return Rational.ONE; }
    @NotNull static SimpleNumber MINUS_ONE() { return Rational.MINUS_ONE; }
    @NotNull static SimpleNumber TWO() { return Rational.TWO; }
    @NotNull static SimpleNumber HALF() { return Rational.HALF; }
    @NotNull static SimpleNumber ABOUT_ZERO() { return Rational.ABOUT_ZERO; }
    @NotNull static SimpleNumber ABOUT_ONE() { return Rational.ABOUT_ONE; }
    @NotNull static SimpleNumber PI() { return Rational.PI; }
    @NotNull static SimpleNumber E() { return Rational.E; }

    @NotNull static Complex I() { return Complex.I; }

    @NotNull static SimpleNumber RAD_TO_DEG() { return Rational.RAD_TO_DEG; }
    @NotNull static SimpleNumber DEG_TO_RAD() { return Rational.DEG_TO_RAD; }

    @NotNull static SimpleNumber TRUE() { return ONE(); }
    @NotNull static SimpleNumber FALSE() { return ZERO(); }



    Object _null = registerJson();
    private static Object registerJson() {
        JsonDeserialization.register(Number.class, json -> {
            if(json.isArray()) return json.as(Vector.class);
            if(json.isNumber()) return json.as(SimpleNumber.class);
            if(json.isString()) {
                Expression expr = json.as(Expression.class).simplify();
                return expr instanceof Expression.Constant c ? c.value() : expr;
            }
            if(json.containsKey("re")) return json.as(Complex.class);
//            if(json.containsKey("n") || json.containsKey("factor") || json.containsKey("precise"))
            return json.as(Rational.class);
        });
        return null;
    }


    boolean isZero();

    boolean isOne();


    @NotNull
    Number add(Number x);

    @NotNull
    default Number add(long x) {
        return add(new Rational(x));
    }

    @NotNull
    default Number add(double x) {
        return add(new Rational(x));
    }

    @NotNull
    Number subtract(Number x);

    @NotNull
    default Number subtract(long x) {
        return subtract(new Rational(x));
    }

    @NotNull
    default Number subtract(double x) {
        return subtract(new Rational(x));
    }

    @NotNull
    Number subtractFrom(Number x);

    @NotNull
    default Number subtractFrom(long x) {
        return subtractFrom(new Rational(x));
    }

    @NotNull
    default Number subtractFrom(double x) {
        return subtractFrom(new Rational(x));
    }

    @NotNull
    Number multiply(Number x);

    @NotNull
    default Number multiply(long x) {
        return multiply(new Rational(x));
    }

    @NotNull
    default Number multiply(double x) {
        return multiply(new Rational(x));
    }

    @NotNull
    Number divide(Number x);

    @NotNull
    default Number divide(long x) {
        return divide(new Rational(x));
    }

    @NotNull
    default Number divide(double x) {
        return divide(new Rational(x));
    }

    @NotNull
    Number divideOther(Number x);

    @NotNull
    default Number divideOther(long x) {
        return divideOther(new Rational(x));
    }

    @NotNull
    default Number divideOther(double x) {
        return divideOther(new Rational(x));
    }

    @NotNull
    Number raise(Number x);

    @NotNull
    default Number raise(long x) {
        return raise(new Rational(x));
    }

    @NotNull
    default Number raise(double x) {
        return raise(new Rational(x));
    }

    @NotNull
    Number raiseOther(Number base);

    @NotNull
    default Number raiseOther(long x) {
        return raiseOther(new Rational(x));
    }

    @NotNull
    default Number raiseOther(double x) {
        return raiseOther(new Rational(x));
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
        return ONE().subtract(greaterThan(x));
    }

    default Number greaterThanOrEqual(Number x) {
        return ONE().subtract(lessThan(x));
    }



//    @NotNull
//    static Number parse(String str) {
//        if(str.startsWith("[") || str.contains(",")) {
//            if(str.charAt(0) == '[' && str.charAt(str.length()-1) == ']')
//                str = str.substring(1, str.length()-1);
//            str = str.replace(" ", "");
//
//            List<Number> components = new ArrayList<>();
//            StringBuilder s = new StringBuilder(str);
//            StringBuilder num = new StringBuilder();
//            int d = 0;
//            while(!s.isEmpty()) {
//                char c = s.charAt(0);
//                s.deleteCharAt(0);
//                if(d == 0 && c == ',') {
//                    if(s.isEmpty())
//                        throw new NumberFormatException("Trailing comma");
//                    components.add(parse(num.toString()));
//                    num.delete(0, num.length());
//                }
//                else {
//                    num.append(c);
//                    if(c == '[') d++;
//                    else if(c == ']' && --d == 0) {
//                        if(!s.isEmpty() && s.charAt(0) != ',')
//                            throw new NumberFormatException("',' expected between vector components");
//                        components.add(parse(num.toString()));
//                        num.delete(0, num.length());
//                    }
//                }
//            }
//            if(!num.isEmpty())
//                components.add(parse(num.toString()));
//            return new Vector(components.toArray(new Number[0]));
//        }
//        if(str.contains("."))
//            return new Real(Double.parseDouble(str));
//        int i = str.indexOf('/');
//        if(i == -1)
//            return new Rational(Long.parseLong(str));
//        return new Rational(Long.parseLong(str, 0, i, 10),
//                Long.parseLong(str, i+1, str.length(), 10));
//    }



    default double toDouble() {
        return toDouble(null);
    }

    double toDouble(SymbolLookup lookup);


    RenderableExpression toRenderable();


    @NotNull
    static SimpleNumber bool(boolean oneOrZero) {
        return oneOrZero ? TRUE() : FALSE();
    }
}
