package dev.rico.docker;

import dev.rico.internal.core.Assert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class DockerCompose {

    private final Path composeFile;

    private Process startProcess;

    public DockerCompose(final Path composeFile) {
        this.composeFile = Assert.requireNonNull(composeFile, "composeFile");
        if (!executableExists("docker-compose")) {
            throw new IllegalStateException("Looks like docker-compose is not installed locally");
        }
    }

    public synchronized void start() {
        try {
            kill();
            startProcess = new ProcessBuilder().directory(composeFile.toFile().getParentFile())
                    .command("docker-compose", "up", "-d", "--build", "--force-recreate")
                    .redirectErrorStream(true)
                    .redirectOutput(new File(composeFile.toFile().getParentFile(), "start_out.txt"))
                    .start();
        } catch (Exception e) {
            throw new RuntimeException("Can not start docker-compose", e);
        }
    }

    public synchronized void start(final long time, final TimeUnit timeUnit, final Wait... waits) {
        start();
        Arrays.asList(waits).forEach(w -> {
            try {
                w.waitFor(time, timeUnit);
            } catch (TimeoutException e) {
                throw new RuntimeException("Error", e);
            }
        });

    }

    public synchronized void kill() {
        try {
            final Process killProcess = new ProcessBuilder().directory(composeFile.toFile().getParentFile())
                    .command("docker-compose", "kill")
                    .redirectErrorStream(true)
                    .redirectOutput(new File(composeFile.toFile().getParentFile(), "kill_out.txt"))
                    .start();
            killProcess.waitFor();
            if (startProcess != null) {
                startProcess.waitFor();
            }
        } catch (Exception e) {
            throw new RuntimeException("Can not start docker-compose", e);
        }
    }

    public static boolean executableExists(String executable) {

        // First check if we've been given the full path already
        File directFile = new File(executable);
        if (directFile.exists() && directFile.canExecute()) {
            return true;
        }

        for (String pathString : getSystemPath()) {
            Path path = Paths.get(pathString);
            if (Files.exists(path.resolve(executable)) && Files.isExecutable(path.resolve(executable))) {
                return true;
            }
        }

        return false;
    }

    public static String[] getSystemPath() {
        return System.getenv("PATH").split(Pattern.quote(File.pathSeparator));
    }
}
