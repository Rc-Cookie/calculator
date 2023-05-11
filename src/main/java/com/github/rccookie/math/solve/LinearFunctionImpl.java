package com.github.rccookie.math.solve;

import java.util.Arrays;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.MathExpressionSyntaxException;
import com.github.rccookie.math.rendering.RenderableExpression;

record LinearFunctionImpl(String[] indeterminants, Expression[] coefficients, Expression offset) implements LinearFunction {

    LinearFunctionImpl(String[] indeterminants, Expression[] coefficients, Expression offset) {
        if(indeterminants.length != coefficients.length)
            throw new MathExpressionSyntaxException("Indeterminant count must be equal to coefficient count");
        this.indeterminants = indeterminants.clone();
        this.coefficients = coefficients.clone();
        this.offset = offset;
    }

    LinearFunctionImpl(Expression value) {
        this(new String[0], new Expression[0], value);
    }

    @Override
    public LinearFunction simplify() {
        Expression[] coeffs = Arrays.stream(coefficients()).map(Expression::simplify).toArray(Expression[]::new);
        if(Arrays.stream(coeffs).allMatch(Number.ZERO()::equals))
            return new LinearFunctionImpl(offset.simplify());
        return new LinearFunctionImpl(indeterminants, coeffs, offset.simplify());
    }

    @Override
    public int indeterminantCount() {
        return indeterminants.length;
    }

    @Override
    public String[] indeterminants() {
        return indeterminants.clone();
    }

    @Override
    public Expression[] coefficients() {
        return coefficients.clone();
    }

    @Override
    public RenderableExpression toRenderable() {
        return null;
    }
}
