package com.github.rccookie.math.calculator;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.util.Console;

public final class Updater {

    private Updater() {
    }

    public static void main(String[] args) throws Exception {

        Console.Config.colored = false;

        String newExe = args[0], oldExe = args[1], self = args[2];
        boolean debug = args.length > 3 && args[3].equals("true");
        Console.getDefaultFilter().setEnabled("debug", debug);

        Console.map("newExe", newExe);
        Console.map("oldExe", oldExe);
        Console.map("self", self);

        Console.log("Waiting for host process to terminate...");
        long time = System.nanoTime();
        do {
            try {
                Files.move(Path.of(newExe), Path.of(oldExe), StandardCopyOption.REPLACE_EXISTING);
                break;
            } catch(AccessDeniedException ignored) { }
        } while(System.nanoTime() < time + 2000);
        Console.log("Executable replaced");

        List<String> cmd = new ArrayList<>();
        if(System.getProperty("os.name").toLowerCase().contains("win"))
            cmd.addAll(List.of("cmd", "/c", "start"));

        cmd.add(oldExe);
        if(debug) cmd.add("--debug");
        cmd.addAll(List.of("--restore", "--delete-update-jar", self));

        Runtime.getRuntime().exec(cmd.toArray(new String[0]));

        Console.log("New calculator launched");
    }
}
