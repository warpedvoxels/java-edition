# Base image for building the Discord bot application.
FROM rust:1.71-alpine3.18 as build
WORKDIR /usr/src

# Creating a dummy project to cache dependencies.
RUN USER=root cargo new voxeru
WORKDIR /usr/src/voxeru
COPY Cargo.toml Cargo.lock ./
COPY .cargo ./.cargo

# Workaround for rust-lang/docker-rust#85
ENV RUSTFLAGS="-C target-feature=-crt-static"

# Setting up the platform dependencies, as we are going to use Alpine, then
# build the dummy project to get the dependencies cached.
RUN apk add --no-cache openssl-dev musl-dev
RUN cargo build --release

# Prepare the source code then build the application.
COPY src ./src
RUN touch ./src/main.rs
RUN cargo build --release

# Copying the binary to a new stage, scratch container.
FROM alpine:3.18 as run
RUN apk add --no-cache libgcc
COPY --from=build /usr/src/voxeru/target/release/voxeru .
USER 1000
ARG SETTINGS_PROFILE
CMD ["./voxeru"]