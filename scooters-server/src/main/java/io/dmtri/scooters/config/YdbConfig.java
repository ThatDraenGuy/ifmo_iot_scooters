package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record YdbConfig(String endpoint, String database, String token) {

    public static YdbConfig fromConfiguration(Configuration config) {
        return new YdbConfig(config.getString("endpoint"), config.getString("database"), config.getString("token"));
    }
}
