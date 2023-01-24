package com.github.rccookie.math.expr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Nullable;

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
            throw new MathEvaluationException("Unknown variable: " + name);
        }

        @Override
        public boolean contains(String name) {
            return false;
        }

        @Override
        public void put(String name, Number value) {
            Arguments.checkNull(name, "name");
        }

        @Override
        public Set<Map.Entry<String, Number>> entrySet() {
            return Set.of();
        }
    };


    Number get(String name);

    boolean contains(String name);

    void put(String name, @Nullable Number value);

    default void delete(String name) {
        put(name, null);
    }

    void pushLocal(String name, Number value);

    void popLocal(String name);

    Set<Map.Entry<String, Number>> entrySet();
}
