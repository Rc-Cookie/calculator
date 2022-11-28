package com.github.rccookie.math.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;

public interface SymbolLookup {

    Number UNSPECIFIED = new Rational(0);
    Expression UNSPECIFIED_EXPR = Expression.of(UNSPECIFIED);


    SymbolLookup LOCAL_ONLY = new SymbolLookup() {
        final Map<String, Stack<Number>> localVariables = new HashMap<>();
        @Override
        public void pushLocal(String name, Number var) {
            localVariables.computeIfAbsent(name, n -> new Stack<>()).push(var);
        }

        @Override
        public void popLocal(String name) {
            Stack<Number> localVars = localVariables.get(name);
            localVars.pop();
            if(localVars.isEmpty())
                localVariables.remove(name);
        }
        @Override
        public Number get(String name) {
            throw new IllegalArgumentException("Unknown variable: " + name);
        }
        @Override
        public void put(String name, Number value) { }
    };


    Number get(String name);

    void put(String name, Number value);

    void pushLocal(String name, Number value);

    void popLocal(String name);
}
