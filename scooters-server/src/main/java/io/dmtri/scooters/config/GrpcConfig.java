package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record GrpcConfig(int port) {

    public static GrpcConfig fromConfiguration(Configuration configuration) {
        return new GrpcConfig(configuration.getInt("port"));
    }
}
