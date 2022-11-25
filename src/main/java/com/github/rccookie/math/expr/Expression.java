package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
 * and operation calls, thus a lookup context is required.
 */
public interface Expression extends Number {

    /**
     * Expression of {@link Number#ZERO()}.
     */
    static Expression ZERO() { return of(Number.ZERO()); }

    /**
     * An expression indicating that the operation parameter was not specified,
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
     * @return The operands this expression operates on, if any
     */
    Expression[] operands();

    /**
     * Returns the name of this operation, for example '+' or '*' or the name of the
     * operation being called. The name should not have to do with the actual parameters
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
        return new SimpleBinaryOperation("+", "$1 + $2", this, x, Number::add);
    }

    @Override
    @NotNull
    default Expression subtract(Number x) {
        return new SimpleBinaryOperation("-", "$1 - $2", this, x, Number::subtract);
    }

    @Override
    @NotNull
    default Expression subtractFrom(Number x) {
        return new SimpleBinaryOperation("-", "$2 - $1", this, x, Number::subtractFrom);
    }

    @Override
    @NotNull
    default Expression multiply(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("*", "($1) * ($2)", this, x, Number::multiply),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression divide(Number x) {
        return new SimpleBinaryOperation("/", "($1) / ($2)", this, x, Number::divide);
    }

    @Override
    @NotNull
    default Expression divideOther(Number x) {
        return new SimpleBinaryOperation("/", "($2) / ($1)", this, x, Number::divideOther);
    }

    @Override
    @NotNull
    default Expression raise(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("^", "($1)^($2)", this, x, Number::raise),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression raiseOther(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("^", "($2)^($1)", this, x, Number::raiseOther),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default Expression abs() {
        return new SimpleUnaryOperation("abs", "|$x|", this, Number::abs);
    }

    @Override
    @NotNull
    default Expression negate() {
        return new SimpleUnaryOperation("negate", "-($x)", this, Number::negate);
    }

    @Override
    @NotNull
    default Expression invert() {
        return new SimpleUnaryOperation("invert", "1/($y)", this, Number::invert);
    }

    @Override
    @NotNull
    default Expression equalTo(Number x) {
        return new SimpleBinaryOperation("=", "$1 = $2", this, x, Number::equalTo);
    }

    @Override
    @NotNull
    default Expression lessThan(Number x) {
        return new SimpleBinaryOperation("<", "$1 < $2", this, x, Number::lessThan);
    }

    @Override
    default Expression lessThanOrEqual(Number x) {
        return new SimpleBinaryOperation("<=", "$1 <= $2", this, x, Number::lessThanOrEqual);
    }

    @Override
    @NotNull
    default Expression greaterThan(Number x) {
        return new SimpleBinaryOperation(">", "$1 > $2", this, x, Number::greaterThan);
    }

    @Override
    default Expression greaterThanOrEqual(Number x) {
        return new SimpleBinaryOperation(">=", "$1 >= $2", this, x, Number::greaterThanOrEqual);
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
     * be an operation.
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

        static Symbol of(String name) {
            return new Token.Symbol(name);
        }
    }

    /**
     * An operation on one or more numbers (which may intern again be expressions).
     */
    interface Operation extends Expression { }

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
     * is if no operand is specified. This particularly includes operation calls, but
     * also implicit multiplication. The exact type can only be determined when evaluated
     * in a specific context. For example, <code>f(x)</code> may be evaluated as operation
     * call if <code>f</code> describes an operation, or as multiplication if it just
     * describes a numeric value.
     */
    interface ImplicitOperation extends BinaryOperation {
        boolean isFunctionCall(SymbolLookup lookup);
    }

    /**
     * A list of multiple numbers, particularly to be passed as parameter to operation calls.
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
        @NotNull
        default Expression add(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression subtract(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression subtractFrom(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression multiply(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression divide(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression divideOther(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression raise(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression raiseOther(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression abs() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression negate() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression invert() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression equalTo(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression lessThan(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default Expression lessThanOrEqual(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default Expression greaterThan(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default Expression greaterThanOrEqual(Number x) {
            throw new ArithmeticException("List arithmetics");
        }
    }

    /**
     * An expression that requires parameters to be evaluated. This differs from
     * {@link ImplicitOperation} in that it represents the unevaluated operation expression.
     * An operation call would be represented as an implicit operation where the first operand
     * is a {@link Function}.
     * <p>Evaluating a operation with the normal {@link #evaluate(SymbolLookup)} method
     * will simply return the operation itself. Use {@link #evaluate(SymbolLookup, Number)}
     * instead to specify the parameters.</p>
     *
     * @see Numbers
     */
    interface Function extends Operation {
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
         * Evaluates the operation with the given parameters. The parameters should
         * already be evaluated, otherwise they will be treated as expressions in
         * the calculation.
         *
         * @param lookup The lookup context to use
         * @param params The operation parameters. Either a single number or an instance
         *               of {@link Numbers}
         * @return The result of the operation
         */
        Number evaluate(SymbolLookup lookup, Number params);

        /**
         * Returns a new function that represents first evaluating this function,
         * then applying the specified operator to the result and the given argument.
         *
         * @param name The name for the derived function
         * @param format The toString() format, using $1 and $2
         * @param b The second parameter for the operator
         * @param operator The operator to apply to the result of the function and the
         *                 specified parameter
         * @return A new function representing the function described above
         */
        Function derive(String name, String format, Expression b, BinaryOperator<Number> operator);

        /**
         * Returns a new function that represents first evaluating this function,
         * then applying the specified operator to the result and the given argument.
         *
         * @param name The name for the derived function
         * @param format The toString() format, using $1 and $2
         * @param b The second parameter for the operator
         * @param operator The operator to apply to the result of the function and the
         *                 specified parameter
         * @return A new function representing the function described above
         */
        default Function derive(String name, String format, Number b, BinaryOperator<Number> operator) {
            return derive(name, format, Expression.of(b), operator);
        }

        /**
         * Returns a new function that represents first evaluating this function,
         * then applying the specified operator to the result.
         *
         * @param name The name for the derived function
         * @param format The toString() format, using $x
         * @param operator The operator to apply to the result of the function
         * @return A new function representing the function described above
         */
        Function derive(String name, String format, UnaryOperator<Number> operator);
    }
}
