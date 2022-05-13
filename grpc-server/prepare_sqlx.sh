#!/bin/bash

# Database needs to be running before running this script. You can shut down
# the database after running this.

SETTINGS="$(cat ~/.hexalite/settings.toml)"
SETTINGS="${SETTINGS#*\[grpc.services.postgres\]}" 
SETTINGS=$(echo "$SETTINGS" | tail -n +2 | sed -e '/^$/,$d')

get_field() {
    echo "$SETTINGS" | awk -F ' = ' "/$1/ {print \$2}" | tr -d "'"
}

# Fields: host, port, user, password, database
HOST=`get_field host`
PORT=`get_field port`
USER=`get_field user`
PASSWORD=`get_field password`
DATABASE=`get_field database`
export DATABASE_URL="postgres://$USER:$PASSWORD@$HOST:$PORT/$DATABASE"

if [ "$1" != "-s" ]; then
    cargo install sqlx-cli
fi

cargo sqlx prepare -- --lib --all-targets --all-features