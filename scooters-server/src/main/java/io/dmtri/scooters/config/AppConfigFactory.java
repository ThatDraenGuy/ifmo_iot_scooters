package io.dmtri.scooters.config;

import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.net.URL;

public class AppConfigFactory {
    private static URL basicConfiguration = AppConfigFactory.class.getResource("/config.properties");
    private static URL secretsConfiguration = AppConfigFactory.class.getResource("/secrets.properties");

    public static AppConfig getAppConfig() throws ConfigurationException {
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

        return AppConfig.fromConfiguration(config);
    }
}
