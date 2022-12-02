package com.github.rccookie.math.expr;

public final class Precedence {

    private Precedence() { }


    public static final int MIN = Integer.MIN_VALUE;
    public static final int MAX = Integer.MAX_VALUE;

    public static final int COMMA = -50;
    public static final int LEFT_PARENTHESIS = -99;
    public static final int RIGHT_PARENTHESIS = 100;
    public static final int LEFT_BRACKET = LEFT_PARENTHESIS;
    public static final int RIGHT_BRACKET = RIGHT_PARENTHESIS;

    public static final int PLUS = 10;
    public static final int MINUS = PLUS;
    public static final int MULTIPLY = 20;
    public static final int DIVIDE = MULTIPLY;
    public static final int POWER = 30;

    public static final int NEGATE = 11;
    public static final int FACTORIAL = 40;

    public static final int DEGREE = 120;
    public static final int PERCENT = DEGREE;
    public static final int SQUARE = POWER;
    public static final int CUBE = POWER;

    public static final int DEFINE = 0;
    public static final int LAMBDA = 1;

    public static final int EQUALS = 5;
    public static final int LESS = EQUALS;
    public static final int LESS_OR_EQUAL = EQUALS;
    public static final int GREATER = EQUALS;
    public static final int GREATER_OR_EQUAL = EQUALS;

    public static final int IMPLICIT = MULTIPLY;
    public static final int FUNCTION_CALL = IMPLICIT;
}
