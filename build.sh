#!/bin/bash -e

WILDFLY_VERSION=8.2.1.krista24
WILDFLY_CLASSIFIER=WF82-SwitchYard
DATE=$(date +"%Y-%m-%d")

IMAGE_TAG="javister-docker-docker.bintray.io/javister/javister-docker-wildfly"
PROXY_ARGS="--build-arg http_proxy=${http_proxy} \
            --build-arg no_proxy=${no_proxy}"
docker pull javister-docker-docker.bintray.io/javister/javister-docker-openjdk:1.0.java8

docker build \
    --build-arg WILDFLY_VERSION=${WILDFLY_VERSION} \
    --build-arg WILDFLY_CLASSIFIER=${WILDFLY_CLASSIFIER} \
    --tag ${IMAGE_TAG}:latest \
    --tag ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER} \
    --tag ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER}-${DATE} \
    ${PROXY_ARGS} \
    $@ \
    .

docker push ${IMAGE_TAG}:latest
docker push ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER}
docker push ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER}-${DATE}
