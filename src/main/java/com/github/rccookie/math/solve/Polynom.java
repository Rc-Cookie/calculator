package com.github.rccookie.math.solve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.SimpleNumber;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;

public interface Polynom extends Expression.Function {

    int degree(int indeterminant);

    default int degree() {
        int d = 0;
        for(int i=0; i<indeterminantCount(); i++)
            d += degree(i);
        return d;
    }

    default int maxDegree() {
        int d = 0;
        for(int i=0; i<indeterminantCount(); i++)
            d = Math.max(d, degree(i));
        return d;
    }

    Expression getCoefficient(int n);

    Expression[] coefficients();

    String[] indeterminants();

    int indeterminantCount();

    Polynom derivative(int indeterminant);

    default Polynom derivative() {
        Polynom derivative = this;
        for(int i=0, indCount=indeterminantCount(); i<indCount; i++)
            derivative = derivative.derivative(i);
        return derivative;
    }

    Polynom antiderivative(int indeterminant);

    default Polynom antiderivative() {
        Polynom antiderivative = this;
        for(int i=0, indCount=indeterminantCount(); i<indCount; i++)
            antiderivative = antiderivative.antiderivative(i);
        return antiderivative;
    }

    default Polynom derivative(int indeterminant, int n) {
        if(n < 0) return antiderivative(indeterminant, -n);
        Polynom d = this;
        for(; n>0; n--)
            d = d.derivative(indeterminant);
        return d;
    }

    default Polynom antiderivative(int indeterminant, int n) {
        if(n < 0)
            return derivative(indeterminant, -n);
        Polynom d = this;
        for(; n>0; n--)
            d = d.antiderivative(indeterminant);
        return d;
    }

    default Number integrate(int indeterminant, SymbolLookup lookup, Number a, Number b, int n) {
        Polynom antiderivative = antiderivative(indeterminant, n);
        return antiderivative.evaluate(lookup, b).subtract(antiderivative.evaluate(lookup, a));
    }

    default Number integrate(int indeterminant, SymbolLookup lookup, Number a, Number b) {
        return integrate(indeterminant, lookup, a, b, 1);
    }

    default Number integrate(SymbolLookup lookup, Number a, Number b) {
        Polynom antiderivative = antiderivative();
        return antiderivative.evaluate(lookup, b).subtract(antiderivative.evaluate(lookup, a));
    }

    @Override
    default String[] paramNames() {
        return indeterminants();
    }

    @Override
    default int paramCount() {
        return indeterminantCount();
    }

    @Override
    default String name() {
        return "polynom";
    }

    @Override
    default int operandCount() {
        return indeterminantCount() + maxDegree();
    }

    @Override
    default Expression[] operands() {
        String[] indeterminants = indeterminants();
        Expression[] coefficients = coefficients();
        Expression[] operands = new Expression[indeterminants.length + coefficients.length];
        int indCount = indeterminantCount();
        for(int i=0; i<indCount; i++)
            operands[i] = Symbol.of(indeterminants[i]);
        for(int i=0; i<coefficients.length; i++) {
            Number operand = coefficients[i];
            for(int j=0; j<indCount; j++)
                operand = operand.multiply(operands[j].raise(i));
            operands[i + indCount] = Expression.of(operand);
        }
        return operands;
    }

    @Override
    default Expression expr() {
        Expression[] operands = operands();
        int indCount = indeterminantCount();
        if(operands.length == indCount)
            return Expression.ZERO();
        Expression expr = operands[operands.length-1];
        for(int i=operands.length - 2; i>=indCount; i--)
            expr = expr.add(operands[i]);
        return expr;
    }



    static Polynom parse(Number x) {
        return parse(SymbolLookup.LOCAL_ONLY, x);
    }

    static Polynom parse(SymbolLookup lookup, Number x) {
        if(x instanceof Polynom p) return p;
        if(!(x instanceof Function f))
            throw new IllegalArgumentException("Illegal polynom expression: function expected");

        String[] indeterminants = f.paramNames();
        if(indeterminants.length == 0) // Constant expression
            return new SimplePolynom(f.expr());
        if(indeterminants.length != 1)
            throw new UnsupportedOperationException("Multi-indeterminant polynoms not supported");

        Map<Integer, Expression> coefficients = new HashMap<>();
        parseSumComponent(f.expr(), lookup, indeterminants[0], coefficients);

        int degree = coefficients.keySet().stream().mapToInt(i->i).max().orElse(-1);
        if(degree == -1) return new SimplePolynom(indeterminants[0]);

        Expression[] coeffs = new Expression[degree+1];
        Arrays.setAll(coeffs, i -> coefficients.getOrDefault(i, Expression.ZERO()));
        return new SimplePolynom(indeterminants[0], coeffs);
    }

    private static void parseSumComponent(Expression sum, SymbolLookup lookup, String indeterminant, Map<Integer, Expression> coefficients) {
        // TODO: Make sum function analyzable and support sum expression
        if(sum.name().equals("+")) {
            parseSumComponent(((BinaryOperation) sum).a(), lookup, indeterminant, coefficients);
            parseSumComponent(((BinaryOperation) sum).b(), lookup, indeterminant, coefficients);
        }
        else parseProduct(sum, lookup, indeterminant, coefficients);
    }

    private static void parseProduct(Expression product, SymbolLookup lookup, String indeterminant, Map<Integer, Expression> coefficients) {
        List<Expression> factors = new ArrayList<>();
        int exp = parseProductComponent(product, lookup, indeterminant, factors);
        if(coefficients.containsKey(exp))
            factors.add(coefficients.get(exp));
        coefficients.put(exp, factors.stream().reduce(Expression::multiply).orElseGet(() -> Expression.of(Number.ONE())));
    }

    private static int parseProductComponent(Expression product, SymbolLookup lookup, String indeterminant, List<Expression> factors) {
        if(product.name().equals("*") || product.name().equals("implicit"))
            return parseProductComponent(((BinaryOperation) product).a(), lookup, indeterminant, factors) +
                   parseProductComponent(((BinaryOperation) product).b(), lookup, indeterminant, factors);

        if(product instanceof Symbol s && s.name().equals(indeterminant))
            return 1;
        if(product.name().equals("^") && ((BinaryOperation) product).a() instanceof Symbol s &&
                s.name().equals(indeterminant)) {
            Number exp = ((BinaryOperation) product).b().evaluate(lookup);
            double dExp;
            if(!(exp instanceof SimpleNumber) || (dExp = exp.toDouble()) != (int) dExp || dExp < 0)
                throw new IllegalArgumentException("Illegal polynom expression: non-natural indeterminant exponent");
            return (int) dExp;
        }
        if(containsIndeterminant(product, indeterminant))
            throw new IllegalArgumentException("Illegal polynom expression: indeterminant not allowed here");
        factors.add(product);
        return 0;
    }

    static boolean containsIndeterminant(Expression expr, String indeterminant) {
        if(expr instanceof Symbol s && s.name().equals(indeterminant))
            return true;
        if(expr instanceof Function f) {
            for(String p : f.paramNames())
                if(p.equals(indeterminant))
                    return false;
        }
        for(Expression e : expr.operands())
            if(containsIndeterminant(e, indeterminant)) return true;
        return false;
    }
}
