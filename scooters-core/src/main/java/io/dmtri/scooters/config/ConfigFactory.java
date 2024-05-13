package io.dmtri.scooters.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.net.URL;

public abstract class ConfigFactory<T> {
    private static URL basicConfiguration = ConfigFactory.class.getResource("/config.properties");
    private static URL secretsConfiguration = ConfigFactory.class.getResource("/secrets.properties");

    public T getAppConfig() throws ConfigurationException {
        CompositeConfiguration config = new CompositeConfiguration();

        config.getInterpolator().addDefaultLookup(new CustomEnvLookup());

        SystemConfiguration sysConfig = new SystemConfiguration();
        EnvironmentConfiguration envConfig = new EnvironmentConfiguration();
        PropertiesConfiguration secretsConfig = new Configurations().properties(secretsConfiguration);
        PropertiesConfiguration basicConfig = new Configurations().properties(basicConfiguration);
        config.addConfiguration(sysConfig);
        config.addConfiguration(envConfig);
        config.addConfiguration(secretsConfig);
        config.addConfiguration(basicConfig);

        return fromConfiguration(config);
    }

    protected abstract T fromConfiguration(Configuration configuration);
}
