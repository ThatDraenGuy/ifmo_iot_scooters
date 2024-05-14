package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record MapApiConfig(String host, int port) {
    public static MapApiConfig fromConfiguration(Configuration configuration) {
        return new MapApiConfig(configuration.getString("host"), configuration.getInt("port"));
    }
}
