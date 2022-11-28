//package com.github.rccookie.math.solve;
//
//import java.util.Arrays;
//import java.util.stream.Stream;
//
//import com.github.rccookie.math.Number;
//import com.github.rccookie.math.expr.Expression;
//import com.github.rccookie.math.expr.SymbolLookup;
//import com.github.rccookie.util.Arguments;
//
//record ComplexPolynom(String[] indeterminants, Number... coefficients) implements Polynom {
//
//    ComplexPolynom(String[] indeterminants, Number... coefficients) {
//        this.indeterminants = Arguments.checkNull(indeterminants, "indeterminants");
//        int d = coefficients.length-1;
//        while(d >= 0 && coefficients[d].equals(Number.ZERO())) d--;
//        if(d <= 0)
//            this.coefficients = new Number[] { Number.ZERO() };
//        else this.coefficients = Arrays.copyOf(coefficients, d+1);
//    }
//
//    ComplexPolynom(Number... coefficients) {
//        this(Stream.iterate(1,i->i+1).limit(coefficients.length).map(i->"x"+i).toArray(String[]::new), coefficients);
//    }
//
//    @Override
//    public Number evaluate(SymbolLookup lookup, Number params) {
//        Number res = coefficients[0];
//        if(coefficients.length == 1)
//            return res;
//        Number x = lookup.get(indeterminants);
//        for(int i=1; i<coefficients.length; i++)
//            res = res.add(x.raise(i));
//        return res;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder str = new StringBuilder();
//        str.append(indeterminants).append(" -> ");
//        for(int i=coefficients.length-1; i>0; i--)
//            if(!coefficients[i].equals(Number.ZERO()))
//                str.append('(').append(coefficients[i]).append(')').append(indeterminants).append('^').append(i).append(" + ");
//        if(str.isEmpty())
//            str.append(coefficients[0]);
//        else if(coefficients[0].equals(Number.ZERO()))
//            str.delete(str.length() - 3, str.length());
//        else str.append(coefficients[0]);
//
//        return str.toString();
//    }
//
//    @Override
//    public int degree(int indeterminant) {
//        Arguments.checkRange(indeterminant, 0, 1);
//        return (coefficients.length == 1 && coefficients[0].equals(Number.ZERO())) ? 0 : coefficients.length + 1;
//    }
//
//    @Override
//    public Number getCoefficient(int n) {
//        Arguments.checkRange(n, 0, null);
//        if(n >= coefficients.length)
//            return Number.ZERO();
//        return coefficients[n];
//    }
//
//    @Override
//    public String[] indeterminants() {
//        return new String[] {indeterminants};
//    }
//
//    @Override
//    public int indeterminantCount() {
//        return 1;
//    }
//
//    @Override
//    public Polynom derivative(int indeterminant) {
//        Arguments.checkRange(indeterminant, 0, 1);
//        if(coefficients.length == 1)
//            return new ComplexPolynom(this.indeterminants);
//        Expression[] derivative = new Expression[coefficients.length - 1];
//        for(int i=0; i<derivative.length; i++)
//            derivative[i] = (Expression) coefficients[i+1].multiply(i);
//        return new ComplexPolynom(this.indeterminants, derivative);
//    }
//
//    @Override
//    public Polynom antiderivative(int indeterminant) {
//        Arguments.checkRange(indeterminant, 0, 1);
//        Expression[] antiderivative = new Expression[coefficients.length + 1];
//        for(int i=1; i<antiderivative.length; i++)
//            antiderivative[i] = (Expression) coefficients[i-1].divide(i);
//        return new ComplexPolynom(this.indeterminants, antiderivative);
//    }
//}
