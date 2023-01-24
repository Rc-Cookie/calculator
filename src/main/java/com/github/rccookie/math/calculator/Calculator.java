package com.github.rccookie.math.calculator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonSerializable;
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
import com.github.rccookie.util.Args;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.UncheckedException;
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
public class Calculator implements JsonSerializable {

    /**
     * Version of this calculator API.
     */
    public static final String VERSION = "2.8";

    static final String STATE_STORE_DIR = Utils.getAppdata() + "/calculator/states";
    private static final Path RECENT_STATE_DIR = Path.of(STATE_STORE_DIR, "_recent");

    private static final JsonObject DEFAULT_SETTINGS = new JsonObject(
            "precision", 50,
            "scientific", true,
            "autoUpdate", true
    );

    private static final long PID = ProcessHandle.current().pid();


    /**
     * Variables set by default in a calculator, that cannot be changed.
     */
    public static final Map<String, Number> DEFAULT_VARS = Map.copyOf(Utils.map(
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
    ));
    private static final Map<String, Number> OPTIONAL_DEFAULT_VARS = Utils.map(
            "precision", new Rational(Rational.getPrecision()),
            "scientific", switch(Rational.getToStringMode()) {
                case SMART, DECIMAL_IF_POSSIBLE, FORCE_FRACTION, FORCE_DECIMAL -> Number.ZERO();
                default -> Number.ONE();
            },
            "k", new Rational(1000)
    );

    /**
     * Commands registered to a calculator by default. Can be overridden or removed!
     */
    public static final Map<String, Command> DEFAULT_COMMANDS;
    static {
        Map<String, Command> cmds = new LinkedHashMap<>();
        cmds.put("exit", Commands.EXIT);
        cmds.put("reset", Commands.RESET);
        cmds.put("precision", Commands.PRECISION);
        cmds.put("scientific", Commands.SCIENTIFIC);
        cmds.put("vars", Commands.VARS);
        cmds.put("more", Commands.MORE);
        cmds.put("frac", Commands.FRAC);
        cmds.put("dec", Commands.DEC);
        cmds.put("bin", Commands.BIN);
        cmds.put("hex", Commands.HEX);
        cmds.put("radix", Commands.RADIX);
        cmds.put("del", Commands.DELETE);
        cmds.put("delete", Commands.DELETE);
        cmds.put("load", Commands.LOAD);
        cmds.put("store", Commands.STORE);
        cmds.put("restore", Commands.RESTORE);
        DEFAULT_COMMANDS = Utils.view(cmds);
    }

    private static final char ABOUT_EQUAL = Charset.defaultCharset().newEncoder().canEncode('\u2248') ? '\u2248' : '~';

    private final Lookup lookup = new Lookup();
    private String lastExpr = "ans";
    private int precision = 50;
    private boolean scientificNotation = true;
    int moreCount = 0;


    private final Map<String, Command> commands = new LinkedHashMap<>(DEFAULT_COMMANDS);


    /**
     * Returns a mutable reference to the variables in the calculator. Some variable names
     * are protected for modification and will cause a {@link MathEvaluationException} to
     * be thrown when attempted to be modified. Subsequent changes to the variables in the
     * calculator will be reflected in the returned lookup, and vice versa.
     *
     * @return The variables in this calculator
     */
    public SymbolLookup variables() {
        return lookup;
    }


    /**
     * Registers the specified command under the given name. If there was a different command
     * registered for that name, it will be replaced.
     *
     * @param name Name of the command (case-insensitive)
     * @param command The command to be registered
     */
    public void registerCommand(String name, Command command) {
        commands.put(Arguments.checkNull(name, "name").toLowerCase(),
                     Arguments.checkNull(command, "command"));
    }

    /**
     * Removes the command registered for the specified name, if any.
     *
     * @param name Name of the command (case-insensitive)
     * @return Whether a command was previously registered for that name
     */
    public boolean removeCommand(String name) {
        return commands.remove(Arguments.checkNull(name, "name").toLowerCase()) != null;
    }

    /**
     * Returns a view of all commands registered in this calculator. All names
     * are in lower case.
     *
     * @return A view of the commands in this calculator
     */
    public Map<String, Command> commands() {
        return Utils.view(commands);
    }


    /**
     * Returns whether the calculator displays in scientific notation.
     *
     * @return Whether the calculator displays in scientific notation
     */
    public boolean isScientificNotation() {
        return scientificNotation;
    }

    /**
     * Sets whether the calculator should display in scientific notation.
     *
     * @param scientificNotation Whether to use scientific notation
     */
    public void setScientificNotation(boolean scientificNotation) {
        this.scientificNotation = scientificNotation;
    }

    /**
     * Returns the precision setting of this calculator.
     *
     * @return The calculators precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets the precision of the calculator.
     *
     * @param precision The precision to use, >= 2
     */
    public void setPrecision(int precision) {
        this.precision = Arguments.checkRange(precision, 2, null);
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
        return runWithSettings(() -> {
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
        });
    }

    /**
     * Loads the exact state (variables, history) of the specified calculator,
     * discarding the current state of this calculator.
     *
     * @param calculator The calculator to copy
     */
    public void loadState(Calculator calculator) {
        Arguments.checkNull(calculator, "calculator");

        for(Map.Entry<String, Number> entry : Set.copyOf(lookup.entrySet())) {
            if(entry.getKey().equals("exit") || entry.getKey().equals("scientific") ||
               entry.getKey().equals("precision")|| DEFAULT_VARS.containsKey(entry.getKey()))
                continue;
            lookup.delete(entry.getKey());
        }

        calculator.lookup.entrySet().stream()
                .filter(e -> !e.getKey().equals("exit") && !DEFAULT_VARS.containsKey(e.getKey()))
                .forEach(e -> lookup.put(e.getKey(), e.getValue()));
        lookup.setAns(calculator.lookup.get("ans"));
        lastExpr = calculator.lastExpr;
        setPrecision(calculator.getPrecision());
        setScientificNotation(calculator.isScientificNotation());
        moreCount = calculator.moreCount;
    }


    /**
     * Evaluates the specified input, expressions or commands separated
     * by semicolons, and prints the result into the standard output
     * stream. A value of <code>null</code> will invoke the exit command.
     *
     * @param expressions The expressions to evaluate
     */
    public void evalInput(String expressions) {
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
        } catch(MathEvaluationException|IllegalCommandException e) {
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


    public void evalCommand(String cmd) {
        Console.mapDebug("Received command", cmd);
        String[] cmds = cmd.split("\s+");
        cmds[0] = cmds[0].toLowerCase();

        if(cmds[0].equals("help")) {
            System.out.println("""
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
                     - Use the variable 'ans' to refer to the previous result, or operate as if it was at the front of the expression
                     - Set the variable 'precision' to set the approximate decimal number precision (>1, default is 100)
                     - Set the variable 'scientific' to something other than 0 to enable scientific notation output
                     - Use commands:""");
            commands.forEach((n,c) -> System.out.println("   \\" + n + ": " + c.getDescription()));
        }
        else {
            Command command = commands.get(cmds[0]);
            if(command != null)
                runWithSettings(() -> command.invoke(this, cmds));
            else throw new IllegalCommandException("Unknown command: "+cmds[0]);
        }
    }


    private void runWithSettings(Runnable code) {
        runWithSettings(() -> { code.run(); return null; });
    }
    private <T> T runWithSettings(Supplier<T> code) {
        int precision = Rational.getPrecision();
        Rational.ToStringMode toStringMode = Rational.getToStringMode();

        Rational.setPrecision(this.precision);
        Rational.setToStringMode(switch(Rational.getToStringMode()) {
            case SMART, SMART_SCIENTIFIC -> scientificNotation ? Rational.ToStringMode.SMART_SCIENTIFIC : Rational.ToStringMode.SMART;
            case DECIMAL_IF_POSSIBLE, DECIMAL_IF_POSSIBLE_SCIENTIFIC -> scientificNotation ? Rational.ToStringMode.DECIMAL_IF_POSSIBLE_SCIENTIFIC : Rational.ToStringMode.DECIMAL_IF_POSSIBLE;
            case FORCE_FRACTION, FORCE_FRACTION_SCIENTIFIC -> scientificNotation ? Rational.ToStringMode.FORCE_FRACTION_SCIENTIFIC : Rational.ToStringMode.FORCE_FRACTION;
            case FORCE_DECIMAL, FORCE_DECIMAL_SCIENTIFIC -> scientificNotation ? Rational.ToStringMode.FORCE_DECIMAL_SCIENTIFIC : Rational.ToStringMode.FORCE_DECIMAL;
        });

        try {
            return code.get();
        } finally {
            Rational.setPrecision(precision);
            Rational.setToStringMode(toStringMode);
        }
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
        parser.addOption('r', "restore", null, "Restore the last state or a state saved using \\store");
        parser.setName("Java math interpreter - version " + VERSION + "\nBy RcCookie");
        parser.setDescription("""

                        Usage: math [--options] [expression]
                        Evaluate entered math expressions. Evaluate '\\help' to show expressions help.""");

        Args options = parser.parse(args);

        if(System.getProperty("os.name").toLowerCase().contains("windows"))
            checkReg();

        Calculator calculator = new Calculator();
        if(options.is("restore"))
            Commands.RESTORE.invoke(calculator,options.get("restore").equals("true") ?
                    new String[] { "restore" } : new String[] { "restore", options.get("restore") });

        calculator.variables().put("exit", new Rational(0));
        Rational.setToStringMode(Rational.ToStringMode.SMART_SCIENTIFIC);


        String argsStr = options.getArgsString();
        if(!argsStr.isBlank()) {
            calculator.evalInput(argsStr);
            calculator.evalCommand("exit");
        }



        System.out.println("Java math interpreter - version " + VERSION + """
                            
                            By RcCookie
                            -----------------------------------""");

        try {
            Config config = Config.fromAppdataPath("calculator");
            config.setDefaults(DEFAULT_SETTINGS);

            Calculator actual = new Calculator() {
                @Override
                public void setPrecision(int precision) {
                    super.setPrecision(precision);
                    config.set("precision", precision);
                }

                @Override
                public void setScientificNotation(boolean scientificNotation) {
                    super.setScientificNotation(scientificNotation);
                    config.set("scientific", scientificNotation);
                }
            };

            calculator.setPrecision(config.getInt("precision"));
            calculator.setScientificNotation(config.getBool("scientific"));
            actual.loadState(calculator);
            calculator = actual;

            if(config.getBool("autoUpdate"))
                CalculatorUpdateChecker.update(2000, true);

            calculator.registerCommand("autoUpdate", new Commands.LambdaCommand(
                    "Test or set whether to automatically check for updates",
                    (c,cmds) -> {
                        if(cmds.length == 1)
                            System.out.println("Automatic update checks are " +
                                               (config.getBool("autoUpdate") ? "enabled." : "disabled."));
                        else if(cmds.length != 2)
                            throw new IllegalCommandException("Usage: \\"+cmds[0]+" <true/false/1/0?>");
                        else {
                            boolean autoUpdate;
                            cmds[1] = cmds[1].toLowerCase();
                            if(cmds[1].equals("0") || cmds[1].equals("false"))
                                autoUpdate = false;
                            else if(cmds[1].equals("1") || cmds[1].equals("true"))
                                autoUpdate = true;
                            else throw new IllegalCommandException("Usage: \\"+cmds[0]+" <true/false/1/0?>");
                            config.set("autoUpdate", autoUpdate);
                            System.out.println("Automatic update checks "+(autoUpdate?"enabled.":"disabled."));
                        }
                    }
            ));
            calculator.registerCommand("update", new Commands.LambdaCommand(
                    "Check for updates", (c,cmds) -> CalculatorUpdateChecker.update(5000, false)));
        } catch(Exception e) {
            System.err.println("Failed to load settings");
            if(Console.getFilter().isEnabled("debug"))
                e.printStackTrace();
        }

        // Remove unused restore state saves
        calculator.getLatestRestoreStateAndClean();

        //noinspection InfiniteLoopStatement
        while(true) {
            System.out.print("> ");
            calculator.evalInput(Console.in.readLine());
            calculator.storeRestoreState();
        }
    }

    /**
     * Prints a number formatted as result into the standard output stream.
     *
     * @param res The number to display
     * @param mode A display mode override, or <code>null</code> to use the default
     */
    protected void printRes(Number res, Rational.ToStringMode mode) {
        runWithSettings(() -> {
            Number n = res;
            while (n instanceof Expression.Function f && f.paramCount() == 0) try {
                Number val = f.evaluate(lookup, Expression.Numbers.EMPTY);
                if(n.equals(val)) break;
                n = val;
            } catch (Exception e) {
                Console.debug("Failed to evaluate parameter-less function, displaying as function:");
                Console.debug(Utils.getStackTraceString(e));
                break;
            }

            if(n instanceof Expression && !(n instanceof Expression.Numbers))
                System.out.print(n);
            else if(n instanceof Rational r) {
                Rational.DetailedToString str = mode != null ? r.detailedToString(mode) : r.detailedToString();
                System.out.print(str.precise() ? '=' : ABOUT_EQUAL);
                System.out.print(' ');
                if(str.precise() && !str.isFull()) {
                    int index = str.str().indexOf("\u00B7");
                    if(index == -1) System.out.print(str.str() + "...");
                    else System.out.print(str.str().substring(0, index) + "..." + str.str().substring(index));
                } else System.out.print(str.str());
            } else if(n instanceof SimpleNumber sn && !sn.precise() || n instanceof Complex c && !c.precise())
                System.out.print(ABOUT_EQUAL + " " + n);
            else System.out.print("= " + n);
            System.out.println();
        });
    }

    private void storeRestoreState() {
        synchronized(Calculator.class) {
            try {
                Files.createDirectories(Path.of(STATE_STORE_DIR, "_recent"));
                Json.store(this, new File(STATE_STORE_DIR+"/_recent/"+PID+".json"));
            } catch (Exception e) {
                System.err.println("Failed to store calculator state for restoring");
                if(Console.getFilter().isEnabled("debug"))
                    Console.error(e);
            }
        }
    }

    Path getLatestRestoreStateAndClean() {
        try {
            Path latest = null;
            Files.createDirectories(RECENT_STATE_DIR);
            try (var s = Files.newDirectoryStream(Path.of(STATE_STORE_DIR, "_recent"), "*.json")) {
                for (Path p : s) {
                    String f = p.getFileName().toString();
                    if(f.equals(PID + ".json")) continue; // Don't restore your own state
                    // Is the process with that id still running and is probably
                    // a calculator instance?
                    long pid = Long.parseLong(f.substring(0, f.length() - 5));
                    if(ProcessHandle.of(pid)
                            .map(ph -> ph.info().command().orElse("math"))
                            .map(n -> n.contains("math") || n.contains("java"))
                            .orElse(false)) continue;

                    if(latest == null) latest = p;
                        // Delete all non-latest restore files, will never be chosen again
                    else if(Files.getLastModifiedTime(latest).compareTo(Files.getLastModifiedTime(p)) < 0) {
                        Files.delete(latest);
                        latest = p;
                    } else Files.delete(p);
                }
            }
            return latest;
        } catch(Exception e) {
            throw new UncheckedException(e);
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

    @Override
    public Object toJson() {
        JsonObject vars = new JsonObject();
        lookup.entrySet().stream()
                .filter(e -> !e.getKey().equals("ans") && !DEFAULT_VARS.containsKey(e.getKey()))
                .forEach(e -> vars.put(e.getKey(), e.getValue()));
        return new JsonObject(
                "vars", vars,
                "ans", lookup.get("ans"),
                "lastExpr", lastExpr,
                "precision", precision,
                "scientificNotation", scientificNotation,
                "moreCount", moreCount,
                "version", VERSION
        );
    }

    static {
        JsonDeserialization.register(Calculator.class, json -> {
            if(!VERSION.equals(json.get("version").orNull()))
                Console.warn("Loading calculator with different version (current: {}, loading: {})",
                        VERSION, json.get("version").or("Unspecified"));
            Calculator calculator = new Calculator();
            json.get("vars").forEach((n,v) -> calculator.lookup.put(n, v.as(Number.class)));
            calculator.lookup.setAns(json.get("ans").as(Number.class));
            calculator.lastExpr = json.get("lastExpr").asString();
            calculator.setPrecision(json.get("precision").asInt());
            calculator.setScientificNotation(json.get("scientificNotation").asBool());
            calculator.moreCount = json.get("moreCount").asInt();
            return calculator;
        });
    }


    private class Lookup extends DefaultSymbolLookup {

        {
            DEFAULT_VARS.forEach(super::put);
            OPTIONAL_DEFAULT_VARS.forEach(super::put);
        }

        @Override
        public Number get(String name) {
            Number var = super.get(name);
            return var instanceof Rational r && !r.precise ? runWithSettings(() -> new Rational(r.toBigDecimal(), false)) : var; // Round high-precision constants
        }

        @Override
        public void put(String name, @Nullable Number var) {
            if(DEFAULT_VARS.containsKey(name))
                throw new MathEvaluationException("Cannot override variable '"+name+"'");
            super.put(name, var);
        }

        void setAns(Number ans) {
            super.put("ans", ans);
        }
    }
}
