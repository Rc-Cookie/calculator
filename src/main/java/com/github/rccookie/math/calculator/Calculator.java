package com.github.rccookie.math.calculator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Real;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Utils;

public class Calculator {

    public static final Number UNSPECIFIED = new Rational(0);
    static final Expression UNSPECIFIED_EXPR = Expression.of(UNSPECIFIED);


    private static final Map<String, Number> DEFAULT_VARS = Utils.map(
            "pi", Number.PI(),
            "e", Number.E(),
            "dec", Number.ABOUT_ONE(),
            "ans", new Rational(42),

            "min", Function.MIN,
            "max", Function.MAX,
            "floor", Function.FLOOR,
            "ceil", Function.CEIL,
            "round", Function.ROUND,
            "sin", Function.SIN,
            "cos", Function.COS,
            "tan", Function.TAN,
            "asin", Function.ASIN,
            "acos", Function.ACOS,
            "atan", Function.ATAN,
            "atan2", Function.ATAN2,
            "abs", Function.ABS,
            "sqrt", Function.SQRT,
            "hypot", Function.HYPOT,
            "factorial", Function.FACTORIAL,
            "exp", Function.EXP,
            "ln", Function.LN,
            "ld", Function.LD,
            "log", Function.LOG,
            "get", Function.GET,
            "size", Function.SIZE,
            "cross", Function.CROSS,
            "deg", Function.RAD_TO_DEG,
            "rad", Function.DEG_TO_RAD,
            "sum", Function.SUM,
            "\u03A3", Function.SUM,
            "product", Function.PRODUCT,
            "\u03A0", Function.PRODUCT
    );
    private static final Map<String, Number> OPTIONAL_DEFAULT_VARS = Utils.map(
            "precision", new Rational(Real.getPrecision()),
            "scientific", Real.SCIENTIFIC_NOTATION ? Number.ONE() : Number.ZERO(),
            "g", new Real(9.81, false),
            "c", new Rational(299792458),
            "h", new Real(6.62607015, -34, true),
            "E", new Real(1.602176634, -19, true),
            "m_e", new Real(9.109383701528, -31, false),
            "N_a", new Real(6.02214076, 23, false),
            "\u00B50", new Real(1.2566370621219, -6, false),
            "ep_0", new Real(8.854187812813, -12, false),
            "k", new Real(1.380649, -23, false)
    );

    private final Map<String, Number> variables = new HashMap<>();
    {
        variables.putAll(DEFAULT_VARS);
        variables.putAll(OPTIONAL_DEFAULT_VARS);
    }

    private final Map<String, Stack<Number>> localVariables = new HashMap<>();
    private String lastExpr = "ans";

    public Number getVar(String name) {
        Stack<Number> localVars = localVariables.get(name);
        if(localVars != null) return localVars.peek();

        Number var = variables.get(name);
        if(var == null)
            throw new IllegalArgumentException("Unknown variable or function: '" + name + "'");
        return var;
    }

    void addLocalVar(String name, Number var) {
        localVariables.computeIfAbsent(name, n -> new Stack<>()).push(var);
    }

    void removeLocalVar(String name) {
        Stack<Number> localVars = localVariables.get(name);
        if(localVars == null) {
            Console.warn("Removing non-existent local variable");
            return;
        }
        localVars.pop();
        if(localVars.isEmpty())
            localVariables.remove(name);
    }



    public void addVar(String name, Number var) {
        if(DEFAULT_VARS.containsKey(name))
            throw new IllegalArgumentException("Cannot override variable '"+name+"'");
        if(name.equals("precision")) {
            double p = var.toDouble(this);
            if(p < 2)
                throw new IllegalArgumentException("precision < 2");
            Real.setPrecision((int) p);
//            context = new MathContext((int) Math.max(2, p/2), RoundingMode.HALF_UP);
            var = new Rational((int) p);
        }
        else if(name.equals("scientific"))
            Real.SCIENTIFIC_NOTATION = !var.equals(Number.ZERO());
        variables.put(name, Arguments.checkNull(var, "var"));
    }



    public Number evaluateSmart(String expression) {
        expression = expression.trim();
        if(expression.isEmpty())
            return evaluate(lastExpr);
        char c = expression.charAt(0);
        if(c == '+' || c == '*' || c == '/' || c == ':' || c == '^' || c == '>' || c == '<' || c == '=')
            expression = "ans " + expression;
        return evaluate(expression);
    }

    public Number evaluate(String expression) {
        List<Token> tokens = new ArrayList<>();
        for(Token t : new Lexer(expression))
            tokens.add(t);
        Console.debug("Tokens:");
        Console.debug(tokens);
        Console.debug(tokens.toArray());
        tokens = (List<Token>) new InfixConverter(tokens).toPostfix();
        Console.debug("Postfix:");
        Console.debug(tokens);
        Console.debug(tokens.toArray());
        Expression expr = new Parser().parse(tokens);
        Console.debug("Expression:");
        Console.debug(expr);
        Number ans = expr.evaluate(this);
        //noinspection ConstantConditions
        while(ans instanceof Expression e && (ans = e.evaluate(this)) != ans);
        lastExpr = expression;
        variables.put("ans", ans);
        return ans;
    }



    public static void main(String[] args) throws IOException {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.addOption('e', "expr", true, "Evaluate the specified expression and exit")
                .action(expressions -> {
                    Calculator calculator = new Calculator();
                    evalInput(calculator, expressions);
                    evalCommand(calculator, "exit");
                });
        parser.setName("""
                        Java math interpreter - version 2.1
                        By RcCookie""");
        parser.setDescription("Evaluate entered math expressions. Evaluate '\\help' to show expressions help");
        parser.parse(args);

        System.out.println("""
                        Java math interpreter - version 2.1
                        By RcCookie
                        -----------------------------------""");

        Calculator calculator = new Calculator();
        calculator.addVar("exit", new Rational(0));
        //noinspection InfiniteLoopStatement
        while(true) {
            System.out.print("> ");
            evalInput(calculator, Console.in.readLine());
        }
    }

    private static void evalInput(Calculator calculator, String expressions) {
        if(expressions == null) {
            evalCommand(calculator, "exit");
            return;
        }
        Console.mapDebug("Charset", Charset.defaultCharset(), Charset.defaultCharset().newEncoder().canEncode('\u2248'));
        String aboutEqual = (Charset.defaultCharset().newEncoder().canEncode('\u2248') ? '\u2248' : '~') + " ";
        for(String expr : expressions.split(";")) try {
            if(expr.startsWith("\\"))
                evalCommand(calculator, expr.substring(1));
            else {
                Number res = calculator.evaluateSmart(expr);
                System.out.println((res instanceof Real r && !r.precise ? aboutEqual : "= ") + res);
            }
        } catch (Throwable t) {
            String msg = t.getMessage();
            System.err.println(msg != null ? msg : "Illegal expression");
            if(Console.getFilter().isEnabled("debug"))
                t.printStackTrace();
        }
    }

    private static void evalCommand(Calculator calculator, String cmd) {
        Console.mapDebug("Received command", cmd);
        switch(cmd) {
            case "exit" -> {
                try {
                    System.exit((int) calculator.getVar("exit").toDouble(calculator));
                } catch(Exception e) {
                    if(Console.getFilter().isEnabled("debug"))
                        e.printStackTrace();
                    System.exit(-1);
                }
            }
            case "vars" -> calculator.variables.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(var -> {
                if(var.getValue() instanceof Function f) {
                    System.out.print(var.getKey() + "(" + Arrays.stream(f.paramNames()).map(Object::toString).collect(Collectors.joining(",")) + ")");
                    if(!DEFAULT_VARS.containsKey(var.getKey()))
                        System.out.println(" := " + f);
                    else System.out.println();
                }
                else System.out.println(var.getKey() + " := " + var.getValue());
            });
            case "help" -> System.out.println("""
                    Enter a math expression to be evaluated. Supported features:
                     - Basic arithmetics (+-*/^!)
                     - Implicit multiplication (omit multiplication sign, i.e. 2(3+4) = 2*(3+4)
                     - Comparisons (< <= = >= >) returning 0 or 1
                     - Vectors: Declare with brackets, split arguments with commas, i.e. [1,2,3]
                     - Function calls as usual, i.e. f(2). List default functions using \\vars
                     - Variable declarations: declare using :=, i.e. x := 42
                     - Function declarations: declare using :=, i.e. f(x) := 2x
                     - Anonymous function declarations aka lambdas: declare using ->, i.e. f := x -> 2x
                     - First class functions: functions (particularly lambdas) may be passed to other functions
                     - Convert degrees to radians when writing ° symbol, i.e. 180° -> pi
                     - Convert percentage to normal number when writing % symbol, i.e. 10% -> 1/10
                     - Use the variable 'ans' to refer to the previous result, or operate as if it was at the front of the expression
                     - Set the variable 'precision' to set the approximate decimal number precision (>1, default is 100)
                     - Set the variable 'scientific' to something other than 0 to enable scientific notation output
                     - Set the variable 'exit' to a desired value to set the exit code of the program, exit with \\exit""");
            default -> System.out.println("Unknown command: '\\" + cmd + "'");
        }
    }
}
