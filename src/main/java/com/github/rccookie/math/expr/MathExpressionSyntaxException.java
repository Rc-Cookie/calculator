package com.github.rccookie.math.expr;

/**
 * Thrown to indicate that a math expression could not be parsed due
 * to syntactical errors, for example a missing operand before an
 * operator or a vector without any components.
 */
public class MathExpressionSyntaxException extends IllegalArgumentException {

    public MathExpressionSyntaxException() {
    }

    public MathExpressionSyntaxException(String s) {
        super(s);
    }

    public MathExpressionSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public MathExpressionSyntaxException(Throwable cause) {
        super(cause);
    }
}
