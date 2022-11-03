# Calculator

#### By RcCookie

---

A simple command-line-calculator with support for several features:

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
 - Convert degrees to radians when writing `°` symbol, i.e. `180°` -> pi (may not be supported due to some terminals not properly parsing '°' character)
 - Convert percentage to normal number when writing `%` symbol, i.e. `10%` -> `1/10`
 - Use the variable '`ans`' to refer to the previous result, or operate as if it was at the front of the expression
 - Set the variable '`precision`' to set the approximate decimal number precision (>`1`, default is `100`)
 - Set the variable '`exit`' to a desired value to set the exit code of the program, exit with `\exit`

---

Currently requires Java 18 with flag `--enable-preview`. Switch pattern matching is pretty convenient :)
