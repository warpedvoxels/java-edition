#!/bin/bash

cd "${0%/*}/.."
docker-compose -f ./docker/docker-compose.yml up