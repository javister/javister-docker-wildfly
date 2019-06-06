#!/usr/bin/env bash

cd /config/wildfly/deployments

while true; do
    deployments=$(ls -1 *.ear *.war *.jar 2> /dev/null | wc -l)
    deploying=$(ls -1 *.isdeploying 2> /dev/null | wc -l)
    deployed=$(ls -1 *.deployed 2> /dev/null | wc -l)
    failed=$(ls -1 *.failed 2> /dev/null | wc -l)

    if [ ${deployments} -gt 0 -a ${deployments} -eq ${deployed} ]; then
        exit 0
    fi

    exit 1
done