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

exec setuser system bash -c "/app/wildfly/bin/add-user.sh \
    --confirm-warning \
    --enable \
    -sc ${WILDFLY_CONFIG_DIR} \
    --realm 'ManagementRealm' \
    --user ${ADMIN_LOGIN} \
    --password ${ADMIN_PASSWD}"
