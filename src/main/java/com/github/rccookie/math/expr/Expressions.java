package com.github.rccookie.math.expr;

import java.util.Stack;

final class Expressions {

    private Expressions() { }

    public static Expression append(Stack<? extends Expression> stack) {
        Expression element = stack.pop();
        Expression elements = stack.pop();
        if(elements instanceof Builder b) {
            b.append(element);
            return b;
        }
        return new Builder(elements, element);
    }

    public static Expression buildList(Stack<? extends Expression> stack) {
        Expression x = stack.pop();
        return x instanceof Builder b ? b.buildList() : x;
    }

    public static Expression buildVector(Stack<? extends Expression> stack) {
        Expression x = stack.pop();
        return x instanceof Builder b ? b.buildVector() : x;
    }
}
