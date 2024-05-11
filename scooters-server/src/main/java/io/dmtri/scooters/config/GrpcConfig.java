package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public class GrpcConfig {
    private final int port;

    public GrpcConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static GrpcConfig fromConfiguration(Configuration configuration) {
        return new GrpcConfig(configuration.getInt("port"));
    }
}
