package com.github.rccookie.math.calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.github.rccookie.json.JsonElement;
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


    static FormulaPackage load(JsonElement json) {
        Map<String, Number> formulas = new HashMap<>();
        json.forEach((n,v) -> formulas.put(n, v.as(Number.class)));
//        json.forEach((name, valueObj) -> {
//            boolean precise = !valueObj.isObject() || valueObj.get("precise").or(true);
//            if(valueObj.isObject() && valueObj.containsKey("value"))
//                valueObj = valueObj.get("value");
//
//            Number value;
//            if(valueObj.isNumber()) {
//                if(valueObj.asNumber().toString().contains("."))
//                    value = new Rational(valueObj.asDouble(), 1, precise, false);
//                else value = new Rational(valueObj.asLong(), precise);
//            }
//            else if(valueObj.isString())
//                value = Expression.parse(valueObj.asString());
//            else if(valueObj.containsKey("n")) {
//                value = new Rational(
//                        new BigInteger(valueObj.get("n").toString()),
//                        new BigInteger(valueObj.get("d").toString()),
//                        precise
//                );
//            }
//            else
//                value = new Rational(new BigDecimal(valueObj.get("factor").toString()), valueObj.get("exp").asInt(), precise, false);
//
//            formulas.put(name, value);
//        });
        return new FormulaPackageImpl(formulas);
    }
}

