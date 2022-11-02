package com.github.rccookie.math.interpreter;

import java.util.Stack;

import com.github.rccookie.math.Number;

final class Expressions {

    private Expressions() { }

    //#region Lists, Vectors

    public static Number append(Stack<? extends Number> stack) {
        Number element = stack.pop();
        Number elements = stack.pop();
        if(elements instanceof Builder b) {
            b.append(element);
            return b;
        }
        return new Builder(elements, element);
    }

    public static Number buildList(Stack<? extends Number> stack) {
        Number x = stack.pop();
        return x instanceof Builder b ? b.buildList() : x;
    }

    public static Number buildVector(Stack<? extends Number> stack) {
        Number x = stack.pop();
        return x instanceof Builder b ? b.buildVector() : x;
    }

    //#endregion

    //#region Declarations

    public static Expression variable(String name) {
        return new Variable(name);
    }

    public static Expression define(Stack<? extends Number> stack) {
        Number expr = stack.pop();
        Number signature = stack.pop();

        if(signature instanceof Token.Variable v)
            return new VariableDefinition(v.name(), expr);
        FunctionOrMultiply function = (FunctionOrMultiply) signature;
        return new FunctionDefinition(((Token.Variable) function.functionOrNum).name(), parseFunction(function.argument, expr));
    }

    public static Expression lambda(Stack<? extends Number> stack) {
        Number expr = stack.pop();
        Number signature = stack.pop();

        if(signature instanceof Token.Variable v)
            return new Function(expr, v.name());
        return parseFunction(signature, expr);
    }

    private static Function parseFunction(Number paramList, Number expr) {
        Number[] params;
        if(paramList instanceof List list)
            params = list.elements();
        else params = new Number[]{ paramList };

        String[] paramNames = new String[params.length];
        for (int i = 0; i < params.length; i++)
            paramNames[i] = ((Token.Variable) params[i]).name();

        return new Function(expr, paramNames);
    }

    private record VariableDefinition(String name, Number expr) implements Expression {
        @Override
        public Number evaluate(Calculator c) {
            Number value = Expression.evaluate(expr, c);
            c.addVar(name, value);
            return value;
        }
    }

    private record FunctionDefinition(String name, Function expr) implements Expression {
        @Override
        public Number evaluate(Calculator c) {
            c.addVar(name, expr);
            return expr;
        }
    }

    //#endregion

    public static Expression callFunctionOrMultiply(String varName, Number argument) {
        return callFunctionOrMultiply(variable(varName), argument);
    }

    public static Expression callFunctionOrMultiply(Number numOrFunction, Number argument) {
        return new FunctionOrMultiply(numOrFunction, argument);
    }

    public static Expression functionCallOrMultiply(Stack<? extends Number> stack) {
        Number x = stack.pop(), f = stack.pop();
        return new FunctionOrMultiply(f,x);
    }



    private record Variable(String name) implements Expression {
        @Override
        public Number evaluate(Calculator calculator) {
            return calculator.getVar(name);
        }
    }

    private record FunctionOrMultiply(Number functionOrNum, Number argument) implements Expression {
        @Override
        public Number evaluate(Calculator c) {
            Number var = Expression.evaluate(functionOrNum, c);
            Number argVal = Expression.evaluate(argument, c);
            if(var instanceof Function f && !(argVal instanceof Function))
                return f.evaluate(c, argVal);
            return var.multiply(argVal);
        }

        @Override
        public String toString() {
            return "(" + functionOrNum + ")(" + argument + ")";
        }
    }
}
