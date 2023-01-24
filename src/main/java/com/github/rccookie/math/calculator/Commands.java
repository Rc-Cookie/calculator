package com.github.rccookie.math.calculator;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.Map;

import com.github.rccookie.json.Json;
import com.github.rccookie.math.Complex;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.MathEvaluationException;
import com.github.rccookie.math.expr.MathExpressionSyntaxException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Utils;

/**
 * A collection of commands for a calculator.
 */
public final class Commands {

    /**
     * Exits using System.exit() with the value of the exit variable.
     */
    public static final Command EXIT = new LambdaCommand(
            "Exit the calculator with the exit code of the 'exit' variable",
            (c,args) -> {
                if(!c.variables().contains("exit"))
                    System.exit(0);
                try {
                    System.exit((int) c.variables().get("exit").toDouble(c.variables()));
                } catch (Exception e) {
                    if(Console.getFilter().isEnabled("debug"))
                        e.printStackTrace();
                    System.exit(-1);
                }
            }
    );
    /**
     * Resets the calculator's state.
     */
    public static final Command RESET = new LambdaCommand(
            "Reset the calculator's state",
            (c,args) -> c.loadState(new Calculator())
    );
    public static final Command PRECISION = new LambdaCommand(
            "Show or set the precision of the calculator",
            (c,args) -> {
                if(args.length == 1)
                    System.out.println(c.getPrecision());
                else if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <precision?>");
                else {
                    c.setPrecision(Integer.parseInt(args[1]));
                    System.out.println("Precision set.");
                }
            }
    );
    public static final Command SCIENTIFIC = new LambdaCommand(
            "Show or set whether to use scientific notation",
            (c,args) -> {
                if(args.length == 1)
                    System.out.println(c.isScientificNotation() ? "1" : "0");
                else if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <true/false/1/0?>");
                else {
                    boolean scientific;
                    args[1] = args[1].toLowerCase();
                    if(args[1].equals("0") || args[1].equals("false"))
                        scientific = false;
                    else if(args[1].equals("1") || args[1].equals("true"))
                        scientific = true;
                    else throw new IllegalCommandException("Usage: \\"+args[0]+" <true/false/1/0?>");
                    c.setScientificNotation(scientific);
                    System.out.println("Scientific notation "+(scientific?"enabled.":"disabled."));
                }
            }
    );
    /**
     * Lists all variables and functions currently set in the calculator, including
     * default ones.
     */
    public static final Command VARS = new LambdaCommand(
            "List all variables and functions currently set, including default ones",
            (c,args) -> c.variables().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(var -> {
                if(var.getValue() instanceof Expression.Function f) {
                    System.out.print(var.getKey() + "(" + String.join(",", f.paramNames()) + ")");
                    if(var.getKey().equals("ans") || !Calculator.DEFAULT_VARS.containsKey(var.getKey()))
                        System.out.println(" := " + f.expr());
                    else System.out.println();
                }
                else System.out.println(var.getKey() + " := " + var.getValue());
            })
    );
    /**
     * Displays the previous result with more precision.
     */
    public static final Command MORE = new LambdaCommand(
            "Display the previous result with more precision",
            (c,args) -> {
                if(c.moreCount < 10)
                    c.moreCount++;
                Number res = c.variables().get("ans");
                int precision = Rational.getPrecision();
                Rational.setPrecision(precision << c.moreCount);
                c.printRes(res, null);
                Rational.setPrecision(precision);
            }
    );
    /**
     * Displays the last result as fraction.
     */
    public static final Command FRAC = new LambdaCommand(
            "Display the last result as fraction",
            (c,args) -> {
                Number res = c.variables().get("ans");
                c.printRes(res, Rational.ToStringMode.FORCE_FRACTION);
            }
    );
    /**
     * Displays the last result as decimal.
     */
    public static final Command DEC = new LambdaCommand(
            "Display the last result as decimal",
            (c,args) -> {
                Number res = c.variables().get("ans");
                c.printRes(res, Rational.ToStringMode.FORCE_DECIMAL);
            }
    );
    /**
     * Displays the last result as binary. The last result must have been
     * an integer.
     */
    public static final Command BIN = new LambdaCommand(
            "Display the last (integer) result as binary",
            (c,args) -> {
                Number res = c.variables().get("ans");
                System.out.println(getInt(c, res).toString(2));
            }
    );
    /**
     * Displays the last result as hexadecimal. The last result must have been
     * an integer.
     */
    public static final Command HEX = new LambdaCommand(
            "Display the last (integer) result as hexadecimal",
            (c,args) -> {
                Number res = c.variables().get("ans");
                System.out.println(getInt(c, res).toString(16));
            }
    );
    /**
     * Displays the last result in a different radix. The last result must have been
     * an integer.
     */
    public static final Command RADIX = new LambdaCommand(
            "Display the last (integer) result as different radix",
            (c,args) -> {
                if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <radix>");
                Number res = c.variables().get("ans");
                int radix = Integer.parseInt(args[1]);
                System.out.println(getInt(c, res).toString(radix));
            }
    );
    /**
     * Deletes one or more variables from the calculator.
     */
    public static final Command DELETE = new LambdaCommand(
            "Delete one or more variables",
            (c,args) -> {
                if(args.length == 1)
                    throw new IllegalCommandException("Usage: \\" + args[0] + " <var [var2 var3...]>");
                int startCount = c.variables().entrySet().size();
                for(int i=1; i<args.length; i++)
                    c.variables().delete(args[i]);
                int delCount = startCount - c.variables().entrySet().size();
                System.out.println("Deleted "+delCount+" variable"+(delCount==1?"":"s")+".");
            }
    );
    /**
     * Loads a {@link FormulaPackage} from the jar resources /packages or the packages directory
     * on the same level as the jar file.
     */
    public static final Command LOAD = new LambdaCommand(
            "Load a package of variables and functions",
            (c,args) -> {
                if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <packageName>, i.e. \\"+args[0]+" physics");
                FormulaPackage pkg = FormulaPackage.load(args[1]);
                pkg.addTo(c.variables());
                System.out.println("Loaded " + pkg.size() + " entries.");
            }
    );
    /**
     * Stores the current state of the calculator under a specific name.
     */
    public static final Command STORE = new LambdaCommand(
            "Store the current state of the calculator under a specific name",
            (c,args) -> {
                if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <packageName>, i.e. \\"+args[0]+" physics");
                FormulaPackage pkg = FormulaPackage.load(args[1]);
                pkg.addTo(c.variables());
                System.out.println("Loaded " + pkg.size() + " entries.");
            }
    );
    /**
     * Restores the state of the last quit calculator instance, or a named
     * calculator state saved with {@link #STORE}.
     */
    public static final Command RESTORE = new LambdaCommand(
            "Restore the last or a manually saved state of the calculator",
            (c,args) -> {
                if(args.length == 1) try { // Restore from latest restore save
                    Path latest = c.getLatestRestoreStateAndClean();
                    if(latest == null) throw new IllegalCommandException("No recent state to restore");
                    c.loadState(Json.load(latest.toFile()).as(Calculator.class));
                    System.out.println("Last calculator state restored.");
                    return;
                } catch(Exception e) {
                    throw new IllegalCommandException("No recent state to restore", e);
                }
                if(args.length != 2)
                    throw new IllegalCommandException("Usage: \\"+args[0]+" <state name>");
                if(args[1].contains("/") || args[1].contains("\\"))
                    throw new IllegalCommandException("Illegal name");
                try {
                    c.loadState(Json.load(new File(Calculator.STATE_STORE_DIR, args[1] + ".json")).as(Calculator.class));
                } catch (Exception e) {
                    throw new IllegalCommandException("Could not find state '"+args[1]+"'", e);
                }
                System.out.println("Calculator state restored.");
            }
    );



    private Commands() { }



    private static BigInteger getInt(Calculator calculator, Number n) {
        if(n instanceof Expression.Function f && f.paramCount() == 0) try {
            n = f.evaluate(calculator.variables(), Expression.Numbers.EMPTY);
        } catch(MathEvaluationException|MathExpressionSyntaxException e) {
            Console.debug("Failed to evaluate zero-parameter function");
            Console.debug(Utils.getStackTraceString(e));
        }
        if(n instanceof Rational r && r.d.equals(BigInteger.ONE))
            return r.n;
        if(n instanceof Complex c && c.isReal() && c.re instanceof Rational r && r.d.equals(BigInteger.ONE))
            return r.n;
        throw new MathExpressionSyntaxException("Cannot calculate radix rendering for non-integer");
    }



    static final class LambdaCommand extends Command {

        private final CommandLambda command;

        LambdaCommand(String description, CommandLambda command) {
            super(description);
            this.command = Arguments.checkNull(command, "command");
        }

        @Override
        public void invoke(Calculator calculator, String[] args) throws IllegalCommandException {
            command.invoke(calculator, args);
        }
    }

    @FunctionalInterface
    interface CommandLambda {
        void invoke(Calculator calculator, String[] args) throws IllegalCommandException;
    }
}
