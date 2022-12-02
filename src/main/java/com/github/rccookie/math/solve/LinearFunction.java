package com.github.rccookie.math.solve;

import java.util.Arrays;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Vector;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;

public interface LinearFunction extends Expression.Function {

    int indeterminantCount();

    String[] indeterminants();

    Expression[] coefficients();

    Expression offset();

    @Override
    LinearFunction simplify();

    @Override
    default int paramCount() {
        return indeterminantCount();
    }

    @Override
    default String[] paramNames() {
        return indeterminants();
    }

    @Override
    default Expression expr() {
        return new LinearFunctionExpr(this);
    }

    @Override
    default String name() {
        return "linear";
    }

    @Override
    default int operandCount() {
        return indeterminantCount() * 2 + 1;
    }

    @Override
    default Expression[] operands() {
        String[] indeterminants = indeterminants();
        Expression[] coefficients = coefficients();
        Expression[] operands = new Expression[indeterminants.length * 2 + 1];
        int indCount = indeterminants.length;

        for(int i=0; i<indCount; i++)
            operands[i] = Symbol.of(indeterminants[i]);

        for(int i=0; i<coefficients.length; i++)
            operands[i + indCount] = coefficients[i].multiply(operands[i]);

        operands[operands.length-1] = offset();
        return operands;
    }

    @Override
    default Number evaluate(SymbolLookup lookup, Number params) {
        Expression[] coeffs = coefficients();
        int indCount = coeffs.length;
        if(indCount == 0) {
            if(!(params instanceof Numbers n) || n.size() != 0)
                throw new IllegalArgumentException("No parameters expected for function call");
            return offset().evaluate(lookup);
        }
        if(indCount == 1) {
            if(params instanceof Numbers n)
                return new Vector(n.stream().map(e -> evaluate(lookup, e)).toArray(Number[]::new));
            return params.multiply(coefficients()[0]).add(offset());
        }
        Number[] ps = new Number[indCount];
        Arrays.fill(ps, Expression.UNSPECIFIED());

        if(params instanceof Numbers n)
            for(int i=0; i<ps.length; i++)
                ps[i] = n.get(i);
        else ps[0] = params;

        Number result = offset();
        for(int i=0; i<indCount; i++)
            result = result.add(coeffs[i].evaluate(lookup).multiply(ps[i]));
        return result;
    }



    static LinearFunction parse(Number x) {
        if(x instanceof LinearFunction l) return l;
        if(!(x instanceof Function f))
            throw new IllegalArgumentException("Illegal linear expression: function expected");

        String[] indeterminants = f.paramNames();
        if(indeterminants.length == 0) // Constant expression
            return new LinearFunctionImpl(f.expr().simplify());
        return null;
    }
}
