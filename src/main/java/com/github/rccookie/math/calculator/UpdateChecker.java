package com.github.rccookie.math.calculator;

import com.github.rccookie.http.HttpRequest;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ThreadedFutureImpl;
import com.github.rccookie.util.Utils;

import com.diogonunes.jcolor.Attribute;

final class UpdateChecker extends Thread {

    private boolean stop = false;
    private final boolean async;

    private UpdateChecker(boolean async) {
        super("Calculator update thread");
        this.async = async;
    }

    public static void update(long maxDuration, boolean async) {
        UpdateChecker updater = new UpdateChecker(async);
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
            Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", false);

            JsonElement json = new HttpRequest("https://api.github.com/repos/rc-cookie/calculator/releases/latest")
                    .send().json.waitFor();

            Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", null);
            Version version = Version.parse(json.get("name").asString().substring(1));

            boolean updateAvailable = Calculator.VERSION.compareTo(version) < 0;
            if(Thread.interrupted())
                throw new InterruptedException();

            if(updateAvailable) {
                if(async) System.out.println("\n");
                System.out.println("A new version of the calculator is available: v"+version);
                System.out.print("Run " + Console.colored("\\update", Attribute.GREEN_TEXT()) +
                        " to apply the update. Alternatively, you can disable automatic update checks using " +
                        Console.colored("\\autoUpdate false", Attribute.BLUE_TEXT()) +".\n\n> ");
            }
            else if(!async) System.out.println("Up to date.");
            else if(Console.getFilter().isEnabled("debug")) {
                Console.debug("Up to date.");
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
}
