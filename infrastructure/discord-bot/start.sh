#!/usr/bin/env bash

#
# WarpedVoxels, a network of Minecraft: Java Edition servers
# Copyright (C) 2023  Pedro Henrique
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

cd -P -- "$(dirname -- "${BASH_SOURCE[0]}")"

DOTENV=${DOTENV:-./.env}
case `uname` in
    Linux)
        export $(grep -v '^#' "$DOTENV" | xargs -d '\n') ;;
    Darwin|FreeBSD)
        export $(grep -v '^#' "$DOTENV" | xargs -0) ;;
    *)
        echo "Unsupported operating system detected, quitting..."
        exit 1 ;;
esac

env DOCKER_BUILDKIT=1 docker-compose -f ./docker-compose.yml up "$@"