package com.github.rccookie.math.calculator;

/**
 * Thrown to indicate that an unknown command was used, or an existing
 * command was used with illegal parameters.
 */
public class IllegalCommandException extends IllegalArgumentException {

    public IllegalCommandException() {
        super();
    }

    public IllegalCommandException(String message) {
        super(message);
    }

    public IllegalCommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCommandException(Throwable cause) {
        super(cause);
    }
}
