package com.github.javister.docker.testing.wildfly.containers;

import com.github.javister.docker.testing.base.JavisterBaseContainer;
import com.github.javister.docker.testing.openjdk.JavisterOpenJDKContainer;
import com.github.javister.docker.testing.postgresql.JavisterPostgreSQLContainer;
import com.github.javister.docker.testing.wildfly.JavisterWildflyContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class WildflyHibernateContainer extends JavisterWildflyContainer<WildflyHibernateContainer> {
    private JavisterPostgreSQLContainer<?> dbContainer;
    private final Network network = Network.newNetwork();

    public WildflyHibernateContainer(
            JavisterOpenJDKContainer.Variant jdkVariant,
            JavisterWildflyContainer.Variant wildflyVariant,
            Class<?> testClass) {
        super(jdkVariant, wildflyVariant, testClass, new ImageFromDockerfile()
                .withFileFromClasspath("docker/app/wildfly/setup/app-setup.cli.template", "/docker/app/wildfly/setup/app-setup.cli.template")
                .withFileFromClasspath("docker/etc/my_init.d/03_wildfly8-test_logo.sh", "/docker/etc/my_init.d/03_wildfly8-test_logo.sh")
                .withFileFromClasspath("docker/etc/my_init.d/80_setup_application.sh", "/docker/etc/my_init.d/80_setup_application.sh")
                .withFileFromPath("docker/app/application/hibernate.war", srcFile(testClass, "hibernate.war"))
                .withFileFromPath("docker/app/application/postgresql.jar", srcFile(testClass, "postgresql.jar"))
                .withDockerfileFromBuilder(builder ->
                        builder
                                .from(JavisterBaseContainer.getImageName(JavisterWildflyContainer.class, wildflyVariant.getValue(jdkVariant)))
                                .add("docker", "/")
                                .run("chmod --recursive +x /etc/my_init.d/*.sh /etc/service /usr/local/bin")
                                .env(new HashMap<String, String>() {{
                                    put("POSTGRES_ADDR", "postgres");
                                    put("POSTGRES_PORT", "5432");
                                    put("POSTGRES_USER", "system");
                                }})
                                .build()
                )
        );
        init();
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    public WildflyHibernateContainer withDbPassword(String dbPassword) {
        this.withEnv("POSTGRES_PASS", dbPassword);
        dbContainer.withPassword(dbPassword);
        return self();
    }

    public WildflyHibernateContainer withDbName(String dbName) {
        this.withEnv("POSTGRES_DB_NAME", dbName);
        dbContainer.withDatabaseName(dbName);
        return self();
    }

    private void init() {
        this.dbContainer = new JavisterPostgreSQLContainer<>(getTestClass());
        this.dbContainer
                .withFSync(false)
                .withNetwork(network)
                .withNetworkAliases("postgres")
                .withDatabaseName("hibernate");
        this
                .withDbName("hibernate")
                .withDbPassword("masterkey")
                .withNetwork(network);
    }

    @Override
    public void start() {
        if (isOverrided()) {
            return;
        }
        try {
            dbContainer.deleteTestDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dbContainer.start();
        super.start();
    }

    @Override
    public void stop() {
        if (isOverrided()) {
            return;
        }
        super.stop();
        dbContainer.stop();
    }

    @Override
    public String getURL() {
        return super.getURL("hibernate/index.jsf");
    }

    private static Path srcFile(Class<?> testClass, String... relPath) {
        return Paths.get(JavisterBaseContainer.getTestPath(testClass).toString(), relPath);
    }
}
