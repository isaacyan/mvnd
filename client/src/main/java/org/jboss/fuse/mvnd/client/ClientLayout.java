package org.jboss.fuse.mvnd.client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/**
 * Local paths relevant for the {@link Client}.
 */
public class ClientLayout extends Layout {

    private static ClientLayout ENV_INSTANCE;

    private final Path localMavenRepository;
    private final Path settings;
    private final Path javaHome;

    public static ClientLayout getEnvInstance() {
        if (ENV_INSTANCE == null) {
            final Properties mvndProperties = loadMvndProperties();
            final Path pwd = Paths.get(".").toAbsolutePath().normalize();

            ENV_INSTANCE = new ClientLayout(
                    findMavenHome(mvndProperties),
                    pwd,
                    findMultiModuleProjectDirectory(pwd),
                    findJavaHome(mvndProperties),
                    null,
                    null);
        }
        return ENV_INSTANCE;
    }

    public ClientLayout(Path mavenHome, Path userDir, Path multiModuleProjectDirectory, Path javaHome,
            Path localMavenRepository, Path settings) {
        super(mavenHome, userDir, multiModuleProjectDirectory);
        this.localMavenRepository = localMavenRepository;
        this.settings = settings;
        this.javaHome = Objects.requireNonNull(javaHome, "javaHome");
    }

    /**
     * @return absolute normalized path to local Maven repository or {@code null}
     */
    public Path getLocalMavenRepository() {
        return localMavenRepository;
    }

    /**
     * @return absolute normalized path to {@code settings.xml} or {@code null}
     */
    public Path getSettings() {
        return settings;
    }

    public Path javaHome() {
        return javaHome;
    }

    static Path findJavaHome(Properties mvndProperties) {
        String rawValue = System.getenv("JAVA_HOME");
        if (rawValue == null) {
            rawValue = mvndProperties.getProperty("java.home");
        }
        if (rawValue == null) {
            rawValue = System.getProperty("java.home");
        }
        if (rawValue == null) {
            throw new IllegalStateException(
                    "Either environment variable JAVA_HOME or java.home property in ~/.m2/mvnd.properties or system property java.home must be set");
        }
        final Path path = Paths.get(rawValue);
        try {
            return path.toRealPath();
        } catch (IOException e) {
            throw new RuntimeException("Could not get a real path from path " + path);
        }
    }

    @Override
    public String toString() {
        return "ClientLayout [localMavenRepository=" + localMavenRepository + ", settings=" + settings + ", javaHome="
                + javaHome + ", mavenHome()=" + mavenHome() + ", userDir()=" + userDir() + ", registry()=" + registry()
                + ", multiModuleProjectDirectory()=" + multiModuleProjectDirectory() + "]";
    }

}