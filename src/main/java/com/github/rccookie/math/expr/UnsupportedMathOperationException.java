package com.github.rccookie.math.expr;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.github.rccookie.math.Number;

/**
 * Thrown to indicate that a specific operation is not supported, because
 * it is not implemented or arithmetically possible, particularly due to
 * the type of parameter, for example an unsupported {@link Number}
 * implementation, or missing support for negative parameters.
 */
public class UnsupportedMathOperationException extends MathEvaluationException {

    public UnsupportedMathOperationException(String context, Number... params) {
        super(params.length == 0 ? context : "'" + context + "' unsupported for parameter" + (params.length==1?"":"s") + " because of number type: " + Arrays.stream(params).map(Number::toString).collect(Collectors.joining(", ")));
    }
}
