package com.github.rccookie.math.calculator;

import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.util.Console;

public final class Updater {

    private Updater() {
    }

    public static void main(String[] args) throws Exception /* only for Thread.join() */ {
        Console.Config.colored = false; // elevate.cmd spawns old terminal host even if Windows Terminal is set as default, which probably doesn't support Ansi
        Console.Config.width = 100000; // Don't wrap cmd string in debug output

        System.out.println("Calculator update tool\n" +
                           "----------------------\n");

        String newExe = args[0], oldExe = args[1], self = args[2];
        boolean debug = args.length > 3 && args[3].equals("true");
        Console.getDefaultFilter().setEnabled("debug", debug);

        Console.mapDebug("newExe", newExe);
        Console.mapDebug("oldExe", oldExe);
        Console.mapDebug("self", self);

        try {

            Path oldExePath = Path.of(oldExe);

            System.out.println("Waiting for other calculator processes to terminate...");
            long time = System.nanoTime();
            boolean msg = false;
            do {
                try {
                    Files.move(Path.of(newExe), oldExePath, StandardCopyOption.REPLACE_EXISTING);
                    break;
                } catch (FileSystemException ignored) { }

                if(System.nanoTime() > time + 2000) {
                    if(msg != (msg = true))
                        System.err.println("\nPLEASE ENSURE ALL CALCULATOR INSTANCES ARE CLOSED!\n");
                    Thread.sleep(100);
                }
            } while (true);
            System.out.println("Update completed.");

            List<String> cmd = new ArrayList<>();
            if(System.getProperty("os.name").toLowerCase().contains("win"))
                cmd.addAll(List.of("cmd", "/c", "start", "/d", '"' + oldExePath.getParent().toRealPath().toString() + '"', oldExePath.getFileName().toString()));
            else cmd.add('"' + oldExe + '"');

            if(debug) cmd.add("--debug");
            cmd.addAll(List.of("--restore", "--delete-update-jar", self));

            Console.mapDebug("Cmd", String.join(" ", cmd));
            Runtime.getRuntime().exec(cmd.toArray(new String[0]));

            Console.debug("New calculator launched");

            if(debug) Thread.currentThread().join(); // Don't close window immediately
        } catch(Exception e) {
            System.err.println("Update failed.");
            if(debug) e.printStackTrace();
            Thread.currentThread().join();
        }
    }
}
