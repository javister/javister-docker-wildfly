ARG JAVISTER_OPENJDK_VERSION
FROM javister-docker-docker.bintray.io/javister/javister-docker-openjdk:${JAVISTER_OPENJDK_VERSION} AS img-prepare

ARG WILDFLY_VERSION

COPY src/main/docker /zzz/
ADD --chown=system:system target/dependency/jboss-bas-${WILDFLY_VERSION}/ /zzz/app/wildfly/
RUN chmod --recursive a+r+w /zzz

FROM javister-docker-docker.bintray.io/javister/javister-docker-openjdk:${JAVISTER_OPENJDK_VERSION}
MAINTAINER Viktor Verbitsky <vektory79@gmail.com>

COPY --from=img-prepare /zzz /

ENV JAVA_XMS="64m" \
    JAVA_XMX="512m" \
    JAVA_OPTS="$JVM_OPTS -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true " \
    SERVER_PARAMS="--server-config=standalone-full.xml" \
    WILDFLY_CONFIG_DIR="/config/wildfly/config" \
    WILDFLY_DATA_DIR="/config/wildfly/data" \
    WILDFLY_LOG_DIR="/config/wildfly/log" \
    WILDFLY_TMP_DIR="/config/wildfly/tmp" \
    ADMIN_LOGIN="admin" \
    ADMIN_PASSWD="masterkey" \
    BASE_RPMLIST="$BASE_RPMLIST libaio"

RUN . /usr/local/bin/proxyenv && \
    yum-install && \
    yum-clean && \
    rm --recursive --force /tmp/* && \
    chmod --recursive +x /etc/my_init.d/*.sh /etc/service /usr/local/bin

WORKDIR /app/wildfly

# Используемые сетевые порты:
# 8080 - http
# 8443 - https
# 9990 - management http
# 9993 - management https
# 8009 - ajp
# 8787 - debug
EXPOSE 8080 8443 9990 9993 8009 8787

HEALTHCHECK --interval=10s --timeout=3s --start-period=3m \
    CMD healthcheck-wildfly.sh

ARG WILDFLY_CLASSIFIER

LABEL wildfly-version=$WILDFLY_VERSION \
      wildfly-classifier=$WILDFLY_CLASSIFIER
