#!/usr/bin/env bash

set -e

WILDFLY_CONFIG_DIR=$(cat /etc/container_environment/WILDFLY_CONFIG_DIR)
ADMIN_LOGIN=$(cat /etc/container_environment/ADMIN_LOGIN)
ADMIN_PASSWD=$(cat /etc/container_environment/ADMIN_PASSWD)

if [ ! -d /config/wildfly/config ]; then
    mkdir --parents /config/wildfly/config
    cp --archive --recursive /app/wildfly/standalone/configuration/* /config/wildfly/config/
fi

mkdir --parents /config/wildfly/deployments
chown --recursive system:system /config/wildfly
#chmod --changes 777 $(find /app/wildfly/standalone -type d)
#chmod --changes 666 $(find /app/wildfly/standalone -type f)

expandenv /app/wildfly/setup/setup.cli.template > /app/wildfly/setup/setup.cli

exec setuser system ./bin/standalone.sh \
    --admin-only \
    -b=${HOSTIP} \
    -bmanagement=${HOSTIP} \
    -Djboss.server.config.dir=${WILDFLY_CONFIG_DIR} \
    -Djboss.server.data.dir=${WILDFLY_DATA_DIR} \
    -Djboss.server.log.dir=${WILDFLY_LOG_DIR} \
    ${SERVER_PARAMS} &
wait4tcp -w 200 ${HOSTIP} 9990
WILDFLY_PID=`ps -ef | grep -e "\\[Standalone\\]" | awk '{print $2}'`
jboss-cli --file=/app/wildfly/setup/setup.cli
kill ${WILDFLY_PID}
wait4tcp -w 200 -c ${HOSTIP} 9990

exec setuser system /app/wildfly/bin/add-user.sh \
    --confirm-warning \
    --enable \
    -sc ${WILDFLY_CONFIG_DIR} \
    --realm "ManagementRealm" \
    --user ${ADMIN_LOGIN} \
    --password ${ADMIN_PASSWD}
