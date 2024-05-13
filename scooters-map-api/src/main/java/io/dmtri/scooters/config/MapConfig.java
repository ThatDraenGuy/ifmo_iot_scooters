package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record MapConfig(long regenerationIntervalMillis, float mapSize, int minZones, int maxZones, float minZoneSize,
                        float maxZoneSize, float minSpeedLimit, float maxSpeedLimit) {
    public static MapConfig fromConfiguration(Configuration configuration) {
        return new MapConfig(
                configuration.getLong("regeneration-interval-millis"),
                configuration.getInt("size"),
                configuration.getInt("min-zones"),
                configuration.getInt("max-zones"),
                configuration.getInt("min-zone-size"),
                configuration.getInt("max-zone-size"),
                configuration.getFloat("min-speed-limit"),
                configuration.getFloat("max-speed-limit")
        );
    }
}
