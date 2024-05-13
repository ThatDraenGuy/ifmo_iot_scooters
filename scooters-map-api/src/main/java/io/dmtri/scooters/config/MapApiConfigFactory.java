package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public class MapApiConfigFactory extends ConfigFactory<MapApiConfig> {
    @Override
    protected MapApiConfig fromConfiguration(Configuration configuration) {
        return MapApiConfig.fromConfiguration(configuration);
    }
}
