package com.github.rccookie.math.calculator;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonSerializable;
import com.github.rccookie.math.Number;
import com.github.rccookie.math.expr.SymbolLookup;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.UncheckedException;

public interface FormulaPackage extends JsonSerializable {

    Object _ignore = registerJson();

    private static Object registerJson() {
        JsonDeserialization.register(FormulaPackage.class, FormulaPackageImpl::load);
        return null;
    }


    void forEach(BiConsumer<? super String, ? super Number> action);


    default void addTo(SymbolLookup lookup) {
        forEach(lookup::put);
    }

    @Override
    default Object toJson() {
        JsonObject json = new JsonObject();
        forEach((n,v) -> json.put(n, v.toString()));
        return json;
    }

    static FormulaPackage load(String name) {
        name = name.toLowerCase();
        Path jarDirPath;
        try {
            jarDirPath = Path.of(FormulaPackage.class.getProtectionDomain().getCodeSource().getLocation().toURI()).resolve("..");
            Console.debug(jarDirPath);
        } catch (URISyntaxException e) {
            throw new UncheckedException(e);
        }
        if(Files.exists(jarDirPath.resolve("packages/" + name + ".json")))
            return Json.load(jarDirPath.resolve("packages/" + name + ".json").toFile()).as(FormulaPackage.class);
        InputStream in = FormulaPackage.class.getResourceAsStream("/packages/"+name+".json");
        if(in == null) throw new IllegalArgumentException("Package '"+name+"' not found");
        return Json.parse(in).as(FormulaPackage.class);
    }
}

