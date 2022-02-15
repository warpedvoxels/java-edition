#!/bin/bash

cd "${0%/*}"
cd ..

docker-compose -f ./docker/docker-compose.yml up