package com.github.rccookie.math.calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.github.rccookie.json.JsonObject;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Real;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.Functions;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Utils;
import com.github.rccookie.util.config.Config;

public class Calculator {

    private static final JsonObject DEFAULT_SETTINGS = new JsonObject(
            "precision", 50,
            "scientific", false
    );


    private static final Map<String, Number> DEFAULT_VARS = Utils.map(
            "pi", Number.PI(),
            "e", Number.E(),
            "dec", Number.ABOUT_ONE(),
            "ans", new Rational(42),

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
            "cross", Functions.CROSS,
            "deg", Functions.RAD_TO_DEG,
            "rad", Functions.DEG_TO_RAD,
            "sum", Functions.SUM,
            "\u03A3", Functions.SUM,
            "product", Functions.PRODUCT,
            "\u03A0", Functions.PRODUCT
    );
    private static final Map<String, Number> OPTIONAL_DEFAULT_VARS = Utils.map(
            "precision", new Rational(Real.getPrecision()),
            "scientific", Real.SCIENTIFIC_NOTATION ? Number.ONE() : Number.ZERO(),
            "g", new Real(9.81, false, false),
            "c", new Rational(299792458),
            "h", new Real(6.62607015, -34, true, false),
            "E", new Real(1.602176634, -19, true, false),
            "m_e", new Real(9.109383701528, -31, false, false),
            "N_a", new Real(6.02214076, 23, false, false),
            "\u00B50", new Real(1.2566370621219, -6, false, false),
            "ep_0", new Real(8.854187812813, -12, false, false),
            "k", new Real(1.380649, -23, false, false),
            "G", new Real(6.673, -11, false, false)
    );

    private final Lookup lookup = new Lookup();
    private String lastExpr = "ans";

    public Number getVar(String name) {
        return lookup.get(name);
    }

    public void setVar(String name, Number var) {
        lookup.put(name, var);
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
        Expression expr = Expression.parse(expression);
        Console.debug("Expression:");
        Console.debug(expr);
        Console.debug(expr.toTreeString());
        Number ans = expr.evaluate(lookup);
        lastExpr = expression;
        lookup.variables.put("ans", ans);
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
                        Java math interpreter - version 2.3
                        By RcCookie""");
        parser.setDescription("Evaluate entered math expressions. Evaluate '\\help' to show expressions help");
        parser.parse(args);

        System.out.println("""
                        Java math interpreter - version 2.3
                        By RcCookie
                        -----------------------------------""");

        if(System.getProperty("os.name").toLowerCase().contains("windows"))
            checkReg();

        Calculator calculator = new Calculator();
        calculator.setVar("exit", new Rational(0));

        Config config = Config.fromAppdataPath("Calculator");
        try {
            config.setDefaults(DEFAULT_SETTINGS);
            calculator.setVar("precision", new Rational(config.getInt("precision")));
            calculator.setVar("scientific", config.getBool("scientific") ? Number.ONE() : Number.ZERO());
        } catch(Exception e) {
            System.err.println("Failed to load settings");
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        }

        //noinspection InfiniteLoopStatement
        while(true) {
            System.out.print("> ");
            evalInput(calculator, Console.in.readLine());

            config.set("precision", Real.getPrecision());
            config.set("scientific", Real.SCIENTIFIC_NOTATION);
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
                if(res instanceof Expression && !(res instanceof Expression.Numbers))
                    System.out.println(res);
                else
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
                    System.exit((int) calculator.getVar("exit").toDouble(calculator.lookup));
                } catch(Exception e) {
                    if(Console.getFilter().isEnabled("debug"))
                        e.printStackTrace();
                    System.exit(-1);
                }
            }
            case "vars" -> calculator.lookup.variables.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(var -> {
                if(var.getValue() instanceof Expression.Function f) {
                    System.out.print(var.getKey() + "(" + String.join(",", f.paramNames()) + ")");
                    if(!DEFAULT_VARS.containsKey(var.getKey()))
                        System.out.println(" := " + f.expr());
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



    private static class Lookup implements SymbolLookup {

        private final Map<String, Number> variables = new HashMap<>();
        {
            variables.putAll(DEFAULT_VARS);
            variables.putAll(OPTIONAL_DEFAULT_VARS);
        }

        private final Map<String, Stack<Number>> localVariables = new HashMap<>();

        @Override
        public Number get(String name) {
            Stack<Number> localVars = localVariables.get(name);
            if(localVars != null) return localVars.peek();

            Number var = variables.get(name);
            if(var == null)
                throw new IllegalArgumentException("Unknown variable or function: '" + name + "'");
            return var instanceof Real r ? new Real(r.value, r.precise) : var; // Round high-precision constants
        }

        @Override
        public void pushLocal(String name, Number var) {
            localVariables.computeIfAbsent(name, n -> new Stack<>()).push(var);
        }

        @Override
        public void popLocal(String name) {
            Stack<Number> localVars = localVariables.get(name);
            if(localVars == null) {
                Console.warn("Removing non-existent local variable");
                return;
            }
            localVars.pop();
            if(localVars.isEmpty())
                localVariables.remove(name);
        }

        @Override
        public void put(String name, Number var) {
            if(DEFAULT_VARS.containsKey(name))
                throw new IllegalArgumentException("Cannot override variable '"+name+"'");
            if(name.equals("precision")) {
                double p = var.toDouble(this);
                if(p < 2)
                    throw new IllegalArgumentException("precision < 2");
                Real.setPrecision((int) p);
                var = new Rational((int) p);
            }
            else if(name.equals("scientific"))
                Real.SCIENTIFIC_NOTATION = !var.equals(Number.ZERO());
            variables.put(name, Arguments.checkNull(var, "var"));
        }
    }
}
