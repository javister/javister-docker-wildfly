#!/usr/bin/env bash

set -e

WILDFLY_CONFIG_DIR=$(cat /etc/container_environment/WILDFLY_CONFIG_DIR)
ADMIN_LOGIN=$(cat /etc/container_environment/ADMIN_LOGIN)
ADMIN_PASSWD=$(cat /etc/container_environment/ADMIN_PASSWD)

if [ ! -d /config/wildfly/config ]; then
    mkdir --parents /config/wildfly/config
    cp --archive --recursive /app/wildfly/standalone/configuration/* /config/wildfly/config/
fi

mkdir --parents /config/diagnostics
mkdir --parents /config/wildfly/deployments

chown --recursive system:system /config/diagnostics
chown --recursive system:system /config/wildfly

rm -f /config/diagnostics/*
rm -f /config/wildfly/deployments/*.isdeploying
rm -f /config/wildfly/deployments/*.deployed
rm -f /config/wildfly/deployments/*.failed

exec setuser system /app/wildfly/bin/add-user.sh \
    --confirm-warning \
    --enable \
    --silent \
    -sc ${WILDFLY_CONFIG_DIR} \
    --realm "ManagementRealm" \
    --user ${ADMIN_LOGIN} \
    --password ${ADMIN_PASSWD}
