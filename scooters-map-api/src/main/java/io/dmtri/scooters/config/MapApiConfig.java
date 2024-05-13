package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public record MapApiConfig(GrpcConfig grpcConfig, MetricsConfig metricsConfig, MapConfig mapConfig) {
    public static MapApiConfig fromConfiguration(Configuration configuration) {
        return new MapApiConfig(
                GrpcConfig.fromConfiguration(configuration.subset("grpc")),
                MetricsConfig.fromConfiguration(configuration.subset("metrics")),
                MapConfig.fromConfiguration(configuration.subset("map"))
        );
    }
}
