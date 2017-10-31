#!/usr/bin/env bash

PROXY_ARGS="--env http_proxy=${http_proxy} \
            --env no_proxy=${no_proxy}"

CURRENT_DIR=$(pwd)

if [ ! -d ${CURRENT_DIR}/tmp ]; then
    mkdir tmp
fi

docker run -ti --rm -p 8080:8080 -p 9990:9990 -v ${CURRENT_DIR}/tmp:/config -e PUID=$(id -u) -e PGID=$(id -g) -e JAVA_XMX=1g ${PROXY_ARGS} javister-docker-docker.bintray.io/javister/javister-docker-wildfly $@
