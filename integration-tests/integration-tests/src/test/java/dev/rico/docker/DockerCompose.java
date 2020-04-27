package dev.rico.docker;

import dev.rico.internal.core.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class DockerCompose {

    private final static Logger LOG = LoggerFactory.getLogger(DockerCompose.class);

    private final static String WHALE_EMOJI = "\uD83D\uDC33";

    private final Path composeFile;

    private final Executor executor;

    public DockerCompose(final Executor executor, final Path composeFile) {
        this.composeFile = Assert.requireNonNull(composeFile, "composeFile");
        this.executor = Assert.requireNonNull(executor, "executor");
        if (!executableExists("docker-compose")) {
            throw new IllegalStateException("Looks like docker-compose is not installed locally");
        }
    }

    public synchronized void start(final long time, final TimeUnit timeUnit, final Wait... waits) {
        try {
            kill();
            final Process startProcess = new ProcessBuilder().directory(composeFile.toFile().getParentFile())
                    .command("docker-compose", "up", "-d", "--build", "--force-recreate")
                    .redirectErrorStream(true)
                    .start();

            waitAndDump(startProcess);

            executor.execute(() -> {
                try {
                    final Process logProcess = new ProcessBuilder().directory(composeFile.toFile().getParentFile())
                            .command("docker-compose", "logs", "-f")
                            .redirectErrorStream(true)
                            .start();
                    while (logProcess.isAlive()) {
                        final InputStream inputStream = logProcess.getInputStream();
                        final InputStreamReader reader = new InputStreamReader(inputStream);
                        final BufferedReader bufferedReader = new BufferedReader(reader);
                        String line;
                        System.out.println("");
                        LOG.info("Starting Docker container logging");
                        while ((line = bufferedReader.readLine()) != null) {
                            System.out.println(WHALE_EMOJI + " " + line);
                        }
                    }
                } catch (Exception e) {
                    LOG.error("Error in container logging", e);
                }
            });
            Arrays.asList(waits).forEach(w -> {
                try {
                    w.waitFor(executor, time, timeUnit);
                } catch (TimeoutException e) {
                    throw new RuntimeException("Error", e);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Can not start docker-compose", e);
        }
    }

    private void waitAndDump(final Process process) {
        Assert.requireNonNull(process, "process");
        while (process.isAlive()) {
            final InputStream inputStream = process.getInputStream();
            final InputStreamReader reader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    LOG.info(line);
                }
            } catch (Exception e) {
                LOG.error("Error in process watcher", e);
            }
        }
    }

    public synchronized void kill() {
        try {
            final Process killProcess = new ProcessBuilder().directory(composeFile.toFile().getParentFile())
                    .command("docker-compose", "kill")
                    .redirectErrorStream(true)
                    .start();
            waitAndDump(killProcess);
        } catch (Exception e) {
            throw new RuntimeException("Can not start docker-compose", e);
        }
    }

    public static boolean executableExists(String executable) {
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
