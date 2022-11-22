package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An expression is a special type of number than has to be evaluated to be
 * a normal number. This evaluation process may include variable references
 * and function calls, thus a lookup context is required.
 */
public interface Expression extends Number {

    /**
     * Expression of {@link Number#ZERO()}.
     */
    static Expression ZERO() { return of(Number.ZERO()); }

    /**
     * An expression indicating that the function parameter was not specified,
     * with an effective value of 0.
     */
    static Expression UNSPECIFIED() { return SymbolLookup.UNSPECIFIED_EXPR; }


    /**
     * Evaluates the expression in the specified lookup context. The result might
     * be another expression.
     *
     * @param lookup The context in which to evaluate which contains variable definitions
     * @return The evaluated expression
     */
    Number evaluate(SymbolLookup lookup);

    /**
     * Returns the number of operands this expression operates on when evaluated, if any.
     *
     * @return The number of operands the expression operates on
     */
    int operandCount();

    /**
     * Returns the operands this expression operates on when evaluated, if any.
     *
     * @return The operands thes expression operates on, if any
     */
    Expression[] operands();

    /**
     * Returns the name of this operation, for example '+' or '*' or the name of the
     * function being called. The name should not have to do with the actual parameters
     * of the operation.
     *
     * @return A name for the expression
     */
    String name();

    default String toTreeString() {
        return name() + "[" + Arrays.stream(operands()).map(Expression::toTreeString).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    default @NotNull Expression add(Number x) {
        return apply("+", "$1 + $2", x, Number::add);
    }

    @Override
    @NotNull
    default Expression subtract(Number x) {
        return apply("-", "$1 - $2", x, Number::subtract);
    }

    @Override
    @NotNull
    default Expression subtractFrom(Number x) {
        return applyInverse("-", "$2 - $1", x, Number::subtract);
    }

    @Override
    @NotNull
    default Expression multiply(Number x) {
        return apply((a,b) -> new OptimizedBinaryOperation("*", "($1) * ($2)", a, b, Number.ZERO(), Number::multiply), x);
    }

    @Override
    @NotNull
    default Expression divide(Number x) {
        return apply("/", "($1) / ($2)", x, Number::divide); // Don't optimize in case x evaluates to 0
    }

    @Override
    @NotNull
    default Expression divideOther(Number x) {
        return applyInverse("/", "($1) / ($2)", x, Number::divide);
    }

    @Override
    @NotNull
    default Expression raise(Number x) {
        return apply((a,b) -> new OptimizedBinaryOperation("^", "($1)^($2)", a, b, Number.ZERO(), Number::raise), x);
    }

    @Override
    @NotNull
    default Expression raiseOther(Number x) {
        return apply((b,a) -> new OptimizedBinaryOperation("^", "($1)^($2)", a, b, Number.ZERO(), Number::raise), x);
    }

    @Override
    @NotNull
    default Expression abs() {
        return apply("abs", "|$x|", Number::abs);
    }

    @Override
    @NotNull
    default Expression negate() {
        return apply("negate", "-($x)", Number::negate);
    }

    @Override
    @NotNull
    default Expression invert() {
        return apply("invert", "1/($y)", Number::invert);
    }

    @Override
    @NotNull
    default Expression equalTo(Number x) {
        return apply("=", "$1 = $2", x, Number::equalTo);
    }

    @Override
    @NotNull
    default Expression lessThan(Number x) {
        return apply("<", "$1 < $2", x, Number::lessThan);
    }

    @Override
    default Expression lessThanOrEqual(Number x) {
        return apply("<=", "$1 <= $2", x, Number::lessThanOrEqual);
    }

    @Override
    @NotNull
    default Expression greaterThan(Number x) {
        return apply(">", "$1 > $2", x, Number::greaterThan);
    }

    @Override
    default Expression greaterThanOrEqual(Number x) {
        return apply(">=", "$1 >= $2", x, Number::greaterThanOrEqual);
    }


    default Expression apply(String name, String format, Number b, BinaryOperator<Number> function) {
        return apply((_a,_b) -> new SimpleBinaryOperation(name, format, _a, _b, function), b);
    }

    default Expression applyInverse(String name, String format, Number a, BinaryOperator<Number> function) {
        return applyInverse((_a,_b) -> new SimpleBinaryOperation(name, format, _a, _b, function), a);
    }

    default Expression apply(String name, String format, UnaryOperator<Number> function) {
        return apply(x -> new SimpleUnaryOperation(name, format, x, function));
    }

    default Expression apply(BiFunction<Expression,Expression,BinaryOperation> operation, Number b) {
        return operation.apply(this, Expression.of(b));
    }

    default Expression applyInverse(BiFunction<Expression,Expression,BinaryOperation> operation, Number a) {
        return operation.apply(Expression.of(a), this);
    }

    default Expression apply(java.util.function.Function<Expression,UnaryOperation> operation) {
        return operation.apply(this);
    }

    @Override
    default double toDouble(SymbolLookup c) {
        return evaluate(c).toDouble(c);
    }


    /**
     * Wraps the given number as a numeric expression, or returns the
     * number if it already is an expression.
     *
     * @param x The number to wrap as expression
     * @return The number wrapped as expression, or the passed expression itself
     */
    static Expression of(Number x) {
        return x instanceof Expression e ? e : new NumericExpression(x);
    }

    /**
     * Evaluates the given number, if it is an expression, otherwise returns the
     * number itself.
     *
     * @param x The number to be evaluated if it is an expression
     * @param lookup The lookup context to use for evaluation
     * @return The evaluated expression or the number itself
     */
    static Number evaluate(Number x, SymbolLookup lookup) {
        return x instanceof Expression expr ? expr.evaluate(lookup) : x;
    }


    /**
     * Parses the given math expression into an expression tree.
     *
     * @param expression The math expression to parse
     * @return The parsed expression
     */
    static Expression parse(String expression) {
        if(!Console.getFilter().isEnabled("debug"))
            return new Parser().parse(new InfixConverter(new Lexer(expression)).toPostfix());

        List<Token> tokens = new ArrayList<>();
        for(Token t : new Lexer(expression)) tokens.add(t);
        Console.debug("Tokens:");
        Console.debug(tokens);
        Console.debug(tokens.toArray());
        Iterable<Token> postfix = new InfixConverter(tokens).toPostfix();
        Console.debug("Postfix:");
        Console.debug(postfix);
        Console.debug(((Collection<?>) postfix).toArray());
        return new Parser().parse(postfix);
    }


    /**
     * A numeric expression. Has a constant value that is independent of the
     * evaluation context.
     */
    interface Numeric extends Expression {
        Number value();

        @Override
        default Number evaluate(SymbolLookup lookup) {
            return value();
        }

        @Override
        default int operandCount() {
            return 0;
        }

        @Override
        default Expression[] operands() {
            return new Expression[0];
        }

        @Override
        default String toTreeString() {
            return name() + "[" + value() + "]";
        }
    }

    /**
     * An expression that represents a named symbol. The actual value depends on
     * the lookup context which directly corresponds to the value when evaluated.
     * The evaluated value is not necessarily numeric, for example it could also
     * be a function.
     */
    interface Symbol extends Expression {
        @Override
        String name();

        @Override
        default int operandCount() {
            return 0;
        }

        @Override
        default Expression[] operands() {
            return new Expression[0];
        }

        @Override
        default String toTreeString() {
            return "Symbol[" + name() + "]";
        }
    }

    /**
     * An operation on one or more numbers (which may intern again be expressions).
     */
    interface Operation extends Expression {
    }

    /**
     * An operation on two numbers (which may intern again be expressions).
     */
    interface UnaryOperation extends Operation {
        Expression x();

        @Override
        default Expression[] operands() {
            return new Expression[] { x() };
        }

        @Override
        default int operandCount() {
            return 1;
        }
    }

    /**
     * An operation on a single number (which may intern again be an expression).
     */
    interface BinaryOperation extends Operation {
        Expression a();
        Expression b();

        @Override
        default Expression[] operands() {
            return new Expression[] { a(), b() };
        }

        @Override
        default int operandCount() {
            return 2;
        }
    }

    /**
     * An operation that describes an implicit operation between two operands, which
     * is if no operand is specified. This particularly includes function calls, but
     * also implicit multiplication. The exact type can only be determined when evaluated
     * in a specific context. For example, <code>f(x)</code> may be evaluated as function
     * call if <code>f</code> describes a function, or as multiplication if it just
     * describes a numeric value.
     */
    interface ImplicitOperation extends BinaryOperation {
        boolean isFunctionCall(SymbolLookup lookup);
    }

    /**
     * A list of multiple numbers, particularly to be passed as parameter to function calls.
     */
    interface Numbers extends Expression, Iterable<Expression> {

        int size();

        @Override
        default int operandCount() {
            return size();
        }

        Expression get(int index);

        Expression[] toArray();

        @Override
        default Expression[] operands() {
            return toArray();
        }

        @Override
        Numbers evaluate(SymbolLookup lookup);

        Number evaluate(int index, SymbolLookup lookup);

        Stream<Expression> stream();

        @Override
        default BinaryOperation apply(BiFunction<Expression, Expression, BinaryOperation> operation, Number b) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default BinaryOperation applyInverse(BiFunction<Expression, Expression, BinaryOperation> operation, Number a) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default UnaryOperation apply(java.util.function.Function<Expression, UnaryOperation> operation) {
            throw new ArithmeticException("List arithmetics");
        }
    }

    /**
     * An expression that requires parameters to be evaluated. This differs from
     * {@link ImplicitOperation} in that it represents the unevaluated function expression.
     * A function call would be represented as an implicit operation where the first operand
     * is a {@link Function}.
     * <p>Evaluating a function with the normal {@link #evaluate(SymbolLookup)} method
     * will simply return the function itself. Use {@link #evaluate(SymbolLookup, Number)}
     * instead to specify the parameters.</p>
     *
     * @see Numbers
     */
    interface Function extends Expression {
        @Override
        String name();
        int paramCount();
        String[] paramNames();
        Expression expr();

        @Override
        @Contract("_->this")
        default Number evaluate(SymbolLookup lookup) {
            return this;
        }

        /**
         * Evaluates the function with the given parameters. The parameters should
         * already be evaluated, otherwise they will be treated as expressions in
         * the calculation.
         *
         * @param lookup The lookup context to use
         * @param params The function parameters. Either a single number or an instance
         *               of {@link Numbers}
         * @return The result of the function
         */
        Number evaluate(SymbolLookup lookup, Number params);
    }
}
