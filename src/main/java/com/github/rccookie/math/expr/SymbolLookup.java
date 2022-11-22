package com.github.rccookie.math.expr;

import com.github.rccookie.math.Number;
import com.github.rccookie.math.Rational;

public interface SymbolLookup {

    Number UNSPECIFIED = new Rational(0);
    Expression UNSPECIFIED_EXPR = Expression.of(UNSPECIFIED);



    Number get(String name);

    void put(String name, Number value);

    void pushLocal(String name, Number value);

    void popLocal(String name);
}
