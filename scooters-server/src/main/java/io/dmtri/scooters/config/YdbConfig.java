package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public class YdbConfig {
    private final String endpoint;
    private final String database;
    private final String token;

    public YdbConfig(String endpoint, String database, String token) {
        this.endpoint = endpoint;
        this.database = database;
        this.token = token;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getDatabase() {
        return database;
    }

    public String getToken() {
        return token;
    }

    public static YdbConfig fromConfiguration(Configuration config) {
        return new YdbConfig(config.getString("endpoint"), config.getString("database"), config.getString("token"));
    }
}
