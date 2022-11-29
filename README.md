# Calculator

#### By RcCookie

---

A simple command-line calculator with support for several features:

 - Basic arithmetics (`+-*/^!`)
 - Fraction expressions and calculations
 - Implicit multiplication (omit multiplication sign, i.e. `2(3+4)` = `2*(3+4)`
 - Comparisons (`< <= = >= >`) returning `0` or `1`
 - Vectors: Declare with brackets, split arguments with commas, i.e. `[1,2,3]`
 - Function calls as usual, i.e. `f(2)`. List default functions using `\vars`
 - Variable declarations: declare using `:=`, i.e. `x := 42`
 - Function declarations: declare using `:=`, i.e. `f(x) := 2x`
 - Anonymous function declarations aka lambdas: declare using ->, i.e. `f := x -> 2x`
 - First class functions: functions (particularly lambdas) may be passed to other functions
 - Function arithmetics: operating on functions like numbers will create derived functions
 - Function compositions: Using `f(g)` or `f g` defines the composition "f after g"
 - Convert degrees to radians when writing `°` symbol, i.e. `180°` -> pi (may not be supported due to some terminals not properly parsing '°' character)
 - Convert percentage to normal number when writing `%` symbol, i.e. `10%` -> `1/10`
 - Load packages of constants and formulas using `\load <name>`, i.e. `\load physics`
 - Use the variable `ans` to refer to the previous result, or operate as if it was at the front of the expression
 - Set the variable `precision` to set the approximate decimal number precision (>`1`)
 - Set the variable `scientific` to something other than `0` to enable scientific notation output
 - Set the variable `showAsDecimal` to display fractions that can be converted to decimals as such, and the command `\more` to display the last output with higher decimal count
 - Set the variable `exit` to a desired value to set the exit code of the program, exit with `\exit`

The API to parse math expressions can also be used without the calculator.

---

Currently requires Java 18 with flag `--enable-preview`. Switch pattern matching is pretty convenient for this kind of stuff :)

For executing on Windows, you should first set the console charset to Windows-1512 for ² and similar to work. Then, start the program with the additional flag `-Dfile.encoding=windows-1512`. For conveniance, you may create a cmd file:

```batch
@echo off
chcp 1512
java -jar --enable-preview -Dfile.encoding=1512 "<path to jar>"
```
