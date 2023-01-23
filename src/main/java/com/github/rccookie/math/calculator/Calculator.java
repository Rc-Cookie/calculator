package com.github.rccookie.math.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Map;

import com.github.rccookie.json.JsonObject;
import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.SimpleNumber;
import com.github.rccookie.math.expr.DefaultSymbolLookup;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.Functions;
import com.github.rccookie.math.expr.MathEvaluationException;
import com.github.rccookie.math.expr.MathExpressionSyntaxException;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Utils;
import com.github.rccookie.util.config.Config;

import org.jetbrains.annotations.Nullable;

/**
 * The calculator class can evaluate expressions and manages stored
 * variables. It also contains many default variables and adds support
 * for {@link FormulaPackage}s.
 *
 * <p>The calculator has an command line interface using the {@link #main(String[])}
 * function.</p>
 */
public class Calculator {

    /**
     * Version of this calculator API.
     */
    public static final String VERSION = "2.7";

    private static final JsonObject DEFAULT_SETTINGS = new JsonObject(
            "precision", 50,
            "scientific", false
    );


    private static final Map<String, Number> DEFAULT_VARS = Utils.map(
            "pi", Number.PI(),
            "e", Number.E(),
            "i", Number.I(),
            "dec", Number.ABOUT_ONE(),
            "ans", new Rational(42),
            "true", Number.ONE(),
            "false", Number.ZERO(),
            "_", Expression.UNSPECIFIED(),

            "min", Functions.MIN,
            "max", Functions.MAX,
            "floor", Functions.FLOOR,
            "ceil", Functions.CEIL,
            "round", Functions.ROUND,
            "sin", Functions.SIN,
            "cos", Functions.COS,
            "tan", Functions.TAN,
            "asin", Functions.ASIN,
            "acos", Functions.ACOS,
            "atan", Functions.ATAN,
            "atan2", Functions.ATAN2,
            "arg", Functions.ARGUMENT,
            "abs", Functions.ABS,
            "sqrt", Functions.SQRT,
            "hypot", Functions.HYPOT,
            "factorial", Functions.FACTORIAL,
            "exp", Functions.EXP,
            "ln", Functions.LN,
            "ld", Functions.LD,
            "log", Functions.LOG,
            "get", Functions.GET,
            "size", Functions.SIZE,
            "norm", Functions.NORMALIZE,
            "cross", Functions.CROSS,
            "deg", Functions.RAD_TO_DEG,
            "rad", Functions.DEG_TO_RAD,
            "sum", Functions.SUM,
            "\u03A3", Functions.SUM,
            "product", Functions.PRODUCT,
            "\u03A0", Functions.PRODUCT,

            "poly", Functions.POLYNOM,
            "der", Functions.DERIVATIVE,
            "antiDer", Functions.ANTIDERIVATIVE,
            "int", Functions.INTEGRATE
    );
    private static final Map<String, Number> OPTIONAL_DEFAULT_VARS = Utils.map(
            "precision", new Rational(Rational.getPrecision()),
            "scientific", switch(Rational.getToStringMode()) {
                case SMART, DECIMAL_IF_POSSIBLE, FORCE_FRACTION, FORCE_DECIMAL -> Number.ZERO();
                default -> Number.ONE();
            },
            "k", new Rational(1000)
    );

    private static final char ABOUT_EQUAL = Charset.defaultCharset().newEncoder().canEncode('\u2248') ? '\u2248' : '~';

    private final Lookup lookup = new Lookup();
    private String lastExpr = "ans";
    private int moreCount = 0;


    /**
     * Returns a mutable reference to the variables in the calculator. Some variable names
     * are protected for modification. Subsequent changes to the variables in the calculator
     * will be reflected in the returned lookup, and vice versa.
     *
     * @return The variables in this calculator
     */
    public SymbolLookup variables() {
        return lookup;
    }


    /**
     * "Smartly" parses and evaluates the given expression. This is similar to
     * {@link #evaluate(String)}, but adds some conveniences:
     * <ul>
     *     <li>If the expression is blank, the previous expression will be evaluated again.
     *     The default previous expression is 'ans'.</li>
     *     <li>If the expression starts with a binary operator or postfix unary operator, 'ans'
     *     will be inserted before the expression (this gives the illusion to operate on the
     *     previous result)</li>
     * </ul>
     *
     * @param expression The expression to evaluate
     * @return The evaluated expression
     */
    public Number evaluateSmart(String expression) throws MathExpressionSyntaxException, MathEvaluationException {
        expression = expression.trim();
        if(expression.isEmpty())
            return evaluate(lastExpr);
        char c = expression.charAt(0);
        if(c == '+' || c == '*' || c == '\u00B7' || c == '/' || c == ':' || c == '^' || c == '>' || c == '<' || c == '=' || c == '\u00B2' || c == '\u00B3')
            expression = "ans " + expression;
        return evaluate(expression);
    }

    /**
     * Parses and evaluates the given math expression and returns the result.
     * This may use or modify the variables in this calculator.
     *
     * @param expression The expression to evaluate
     * @return The value of the expression
     */
    public Number evaluate(String expression) throws MathExpressionSyntaxException, MathEvaluationException {
        Expression expr = Expression.parse(expression);
        Console.debug("Expression:");
        Console.debug(expr);
        Console.debug(expr.toTreeString());
        Number ans = expr.evaluate(lookup);
        if(ans instanceof Expression e)
            ans = e.simplify();
        Console.debug("Result:");
        Console.debug(Expression.of(ans).toTreeString());
        lastExpr = expression;
        lookup.setAns(ans);
        return ans;
    }


    /**
     * Entry point for the command line interface of the calculator.
     *
     * @param args Command line args, run --help for more detail
     * @throws IOException If an IO exception occurs
     */
    public static void main(String[] args) throws IOException {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.addOption('e', "expr", true, "Evaluate the specified expression and exit")
                .action(expressions -> {
                    Calculator calculator = new Calculator();
                    calculator.evalInput(expressions);
                    calculator.evalCommand("exit");
                });
        parser.setName("Java math interpreter - version " + VERSION + "\nBy RcCookie");
        parser.setDescription("""

                        Usage: math [--options] [expression]
                        Evaluate entered math expressions. Evaluate '\\help' to show expressions help.""");

        String argsStr = parser.parse(args).getArgsString();
        if(!argsStr.isBlank()) {
            Calculator calculator = new Calculator();
            calculator.evalInput(argsStr);
            calculator.evalCommand("exit");
        }

        System.out.println("Java math interpreter - version " + VERSION + """
                            
                            By RcCookie
                            -----------------------------------""");

        if(System.getProperty("os.name").toLowerCase().contains("windows"))
            checkReg();

        Calculator calculator = new Calculator();
        calculator.variables().put("exit", new Rational(0));
        Rational.setToStringMode(Rational.ToStringMode.SMART_SCIENTIFIC);

        Config config = Config.fromAppdataPath("Calculator");
        try {
            config.setDefaults(DEFAULT_SETTINGS);
            calculator.variables().put("precision", new Rational(config.getInt("precision")));
            calculator.variables().put("scientific", config.getBool("scientific") ? Number.ONE() : Number.ZERO());
        } catch(Exception e) {
            System.err.println("Failed to load settings");
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        }

        //noinspection InfiniteLoopStatement
        while(true) {
            System.out.print("> ");
            calculator.evalInput(Console.in.readLine());

            config.set("precision", Rational.getPrecision());
            config.set("scientific", switch(Rational.getToStringMode()) {
                case SMART, DECIMAL_IF_POSSIBLE, FORCE_FRACTION, FORCE_DECIMAL -> false;
                default -> true;
            });
        }
    }

    private void evalInput( String expressions) {
        if(expressions == null) {
            evalCommand("exit");
            return;
        }
        for(String expr : expressions.split(";")) try {
            if(expr.startsWith("\\"))
                evalCommand(expr.substring(1));
            else {
                moreCount = 0;
                Number res = evaluateSmart(expr);
                printRes(res, null);
            }
        } catch(MathExpressionSyntaxException e) {
            System.err.println("Illegal expression: " + e.getMessage());
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        } catch(MathEvaluationException e) {
            System.err.println(e.getMessage());
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        } catch (Throwable t) {
            String msg = t.getMessage();
            System.err.println(msg != null ? msg : "Internal error");
            if(Console.getFilter().isEnabled("debug"))
                t.printStackTrace();
        }
    }

    private void printRes(Number res, Rational.ToStringMode mode) {
        while(res instanceof Expression.Function f && f.paramCount() == 0)try {
            Number val = f.evaluate(lookup, Expression.Numbers.EMPTY);
            if(res.equals(val)) break;
            res = val;
        } catch(Exception e) {
            Console.debug("Failed to evaluate parameter-less function, displaying as function:");
            Console.debug(Utils.getStackTraceString(e));
            break;
        }

        if(res instanceof Expression && !(res instanceof Expression.Numbers))
            System.out.print(res);
        else if(res instanceof Rational r) {
            Rational.DetailedToString str = mode != null ? r.detailedToString(mode) : r.detailedToString();
            System.out.print(str.precise() ? '=' : ABOUT_EQUAL);
            System.out.print(' ');
            if(str.precise() && !str.isFull()) {
                int index = str.str().indexOf("\u00B7");
                if(index == -1) System.out.print(str.str() + "...");
                else System.out.print(str.str().substring(0, index) + "..." + str.str().substring(index));
            }
            else System.out.print(str.str());
        }
        else if(res instanceof SimpleNumber n && !n.precise() || res instanceof Complex c && !c.precise())
            System.out.print(ABOUT_EQUAL + " " + res);
        else System.out.print("= " + res);
        System.out.println();
    }

    private void evalCommand(String cmd) {
        Console.mapDebug("Received command", cmd);
        String[] cmds = cmd.split("\s+");
        cmds[0] = cmds[0].toLowerCase();
        switch(cmds[0]) {
            case "exit" -> {
                try {
                    System.exit((int) lookup.get("exit").toDouble(lookup));
                } catch(Exception e) {
                    if(Console.getFilter().isEnabled("debug"))
                        e.printStackTrace();
                    System.exit(-1);
                }
            }
            case "vars" -> lookup.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(var -> {
                if(var.getValue() instanceof Expression.Function f) {
                    System.out.print(var.getKey() + "(" + String.join(",", f.paramNames()) + ")");
                    if(!DEFAULT_VARS.containsKey(var.getKey()))
                        System.out.println(" := " + f.expr());
                    else System.out.println();
                }
                else System.out.println(var.getKey() + " := " + var.getValue());
            });
            case "more" -> {
                if(moreCount < 10)
                    moreCount++;
                Number res = lookup.get("ans");
                int precision = Rational.getPrecision();
                Rational.setPrecision(precision << moreCount);
                printRes(res, null);
                Rational.setPrecision(precision);
            }
            case "frac" -> {
                Number res = lookup.get("ans");
                printRes(res, Rational.ToStringMode.FORCE_FRACTION);
            }
            case "dec" -> {
                Number res = lookup.get("ans");
                printRes(res, Rational.ToStringMode.FORCE_DECIMAL);
            }
            case "bin" -> {
                Number res = lookup.get("ans");
                System.out.println(getInt(res).toString(2));
            }
            case "hex" -> {
                Number res = lookup.get("ans");
                System.out.println(getInt(res).toString(16));
            }
            case "radix" -> {
                if(cmds.length != 2)
                    throw new IllegalCommandException("Usage: \\radix <radix>");
                Number res = lookup.get("ans");
                int radix = Integer.parseInt(cmds[1]);
                System.out.println(getInt(res).toString(radix));
            }
            case "del", "delete" -> {
                if(cmds.length == 1)
                    throw new IllegalCommandException("Usage: \\" + cmds[0] + " <var [var2 var3...]>");
                for(int i=1; i<cmds.length; i++)
                    lookup.delete(cmds[i]);
            }
            case "load" -> {
                if(cmds.length != 2)
                    throw new IllegalCommandException("Usage: \\load <packageName>, i.e. \\load physics");
                FormulaPackage.load(cmds[1]).addTo(lookup);
            }
            case "help" -> System.out.println("""
                    Enter a math expression to be evaluated. Supported features:
                     - Basic arithmetics (+-*/^!)
                     - Implicit multiplication (omit multiplication sign, i.e. 2(3+4) = 2\u00B7(3+4)
                     - Comparisons (< <= = >= >) returning 0 or 1
                     - Vectors: Declare with brackets, split arguments with commas, i.e. [1,2,3]
                     - Function calls as usual, i.e. f(2). List default functions using \\vars
                     - Variable declarations: declare using :=, i.e. x := 42
                     - Function declarations: declare using :=, i.e. f(x) := 2x
                     - Anonymous function declarations aka lambdas: declare using ->, i.e. f := x -> 2x
                     - First class functions: functions (particularly lambdas) may be passed to other functions
                     - Convert degrees to radians when writing ° symbol, i.e. 180° -> pi
                     - Convert percentage to normal number when writing % symbol, i.e. 10% -> 1/10
                     - Load packages of constants and formulas using \\load <name>, i.e. \\load physics
                     - Force decimal or fraction rendering for the last result using \\dec / \\frac
                     - Output more decimal places from the last result using \\more
                     - Use the variable 'ans' to refer to the previous result, or operate as if it was at the front of the expression
                     - Set the variable 'precision' to set the approximate decimal number precision (>1, default is 100)
                     - Set the variable 'scientific' to something other than 0 to enable scientific notation output
                     - Set the variable 'exit' to a desired value to set the exit code of the program, exit with \\exit""");
            default -> throw new IllegalCommandException("Unknown command: '\\" + cmd + "'");
        }
    }

    private static void checkReg() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{ "reg", "query", "HKCU\\SOFTWARE\\Microsoft\\Command Processor", "/v", "AutoRun" });
            p.waitFor();
            if(p.exitValue() != 0)
                Console.mapDebug("Setting AutoRun reg", Runtime.getRuntime().exec(new String[] { "reg", "add", "HKCU\\SOFTWARE\\Microsoft\\Command Processor", "/v", "AutoRun", "/t", "REG_SZ", "/d", "chcp 1252 > nul"}).waitFor());
            else try(BufferedReader in = p.inputReader()) {
                String line = in.readLine();
                while(!line.contains("AutoRun")) line = in.readLine();
                String cur = line.split("\\s+", 3)[2];
                Console.mapDebug("Current reg value", cur);
                if(cur.contains("chcp")) {
                    Console.debug("AutoRun charset already set");
                    return;
                }
                String combined = cur + " & chcp 1252 > nul";
                Console.mapDebug("New reg value", combined);
                Console.mapDebug("Exit code", Runtime.getRuntime().exec(new String[] { "reg", "add", "HKCU\\SOFTWARE\\Microsoft\\Command Processor", "/v", "AutoRun", "/t", "REG_SZ", "/d", combined}).waitFor());
            }
        } catch(Exception e) {
            System.err.println("Failed to access registry");
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        }
    }

    private static BigInteger getInt(Number n) {
        if(n instanceof Rational r && r.d.equals(BigInteger.ONE))
            return r.n;
        if(n instanceof Complex c && c.isReal() && c.re instanceof Rational r && r.d.equals(BigInteger.ONE))
            return r.n;
        throw new MathExpressionSyntaxException("Cannot calculate radix rendering for non-integer");
    }



    private static class Lookup extends DefaultSymbolLookup {

        {
            DEFAULT_VARS.forEach(super::put);
            OPTIONAL_DEFAULT_VARS.forEach(super::put);
        }

        @Override
        public Number get(String name) {
            Number var = super.get(name);
            return var instanceof Rational r && !r.precise ? new Rational(r.toBigDecimal(), false) : var; // Round high-precision constants
        }

        @Override
        public void put(String name, @Nullable Number var) {
            if(DEFAULT_VARS.containsKey(name))
                throw new MathEvaluationException("Cannot override variable '"+name+"'");
            switch (name) {
                case "precision" -> {
                    assert var != null;
                    int p = (int) Arguments.checkNull(var, "var").toDouble(this);
                    if(p < 2)
                        throw new MathEvaluationException("precision < 2");
                    Rational.setPrecision(p);
                    var = new Rational(p);
                }
                case "scientific" -> {
                    assert var != null;
                    boolean scientific = !Arguments.checkNull(var, "var").equals(Number.ZERO());
                    Rational.setToStringMode(switch(Rational.getToStringMode()) {
                        case SMART, SMART_SCIENTIFIC -> scientific ? Rational.ToStringMode.SMART_SCIENTIFIC : Rational.ToStringMode.SMART;
                        case DECIMAL_IF_POSSIBLE, DECIMAL_IF_POSSIBLE_SCIENTIFIC -> scientific ? Rational.ToStringMode.DECIMAL_IF_POSSIBLE_SCIENTIFIC : Rational.ToStringMode.DECIMAL_IF_POSSIBLE;
                        case FORCE_FRACTION, FORCE_FRACTION_SCIENTIFIC -> scientific ? Rational.ToStringMode.FORCE_FRACTION_SCIENTIFIC : Rational.ToStringMode.FORCE_FRACTION;
                        case FORCE_DECIMAL, FORCE_DECIMAL_SCIENTIFIC -> scientific ? Rational.ToStringMode.FORCE_DECIMAL_SCIENTIFIC : Rational.ToStringMode.FORCE_DECIMAL;
                    });
                }
                case "exit" -> {
                    if(var == null) throw new MathEvaluationException("Cannot delete variable 'exit'");
                }
            }
            super.put(name, var);
        }

        void setAns(Number ans) {
            super.put("ans", Expression.Function.of(ans));
        }
    }
}
