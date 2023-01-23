package com.github.rccookie.math.expr;

/**
 * Thrown to indicate that a math expression could not be evaluated because it
 * has semantic errors, for example a reference to a non-existing variable.
 */
public class MathEvaluationException extends ArithmeticException {

    public MathEvaluationException() {
        super();
    }

    public MathEvaluationException(String message) {
        super(message);
    }
}
