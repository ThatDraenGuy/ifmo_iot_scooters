FROM rust:alpine as build-env
WORKDIR /app
COPY ./scooters-fake-client /app
COPY ./proto /proto
RUN apk add --no-cache musl-dev
RUN apk add --no-cache pkgconfig
RUN apk add --no-cache protobuf-dev
RUN rustup update
RUN rustup target add x86_64-unknown-linux-musl
RUN cargo build --target x86_64-unknown-linux-musl --release

FROM alpine:3.17
COPY --from=build-env /app/target/x86_64-unknown-linux-musl/release/scooters-fake-client /
RUN chmod +x scooters-fake-client
CMD ["./scooters-fake-client"]