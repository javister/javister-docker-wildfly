package com.github.javister.docker.testing.wildfly;

import com.github.javister.docker.testing.openjdk.JavisterOpenJDKContainer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Future;

/**
 * Обёртка над базовым контейнером
 * <a href="https://github.com/javister/javister-docker-wildfly">
 * javister-docker-docker.bintray.io/javister/javister-docker-wildfly
 * </a>.
 *
 * <p>Образ данного контейнера содержит сервер приложений JBoss Wildfly и не предназначен для прямого использования.
 * Данный клас необходим для построения обёрток над приложениями, основанными на Wildfly.
 *
 * @param <SELF> параметр, необходимый для организации паттерна fluent API.
 */
@SuppressWarnings({"squid:S00119", "UnusedReturnValue", "unused", "java:S2160"})
public class JavisterWildflyContainer<SELF extends JavisterWildflyContainer<SELF>> extends JavisterOpenJDKContainer<SELF> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavisterWildflyContainer.class);

    private static final String SERVER_OVERRIDE_URL = System.getProperty("stand.override.url", "");
    private static final boolean SERVER_OVERRIDE_ENABLE = Boolean.parseBoolean(System.getProperty("stand.override.enable", "TRUE"));

    private final JavisterWildflyContainer.Variant wildflyVariant;
    private final File volumePath;
    private String instanceId;

    public JavisterWildflyContainer(
            JavisterOpenJDKContainer.Variant jdkVariant,
            JavisterWildflyContainer.Variant wildflyVariant,
            String dockerImageName,
            String tag,
            File volumePath) {
        super(jdkVariant, dockerImageName, tag);
        this.wildflyVariant = wildflyVariant;
        this.volumePath = volumePath;
        init2();
    }

    public JavisterWildflyContainer(
            JavisterOpenJDKContainer.Variant jdkVariant,
            JavisterWildflyContainer.Variant wildflyVariant,
            String dockerImageName,
            String tag,
            Class<?> testClass) {
        super(jdkVariant, dockerImageName, tag, testClass);
        this.wildflyVariant = wildflyVariant;
        this.volumePath = getTestVolumePath();
        init2();
    }

    public JavisterWildflyContainer(
            JavisterOpenJDKContainer.Variant jdkVariant,
            JavisterWildflyContainer.Variant wildflyVariant,
            Class<?> testClass,
            Future<String> image) {
        super(jdkVariant, testClass, image);
        this.wildflyVariant = wildflyVariant;
        this.volumePath = getTestVolumePath();
        init2();
    }

    @Override
    public @Nullable String getVariant() {
        return wildflyVariant.getValue(getJdkVariant());
    }

    /**
     * Задаёт админский логин для Wildfly для доступа к его WEB консоли по порту 9990.
     * По умолчанию <b>admin</b>.
     *
     * @param adminLogin админский логин.
     * @return возвращает this для fluent API.
     */
    public SELF withAdminLogin(String adminLogin) {
        this.withEnv("ADMIN_LOGIN", adminLogin);
        return self();
    }

    /**
     * Задаёт админский пароль для Wildfly для доступа к его WEB консоли по порту 9990.
     * По умолчанию <b>masterkey</b>.
     *
     * @param adminPassword админский пароль.
     * @return возвращает this для fluent API.
     */
    public SELF withAdminPassword(String adminPassword) {
        this.withEnv("ADMIN_PASSWD", adminPassword);
        return self();
    }

    /**
     * Задаёт идентификатор инстанса.
     *
     * <p>Данный идентификатор используется как имя подкаталога при монтировании хранилища для приложения.
     * это может быть необходимо, когда необходимо запустить несколько копий приложения (например в кластере).
     *
     * @param instanceId строка - идентификатор инстанса
     * @return возвращает this для fluent API.
     */
    public SELF withInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return self();
    }

    /**
     * Возвращает HTTP адресс сервера приложений.
     *
     * @return HTTP адресс сервера приложений.
     */
    public String getURL() {
        return getURLInternal();
    }

    /**
     * Возвращает HTTP адресс приложения.
     *
     * @param appPath путь приложения (зависит имени модуля деплоя).
     * @return HTTP адресс приложения.
     */
    protected final String getURL(String appPath) {
        return getURLInternal() + appPath;
    }

    private String getURLInternal() {
        if (isOverrided()) {
            return SERVER_OVERRIDE_URL.endsWith("/") ? SERVER_OVERRIDE_URL : (SERVER_OVERRIDE_URL + "/");
        }

        String url = String.format("http://%s:8080/", this.getContainerId().substring(0, 12));
        LOGGER.info("Container {} generate URL: {}", logPrefix, url);
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavisterWildflyContainer)) return false;
        if (!super.equals(o)) return false;

        JavisterWildflyContainer<?> that = (JavisterWildflyContainer<?>) o;

        if (wildflyVariant != that.wildflyVariant) return false;
        return volumePath.equals(that.volumePath);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + wildflyVariant.hashCode();
        result = 31 * result + volumePath.hashCode();
        return result;
    }

    @Override
    public void start() {
        if (isOverrided()) {
            return;
        }
        super.start();
    }

    @Override
    public void stop() {
        if (isOverrided()) {
            return;
        }
        super.stop();
    }

    private void init2() {
        this
                .withExposedPorts(8080, 9990)
                .waitingFor(new WildflyHealthcheckWaitStrategy(this))
                .withStartupTimeout(Duration.of(5, ChronoUnit.MINUTES));
    }

    protected static boolean isOverrided() {
        return SERVER_OVERRIDE_ENABLE && !SERVER_OVERRIDE_URL.isEmpty();
    }

    public enum Variant {
        WILDFLY8("8"),
        WILDFLY8_SWITCHYARD("8-switchyard"),
        WILDFLY8_SWITCHYARD_MODESHAPE("8-switchyard.modeshape");

        private final String value;

        Variant(String version) {
            value = version;
        }

        public String getValue(JavisterOpenJDKContainer.Variant jdkVariant) {
            return value + "-jdk" + jdkVariant.getValue();
        }
    }
}
