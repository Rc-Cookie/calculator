package com.github.rccookie.math.expr;

import java.util.function.BinaryOperator;

import com.github.rccookie.math.Number;

/**
 * First evaluates the first argument. If the evaluated number is equal to
 * the specified constant, it will be returned immediately. Otherwise, the
 * second operand is evaluated and the operator is applied normally.
 *
 * @param name The name of the operation
 * @param format The toString() format including $1 and $2
 * @param a The first operand (always evaluated)
 * @param b The second operand (not always evaluated)
 * @param optimize The value a should have if the operation does not need to
 *                 be performed
 * @param function The operation to perform normally
 */
record OptimizedBinaryOperation(String name,
                                String format,
                                Expression a,
                                Expression b,
                                Number optimize,
                                BinaryOperator<Number> function) implements Expression.BinaryOperation {

    @Override
    public Number evaluate(SymbolLookup lookup) {
        Number ea = a.evaluate(lookup);
        if(ea.equals(optimize)) return ea;
        return function.apply(ea, b.evaluate(lookup));
    }

    @Override
    public String toString() {
        return format.replace("$1", a.toString()).replace("$2", b.toString());
    }
}
