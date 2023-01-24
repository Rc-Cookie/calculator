package com.github.rccookie.math.calculator;

import com.github.rccookie.http.HttpRequest;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ThreadedFutureImpl;
import com.github.rccookie.util.Utils;

import org.jetbrains.annotations.NotNull;

final class CalculatorUpdateChecker extends Thread {

    private static final Version EXACT_VERSION = new Version(2, 8, 0);

    private boolean stop = false;
    private final boolean async;

    private CalculatorUpdateChecker(boolean async) {
        super("Calculator update thread");
        this.async = async;
    }

    public static void update(long maxDuration, boolean async) {
        CalculatorUpdateChecker updater = new CalculatorUpdateChecker(async);
        if(!async) {
            //noinspection CallToThreadRun
            updater.run();
            return;
        }
        updater.start();
        new Thread(() -> {
            try {
                Thread.sleep(maxDuration);
            } catch(InterruptedException ignored) { }
            synchronized(updater) {
                if(!updater.stop) {
                    updater.stop = true;
                    updater.interrupt();
                }
            }
        }, "Update watchdog").start();
    }

    @Override
    public void run() {
        try {
            Console.debug("Checking for updates...");
            Console.Config.includeLineNumber = true;
            Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", false);

            JsonElement json = new HttpRequest("https://api.github.com/repos/rc-cookie/calculator/releases/latest")
                    .send().json.waitFor();

            Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", null);
            Version version = Version.parse(json.get("name").asString().substring(1));

            boolean updateAvailable = EXACT_VERSION.compareTo(version) < 0;
            if(Thread.interrupted())
                throw new InterruptedException();

            if(updateAvailable) {
                if(async) System.out.println("\n");
                System.out.println("A new version of the calculator (v"+version+") is available:");
                if(System.getProperty("os.name").toLowerCase().contains("win"))
                    System.out.println(json.get("assets").stream()
                            .filter(a -> a.get("name").asString().endsWith(".exe"))
                            .findFirst().get().get("browser_download_url").asString());
                else System.out.println(json.get("html_url"));
                if(async)
                    System.out.print("You can disable automatic update checks using \\autoUpdate false.\n\n> ");
            }
            else if(!async) System.out.println("Up do date.");
            else if(Console.getFilter().isEnabled("debug")) {
                Console.debug("Up do date.");
                System.out.print("> ");
            }
            System.out.flush();

        } catch(Exception e) {
            Console.debug("Failed to check for updates");
            Console.debug(Utils.getStackTraceString(e));
            if(!async) {
                System.err.println("Failed to check for updates.");
                System.err.flush();
            }
        }
    }


    private record Version(int a, int b, int c) implements Comparable<Version> {

        Version {
            Arguments.checkRange(a, 0, null);
            Arguments.checkRange(b, 0, null);
            Arguments.checkRange(c, 0, null);
        }

        @Override
        public int compareTo(@NotNull CalculatorUpdateChecker.Version o) {
            if(a != o.a) return a < o.a ? -1 : 1;
            if(b != o.b) return b < o.b ? -1 : 1;
            if(c != o.c) return c < o.c ? -1 : 1;
            return 0;
        }

        @Override
        public String toString() {
            return a + "." + b + "." + c;
        }

        public static Version parse(String str) {
            String[] abc = str.split("\\.");
            if(abc.length != 3)
                throw new IllegalArgumentException("Expected xxx.xxx.xxx");
            return new Version(
                    Integer.parseInt(abc[0]),
                    Integer.parseInt(abc[1]),
                    Integer.parseInt(abc[2])
            );
        }
    }
}
