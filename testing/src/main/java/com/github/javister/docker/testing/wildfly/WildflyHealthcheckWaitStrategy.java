package com.github.javister.docker.testing.wildfly;

import org.rnorth.ducttape.TimeoutException;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Ожидание старта и готовности сервера приложений Wildfly 8.
 */
public class WildflyHealthcheckWaitStrategy extends AbstractWaitStrategy {

    private Container<?> container;

    public WildflyHealthcheckWaitStrategy(Container<?> container) {
        this.container = container;
    }

    @Override
    protected void waitUntilReady() {

        try {
            Unreliables.retryUntilTrue((int) startupTimeout.getSeconds(), TimeUnit.SECONDS, () -> {
                Thread.sleep(10);
                return waitStrategyTarget.isHealthy();
            });
        } catch (TimeoutException e) {
            try {
                container.execInContainer(
                        "setuser",
                        "system",
                        "/bin/sh",
                        "-c",
                        "jps | grep jboss-modules | awk '{ print $1 }' | xargs jstack -l > /config/wildfly.threaddump")
                        .getExitCode();
            } catch (IOException ex) {
                e.addSuppressed(ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            throw new ContainerLaunchException("Timed out waiting for container to become healthy", e);
        }
    }
}
