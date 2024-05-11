package io.dmtri.scooters;

import io.dmtri.scooters.config.AppConfig;
import io.dmtri.scooters.config.AppConfigFactory;
import io.dmtri.scooters.persistence.ScooterStatusDao;
import io.dmtri.scooters.persistence.ydb.Ydb;
import io.dmtri.scooters.persistence.ydb.YdbScooterStatusDao;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

class Main {
    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfigFactory.getAppConfig();

        try (Ydb ydb = new Ydb(config.getYdbConfig())) {
            ScooterStatusDao scooterStatusDao = new YdbScooterStatusDao("scooter_status", ydb.getCtx());
            ScootersApiImpl apiImpl = new ScootersApiImpl(scooterStatusDao);
            Server server = ServerBuilder.forPort(config.getGrpcConfig().getPort()).addService(apiImpl)
                    .addService(ProtoReflectionService.newInstance()).build();
            server.start();
            System.out.println("Started server on port " + config.getGrpcConfig().getPort());
            while (true) {
            }
        }
    }
}