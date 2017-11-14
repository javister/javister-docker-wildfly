FROM javister-docker-docker.bintray.io/javister/javister-docker-openjdk:1.0.java8
MAINTAINER Viktor Verbitsky <vektory79@gmail.com>

ARG WILDFLY_VERSION
ARG WILDFLY_CLASSIFIER

LABEL wildfly-version=$WILDFLY_VERSION \
      wildfly-classifier=$WILDFLY_CLASSIFIER

COPY files /

ENV JAVA_XMS="64m" \
    JAVA_XMX="512m" \
    JAVA_OPTS="$JVM_OPTS -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=org.jboss.byteman -Djava.awt.headless=true" \
    SERVER_PARAMS="--server-config=standalone-full.xml" \
    WILDFLY_CONFIG_DIR="/config/wildfly/config" \
    WILDFLY_DATA_DIR="/config/wildfly/data" \
    WILDFLY_LOG_DIR="/config/wildfly/log" \
    ADMIN_LOGIN="admin" \
    ADMIN_PASSWD="masterkey" \
    RPMLIST="libaio"

RUN . /usr/local/sbin/proxyenv && \
    yum-install && \
    mkdir --parents /app/wildfly && \
    https_proxy=$https_proxy no_proxy=$no_proxy curl -s "http://artifactory.krista.ru/artifactory/maven-krista-nexus-open-source/ru/krista/wildfly-bas/$WILDFLY_VERSION/wildfly-bas-$WILDFLY_VERSION-$WILDFLY_CLASSIFIER.zip" > /tmp/wildfly.zip && \
    unzip /tmp/wildfly.zip -d /tmp/wildfly && \
    bash -c 'DIR=$(ls -1 /tmp/wildfly/); cp --archive --recursive --verbose /tmp/wildfly/${DIR}/* /app/wildfly/' && \
    chmod --recursive a+w /app/wildfly && \
    yum-clean && \
    rm --recursive --force /tmp/* && \
    chmod --recursive --changes +x /etc/my_init.d/*.sh /etc/service /usr/local/bin

WORKDIR /app/wildfly

# Используемые сетевые порты:
# 8080 - http
# 8443 - https
# 9990 - management http
# 9993 - management https
# 8009 - ajp
# 8787 - debug
EXPOSE 8080 8443 9990 9993 8009 8787
