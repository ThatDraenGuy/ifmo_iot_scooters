# From https://github.com/yandex-cloud/fluent-bit-plugin-yandex/blob/main/Dockerfile

ARG plugin_version=dev
ARG fluent_bit_version=3.0.0
ARG golang_version=1.20.6

FROM golang:${golang_version}-bullseye as builder
ARG plugin_version
ARG fluent_bit_version
ARG config=github.com/yandex-cloud/fluent-bit-plugin-yandex/v2/config
WORKDIR /build
RUN git clone https://github.com/yandex-cloud/fluent-bit-plugin-yandex.git
WORKDIR /build/fluent-bit-plugin-yandex
RUN CGO_ENABLED=1 go build \
    -buildmode=c-shared \
    -o /yc-logging.so \
    -ldflags "-X ${config}.PluginVersion=${plugin_version} -X ${config}.FluentBitVersion=${fluent_bit_version}" \
    .

FROM fluent/fluent-bit:${fluent_bit_version} as fluent-bit
COPY --from=builder /yc-logging.so /fluent-bit/bin/
COPY ./fluent-bit/fluent-bit.conf /fluent-bit/etc/fluent-bit.conf
COPY ./fluent-bit/parsers.conf /fluent-bit/etc/parsers.conf
COPY ./fluent-bit/plugins.conf /fluent-bit/etc/plugins.conf