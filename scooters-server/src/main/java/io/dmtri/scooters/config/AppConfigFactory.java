package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public class AppConfigFactory extends ConfigFactory<AppConfig> {
    @Override
    protected AppConfig fromConfiguration(Configuration configuration) {
        return AppConfig.fromConfiguration(configuration);
    }
}
