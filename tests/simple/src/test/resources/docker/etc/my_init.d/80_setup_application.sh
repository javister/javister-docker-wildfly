#!/usr/bin/env bash

set -e

. /etc/container_environment.sh

if [ ! -f "/app/container-initialized" ]; then
    touch /app/container-initialized
    echo "false" > /etc/container_environment/CONTAINER_INITIALIZED
    [ -d "/config/wildfly/deployments" ] && rm -rf /config/wildfly/deployments/* || true
    cp -f /app/application/* /config/wildfly/deployments/
    chmod +w /config/wildfly/deployments/*
else
    echo "true" > /etc/container_environment/CONTAINER_INITIALIZED
fi

wait4tcp ${POSTGRES_ADDR} ${POSTGRES_PORT}
