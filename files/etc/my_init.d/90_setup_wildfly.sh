#!/usr/bin/env bash

set -e

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
