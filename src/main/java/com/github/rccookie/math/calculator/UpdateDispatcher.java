package com.github.rccookie.math.calculator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.http.HttpRequest;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ThreadedFutureImpl;
import com.github.rccookie.util.Utils;

public final class UpdateDispatcher {

    private UpdateDispatcher() {
    }

    public static void update() throws Exception {

        System.out.println("Checking for latest version...");

        Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", false);
        JsonElement json = new HttpRequest("https://api.github.com/repos/rc-cookie/calculator/releases/latest")
                .send().json.waitFor();

        Console.getFilter(ThreadedFutureImpl.class).setEnabled("warn", null);
        Version version = Version.parse(json.get("name").asString().substring(1));

        Console.mapDebug("Current version", Calculator.VERSION);
        Console.mapDebug("Latest version available", version);

        if(Calculator.VERSION.compareTo(version) >= 0) {
            System.out.println("Already up to date.");
            return;
        }

        System.out.println("Downloading latest version (" + version + ")...");

        String suffix = System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : ".jar";
        String url = json.get("assets").stream()
                    .filter(a -> a.get("name").asString().endsWith(suffix))
                    .findFirst().get().get("browser_download_url").asString();
        Console.mapDebug("Download url", url);

        Path exe = Files.createTempFile("math", "exe").toAbsolutePath();
        new HttpRequest(url).send().in.waitFor().transferTo(Files.newOutputStream(exe));

        Path jar = Files.createTempFile("math_update", "jar").toAbsolutePath();
        //noinspection resource,DataFlowIssue
        UpdateDispatcher.class.getResourceAsStream("/update/update.jar").transferTo(Files.newOutputStream(jar));

        Path selfExe = Path.of(UpdateDispatcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath();


        if(System.getProperty("os.name").toLowerCase().contains("win"))
            dispatchWin(exe, jar, selfExe);
        else dispatchUnix(exe, jar, selfExe);

        System.out.println("Closing to update. Please close all other running instances.");
        // TODO: Find and close other instances
        System.exit(0);
    }

    @SuppressWarnings("DataFlowIssue")
    private static void dispatchWin(Path newExe, Path updateJar, Path self) throws Exception {

        Path elevateVbs = Path.of(Utils.getAppdata(), "utils", "elevate.vbs").toAbsolutePath();
        Path elevateCmd = Path.of(Utils.getAppdata(), "utils", "elevate.cmd").toAbsolutePath();

        Files.createDirectories(elevateCmd.getParent());

        if(!Files.exists(elevateVbs)) {
            InputStream in = UpdateDispatcher.class.getResourceAsStream("/update/elevate.vbs");
            in.transferTo(Files.newOutputStream(elevateVbs));
            in.close();
        }
        if(!Files.exists(elevateCmd)) {
            InputStream in = UpdateDispatcher.class.getResourceAsStream("/update/elevate.cmd");
            in.transferTo(Files.newOutputStream(elevateCmd));
            in.close();
        }

        String java = System.getProperty("java.home") + "\\bin\\java.exe";

        Runtime.getRuntime().exec(new String[] {
                elevateCmd.toString(),
                java,
                "--enable-preview",
                "-jar",
                updateJar.toString(),
                // ---- from here args ----
                newExe.toString(),
                self.toString(),
                updateJar.toString(),
                Boolean.toString(Console.getDefaultFilter().isEnabled("debug"))
        });
    }

    private static void dispatchUnix(Path newExe, Path updateJar, Path self) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("sudo");

        if(Runtime.getRuntime().exec(new String[] { "if", "[[", "-z", "$SUDO_ASKPASS", "]]", ";", "then", "exit", "1", ";", "else", "exit", "0", ";", "fi" }).waitFor() == 0)
            cmd.add("-A");

        String java = System.getProperty("java.home") + "/bin/java";

        cmd.addAll(List.of(java, "-jar", updateJar.toString()));
        cmd.addAll(List.of(newExe.toString(), self.toString(), updateJar.toString(), Boolean.toString(Console.getDefaultFilter().isEnabled("debug"))));

        Runtime.getRuntime().exec(cmd.toArray(new String[0]));
    }
}
