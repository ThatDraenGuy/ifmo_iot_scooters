package io.dmtri.scooters;

import io.dmtri.scooters.config.MapApiConfig;
import io.dmtri.scooters.config.MapApiConfigFactory;
import io.dmtri.scooters.map.MapGenerator;
import io.dmtri.scooters.prometheus.MetricsInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        MapApiConfig config = new MapApiConfigFactory().getConfig();
        MapGenerator generator = new MapGenerator(config.mapConfig());

        // Metrics
        JvmMetrics.builder().register();
        MetricsInterceptor metricsInterceptor = new MetricsInterceptor();

        try (HTTPServer metrics = HTTPServer.builder().port(config.metricsConfig().port()).buildAndStart()) {
            ScooterMapApiImpl apiImpl = new ScooterMapApiImpl(generator);
            Server server = ServerBuilder.forPort(config.grpcConfig().port())
                    .addService(ServerInterceptors.intercept(apiImpl, metricsInterceptor))
                    .addService(ProtoReflectionService.newInstance()).build();
            server.start();
            logger.info("Started server on port " + config.grpcConfig().port());
            logger.info("Metrics server listening on port " + config.metricsConfig().port());

            Thread.currentThread().join();
        }
    }
}
