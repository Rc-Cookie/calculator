package com.github.rccookie.math.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Utils;

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


    Object _null = registerJson();
    private static Object registerJson() {
        JsonDeserialization.register(Expression.class, json -> parse(json.asString()));
        return null;
    }


    /**
     * Evaluates the expression in the specified lookup context. The result might
     * be another expression.
     *
     * @param lookup The context in which to evaluate which contains variable definitions
     * @return The evaluated expression
     */
    Number evaluate(SymbolLookup lookup);

    Expression simplify();

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

    int precedence();

    default String toString(int parentPrecedence, boolean left) {
        int precedence = precedence();
        return parentPrecedence < precedence || (left & parentPrecedence == precedence) ? toString() : "("+this+")";
    }

    default String toTreeString() {
        return name() + "[" + Arrays.stream(operands()).map(Expression::toTreeString).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    default Object toJson() {
        return toString();
    }

    @Override
    default @NotNull BinaryOperation add(Number x) {
        return new SimpleBinaryOperation("+", "$1 + $2", this, x, Token.PLUS.precedence(), Number::add);
    }

    @Override
    @NotNull
    default BinaryOperation subtract(Number x) {
        return new SimpleBinaryOperation("-", "$1 - $2", this, x, Token.MINUS.precedence(), Number::subtract);
    }

    @Override
    @NotNull
    default BinaryOperation subtractFrom(Number x) {
        return new SimpleBinaryOperation("-", "$2 - $1", this, x, Token.MINUS.precedence(), Number::subtractFrom);
    }

    @Override
    @NotNull
    default BinaryOperation multiply(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("*", "$1\u00B7$2", this, x, Token.MULTIPLY.precedence(), Number::multiply),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default BinaryOperation divide(Number x) {
        return new SimpleBinaryOperation("/", "$1 / $2", this, x, Token.DIVIDE.precedence(), Number::divide);
    }

    @Override
    @NotNull
    default BinaryOperation divideOther(Number x) {
        return new SimpleBinaryOperation("/", "$2 / $1", this, x, Token.DIVIDE.precedence(), Number::divideOther);
    }

    @Override
    @NotNull
    default BinaryOperation raise(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("^", "$1^$2", this, x, Token.POWER.precedence(), Number::raise),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default BinaryOperation raiseOther(Number x) {
        return new OptimizedBinaryOperation(
                new SimpleBinaryOperation("^", "$2^$1", this, x, Token.POWER.precedence(), Number::raiseOther),
                Number.ZERO()
        );
    }

    @Override
    @NotNull
    default UnaryOperation abs() {
        return new SimpleUnaryOperation("abs", "|$x|", this, Integer.MAX_VALUE, Number::abs);
    }

    @Override
    @NotNull
    default UnaryOperation negate() {
        return new SimpleUnaryOperation("negate", "-$x", this, Token.NEGATE.precedence(), Number::negate);
    }

    @Override
    @NotNull
    default UnaryOperation invert() {
        return new SimpleUnaryOperation("invert", "1/$x", this, Token.DIVIDE.precedence(), Number::invert);
    }

    @Override
    @NotNull
    default BinaryOperation equalTo(Number x) {
        return new SimpleBinaryOperation("=", "$1 = $2", this, x, Token.EQUALS.precedence(), Number::equalTo);
    }

    @Override
    @NotNull
    default BinaryOperation lessThan(Number x) {
        return new SimpleBinaryOperation("<", "$1 < $2", this, x, Token.LESS.precedence(), Number::lessThan);
    }

    @Override
    default BinaryOperation lessThanOrEqual(Number x) {
        return new SimpleBinaryOperation("<=", "$1 <= $2", this, x, Token.LESS_OR_EQUAL.precedence(), Number::lessThanOrEqual);
    }

    @Override
    @NotNull
    default BinaryOperation greaterThan(Number x) {
        return new SimpleBinaryOperation(">", "$1 > $2", this, x, Token.GREATER.precedence(), Number::greaterThan);
    }

    @Override
    default BinaryOperation greaterThanOrEqual(Number x) {
        return new SimpleBinaryOperation(">=", "$1 >= $2", this, x, Token.GREATER_OR_EQUAL.precedence(), Number::greaterThanOrEqual);
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
        return x instanceof Expression e ? e : new ConstantExpression(x);
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


    default String format(String format, Expression x) {
        return format.replace("$x", x.toString(precedence(), false));
    }

    default String format(String format, Expression a, Expression b) {
        int precedence = precedence();
        boolean leftIs1 = format.indexOf("$1") < format.indexOf("$2");
        return format.replace("$1", a.toString(precedence, leftIs1)).replace("$2", b.toString(precedence, !leftIs1));
    }


    /**
     * A numeric expression. Has a constant value that is independent of the
     * evaluation context.
     */
    interface Constant extends Expression {
        Number value();

        @Override
        default Number evaluate(SymbolLookup lookup) {
            return value();
        }

        @Override
        default Expression simplify() {
            return this;
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

        @Override
        default String name() {
            return "Constant";
        }

        @Override
        default int precedence() {
            if(value() instanceof Rational r) {
                String str = r.toString();
                if(str.contains("-")) return Precedence.NEGATE;
                if(!str.contains("^")) return Precedence.MAX;
                if(!str.contains("\u00B7")) return Precedence.POWER;
                return Precedence.MULTIPLY;
            }
            if(value() instanceof Complex c) {
                String str = c.toString();
                if(str.contains("+") || str.contains("-")) return Precedence.PLUS;
                if((str.contains("i") && !str.equals("i")) || str.contains("\u00B7")) return Precedence.MULTIPLY;
                if(str.contains("^")) return Precedence.POWER;
            }
            return Precedence.MAX;
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

        @Override
        default int precedence() {
            return Integer.MAX_VALUE;
        }

        @Override
        default Expression simplify() {
            return this;
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

        @Override
        default Number evaluate(SymbolLookup lookup) {
            return evaluateHalf(lookup, a().evaluate(lookup));
        }

        Number evaluateHalf(SymbolLookup lookup, Number ea);
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

        @Override
        default int precedence() {
            return Token.MULTIPLY.precedence();
        }
    }

    /**
     * A list of multiple numbers, particularly to be passed as parameter to operation calls.
     */
    interface Numbers extends Expression, Iterable<Expression> {

        Numbers EMPTY = new NumbersImpl();

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
        default int precedence() {
            return Integer.MIN_VALUE; // Always wrap with parenthesis
        }

        @Override
        default @NotNull BinaryOperation add(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation subtract(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation subtractFrom(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation multiply(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation divide(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation divideOther(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation raise(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default @NotNull BinaryOperation raiseOther(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default UnaryOperation abs() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default UnaryOperation negate() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default UnaryOperation invert() {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default BinaryOperation equalTo(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default BinaryOperation lessThan(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default BinaryOperation lessThanOrEqual(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        @NotNull
        default BinaryOperation greaterThan(Number x) {
            throw new ArithmeticException("List arithmetics");
        }

        @Override
        default BinaryOperation greaterThanOrEqual(Number x) {
            throw new ArithmeticException("List arithmetics");
        }


        static Numbers of(Expression... elements) {
            return new NumbersImpl(elements.clone());
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
//        @Contract("_->this")
        default Number evaluate(SymbolLookup lookup) {
            if(paramCount() == 0) try {
                return evaluate(lookup, Numbers.EMPTY);
            } catch(IllegalArgumentException e) {
                Console.debug("Failed to evaluate parameter-less function, returning function expression:");
                Console.debug(Utils.getStackTraceString(e));
            }
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

        @Override
        Function simplify();

        @Override
        default int precedence() {
            return Token.LAMBDA_DEFINE.precedence();
        }

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
        default BinaryFunctionOperation derive(String name, String format, Expression b, int precedence, BinaryOperator<Number> operator) {
            if(b instanceof Function fb)
                return new BinaryFunctionOperationImpl(name, format, this, fb, precedence, operator);
            return new DerivedBinaryFunction(name, format, this, b, precedence, operator);
        }

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
        default BinaryFunctionOperation derive(String name, String format, Number b, int precedence, BinaryOperator<Number> operator) {
            return derive(name, format, Expression.of(b), precedence, operator);
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
        default UnaryFunctionOperation derive(String name, String format, int precedence, UnaryOperator<Number> operator) {
            return new DerivedUnaryFunction(name, format, this, precedence, operator);
        }



        @Override
        @NotNull
        default Expression.BinaryFunctionOperation add(Number x) {
            return derive("+", "$1 + $2", x, Token.PLUS.precedence(), Number::add);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation subtract(Number x) {
            return derive("-", "$1 - $2", x, Token.MINUS.precedence(), Number::subtract);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation subtractFrom(Number x) {
            return derive("-", "$2 - $1", x, Token.MINUS.precedence(), Number::subtractFrom);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation multiply(Number x) {
            return new OptimizedDerivedBinaryFunction(
                    derive("*", "$1\u00B7$2", x, Token.MULTIPLY.precedence(), Number::multiply),
                    Number.ZERO()
            );
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation divide(Number x) {
            return derive("/", "$1 / $2", x, Token.DIVIDE.precedence(), Number::divide);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation divideOther(Number x) {
            return derive("/", "$2 / $1", x, Token.DIVIDE.precedence(), Number::divideOther);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation raise(Number x) {
            return new OptimizedDerivedBinaryFunction(
                    derive("^", "$1^$2", x, Token.POWER.precedence(), Number::raise),
                    Number.ZERO()
            );
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation raiseOther(Number x) {
            return new OptimizedDerivedBinaryFunction(
                    derive("^", "$2^$1", x, Token.POWER.precedence(), Number::raiseOther),
                    Number.ZERO()
            );
        }

        @Override
        @NotNull
        default Expression.UnaryFunctionOperation abs() {
            return derive("abs", "|$x|", Integer.MAX_VALUE, Number::abs);
        }

        @Override
        @NotNull
        default Expression.UnaryFunctionOperation negate() {
            return derive("negate", "-$x", Token.NEGATE.precedence(), Number::negate);
        }

        @Override
        @NotNull
        default Expression.UnaryFunctionOperation invert() {
            return derive("invert", "1/$x", Token.DIVIDE.precedence(), Number::invert);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation equalTo(Number x) {
            return derive("=", "$1 = $2", x, Token.EQUALS.precedence(), Number::equalTo);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation lessThan(Number x) {
            return derive("<", "$1 < $2", x, Token.LESS.precedence(), Number::lessThan);
        }

        @Override
        default BinaryFunctionOperation lessThanOrEqual(Number x) {
            return derive("<=", "$1 <= $2", x, Token.LESS_OR_EQUAL.precedence(), Number::lessThanOrEqual);
        }

        @Override
        @NotNull
        default Expression.BinaryFunctionOperation greaterThan(Number x) {
            return derive(">", "$1 > $2", x, Token.GREATER.precedence(), Number::greaterThan);
        }

        @Override
        default BinaryFunctionOperation greaterThanOrEqual(Number x) {
            return derive(">=", "$1 >= $2", x, Token.GREATER_OR_EQUAL.precedence(), Number::greaterThanOrEqual);
        }

        static Function of(Number expr) {
            return new RuntimeFunction(Expression.of(expr));
        }
    }

    interface UnaryFunctionOperation extends Function, UnaryOperation { }

    interface BinaryFunctionOperation extends Function, BinaryOperation {
        @Override
        default Number evaluate(SymbolLookup lookup) {
            return Function.super.evaluate(lookup);
        }

        @Override
        default Number evaluateHalf(SymbolLookup lookup, Number ea) {
            return this;
        }

        @Override
        Function a();

        @Override
        default Number evaluate(SymbolLookup lookup, Number params) {
            return evaluateHalf(lookup, params, a().evaluate(lookup, params));
        }

        Number evaluateHalf(SymbolLookup lookup, Number params, Number ea);
    }
}
