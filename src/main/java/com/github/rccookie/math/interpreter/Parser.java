package com.github.rccookie.math.interpreter;

import java.util.Stack;

import com.github.rccookie.math.Number;

public class Parser {

    public Expression parse(Iterable<Token> postfix) {
        Stack<Number> stack = new MathStack();
        for(Token t : postfix) stack.push(switch(t) {
            case Token.NumberToken n -> n.value();
            case Token.Variable v -> v;
            case Token.Operator o -> o.apply(stack);
        });
        if(stack.size() > 1) throw new AssertionError();
        return Expression.of(stack.pop());
    }


    static class MathStack extends Stack<Number> {
        @Override
        public synchronized Number peek() {
            if(isEmpty())
                throw new IllegalArgumentException("Expression expected");
            return super.peek();
        }

        @Override
        public synchronized Number pop() {
            if(isEmpty())
                throw new IllegalArgumentException("Expression expected");
            return super.pop();
        }
    }
}
