package io.dmtri.scooters.config;

import org.apache.commons.configuration2.Configuration;

public class AppConfig {
    private final GrpcConfig grpcConfig;
    private final YdbConfig ydbConfig;

    public AppConfig(GrpcConfig grpcConfig, YdbConfig ydbConfig) {
        this.grpcConfig = grpcConfig;
        this.ydbConfig = ydbConfig;
    }

    public GrpcConfig getGrpcConfig() {
        return grpcConfig;
    }

    public YdbConfig getYdbConfig() {
        return ydbConfig;
    }

    public static AppConfig fromConfiguration(Configuration configuration) {
        return new AppConfig(GrpcConfig.fromConfiguration(configuration.subset("grpc")),
                YdbConfig.fromConfiguration(configuration.subset("ydb")));
    }
}
