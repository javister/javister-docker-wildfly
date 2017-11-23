#!/bin/bash -e

function build() {
    local release
    release="dry"
    doPull="no"

    while getopts ":rph" opt; do
        case $opt in
            r)
                release="release"
                ;;
            p)
                doPull="yes"
                ;;
            h)
                cat <<EOF
usage: build [OPTION]... [-- [docker build opts]]
  -h        show this help.
  -r        push resulting image.
  -p        don't pull base image.
EOF
                return 0;
                ;;
            :)
                echo "$0: option requires an argument -- '$OPTARG'" 1>&2
                return 1
                ;;
            *)
                echo "$0: invalid option -- '$OPTARG'" 1>&2
                return 1
                ;;
        esac
    done
    shift $((OPTIND-1))

    WILDFLY_VERSION=8.2.1.krista24
    WILDFLY_CLASSIFIER=WF82-SwitchYard

    IMAGE_TAG="javister-docker-docker.bintray.io/javister/javister-docker-wildfly"
    PROXY_ARGS="--build-arg http_proxy=${http_proxy} \
                --build-arg no_proxy=${no_proxy}"
    [ "${doPull}" == "yes" ] && docker pull javister-docker-docker.bintray.io/javister/javister-docker-openjdk:1.0.java8

    docker build \
        --build-arg WILDFLY_VERSION=${WILDFLY_VERSION} \
        --build-arg WILDFLY_CLASSIFIER=${WILDFLY_CLASSIFIER} \
        --tag ${IMAGE_TAG}:latest \
        --tag ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER} \
        ${PROXY_ARGS} \
        $@ \
        .

    [ "${release}" == "release" ] && docker push ${IMAGE_TAG}:latest
    [ "${release}" == "release" ] && docker push ${IMAGE_TAG}:${WILDFLY_VERSION}-${WILDFLY_CLASSIFIER}
}

CURRENT_DIR=$(pwd)

if [ -d ${CURRENT_DIR}/tmp ]; then
    rm -rf ${CURRENT_DIR}/tmp
fi

trap "exit 1" INT TERM QUIT

build "$@"
