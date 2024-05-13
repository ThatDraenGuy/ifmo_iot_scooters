package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record MetricsConfig(int port) {
    public static MetricsConfig fromConfiguration(Configuration configuration) {
        return new MetricsConfig(configuration.getInt("port"));
    }
}
