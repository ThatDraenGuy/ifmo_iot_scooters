package io.dmtri.scooters;

import io.dmtri.scooters.config.AppConfig;
import io.dmtri.scooters.config.AppConfigFactory;
import io.dmtri.scooters.persistence.ScooterStatusDao;
import io.dmtri.scooters.persistence.ydb.Ydb;
import io.dmtri.scooters.persistence.ydb.YdbScooterStatusDao;
import io.dmtri.scooters.prometheus.MetricsInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfigFactory.getAppConfig();

        // Metrics
        JvmMetrics.builder().register();
        MetricsInterceptor metricsInterceptor = new MetricsInterceptor();

        try (Ydb ydb = new Ydb(config.ydbConfig());
             HTTPServer metricsServer = HTTPServer.builder().port(config.metricsConfig().port()).buildAndStart()) {
            ScooterStatusDao scooterStatusDao = new YdbScooterStatusDao("scooter_status", ydb.getCtx());
            ScootersApiImpl apiImpl = new ScootersApiImpl(scooterStatusDao);
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