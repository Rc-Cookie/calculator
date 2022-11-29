package com.github.rccookie.math.calculator;

import java.util.Map;
import java.util.function.BiConsumer;

import com.github.rccookie.math.Number;
import com.github.rccookie.util.Utils;

record FormulaPackageImpl(Map<String, Number> formulas) implements FormulaPackage {

    FormulaPackageImpl(Map<String, Number> formulas) {
        this.formulas = Map.copyOf(formulas);
    }

    FormulaPackageImpl(Object... namesAndFormulas) {
        this(Utils.mapSafe(String.class, Number.class, namesAndFormulas));
    }

    @Override
    public String toString() {
        return formulas.toString();
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Number> action) {
        formulas.forEach(action);
    }
}
