#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
(cd "$DIRNAME/.." && docker-compose -f ./docker/docker-compose.yml --env-file ./.env up)