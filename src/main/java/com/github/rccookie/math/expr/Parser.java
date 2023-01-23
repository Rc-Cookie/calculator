package com.github.rccookie.math.expr;

import java.util.Stack;

final class Parser {

    public Expression parse(Iterable<Token> postfix) {
        Stack<Expression> stack = new MathStack();
        for(Token t : postfix) stack.push(switch(t) {
            case Token.NumberToken n -> new ConstantExpression(n.value());
            case Token.Symbol v -> v;
            case Token.Operator o -> o.apply(stack);
        });
        if(stack.size() > 1) //throw new AssertionError();
            throw new IllegalArgumentException("Mismatched parenthesis / brackets");
        return Expression.of(stack.pop());
    }


    static class MathStack extends Stack<Expression> {
        @Override
        public synchronized Expression peek() {
            if(isEmpty())
                throw new IllegalArgumentException("Expression expected");
            return super.peek();
        }

        @Override
        public synchronized Expression pop() {
            if(isEmpty())
                throw new IllegalArgumentException("Expression expected");
            return super.pop();
        }
    }
}
