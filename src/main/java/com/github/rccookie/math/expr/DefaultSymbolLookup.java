package com.github.rccookie.math.expr;


import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Arguments;

public class DefaultSymbolLookup implements SymbolLookup {

    private final Map<String, Number> variables = new HashMap<>();
    private final Map<String, Stack<Number>> localVariables = new HashMap<>();


    @Override
    public Number get(String name) {
        Stack<Number> localVars = localVariables.get(name);
        if(localVars != null) return localVars.peek();

        Number var = variables.get(name);
        if(var == null)
            throw new IllegalArgumentException("Unknown variable or function: '" + name + "'");
        return var;
    }

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
    public void put(String name, Number var) {
        variables.put(name, Arguments.checkNull(var, "var"));
    }
}
