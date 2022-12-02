package com.github.rccookie.math.calculator;

import java.util.Map;
import java.util.function.BiConsumer;

import com.github.rccookie.math.Rational;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.Expression;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Utils;

public interface FormulaPackage {

    FormulaPackage PHYSICS = new FormulaPackageImpl(
            "g", new Rational(9.81, false),
            "c", new Rational(299792458),
            "h", new Rational(6.62607015).multiply(Rational.TEN.raise(-34)),
            "E", new Rational(1.602176634).multiply(Rational.TEN.raise(-19)),
            "m_e", new Rational(9.109383701528, -31, false, false),
            "N_a", new Rational(6.02214076, 23, false, false),
            "\u00B50", new Rational(1.2566370621219, -6, false, false),
            "ep_0", new Rational(8.854187812813, -12, false, false),
            "k", new Rational(1.380649, -23, false, false),
            "G", new Rational(6.673, -11, false, false),

            "F_g", Expression.parse("(m1,m2,d) -> -G * m1 * m2 / (d*d) * (norm(d))"),
            "F_z", Expression.parse("(m,r,v) -> m * v * v / r"),
            "F_s", Expression.parse("(D,dx) -> -D / dx"),

            "F_cw", Expression.parse("(A,cw,rho,v) -> -A/2 * cw * rho * v * v"),
            "F_r", Expression.parse("(F_n,\u00B5) -> |F_n * \u00B5|"),

            "lorenz", Expression.parse("v -> 1 / (sqrt(1 - (v/c)^2)")
    );

    Map<String, FormulaPackage> PACKAGES = Utils.map(
            "physics", PHYSICS
    );


    void forEach(BiConsumer<? super String, ? super Number> action);


    default void addTo(SymbolLookup lookup) {
        forEach(lookup::put);
    }
}
