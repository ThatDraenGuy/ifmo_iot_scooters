package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record AppConfig(GrpcConfig grpcConfig, MetricsConfig metricsConfig, YdbConfig ydbConfig, MapApiConfig mapApiConfig) {

    public static AppConfig fromConfiguration(Configuration configuration) {
        return new AppConfig(GrpcConfig.fromConfiguration(configuration.subset("grpc")),
                MetricsConfig.fromConfiguration(configuration.subset("metrics")),
                YdbConfig.fromConfiguration(configuration.subset("ydb")),
                MapApiConfig.fromConfiguration(configuration.subset("map-api")));
    }
}
