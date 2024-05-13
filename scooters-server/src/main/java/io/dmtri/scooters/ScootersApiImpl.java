package io.dmtri.scooters;

import io.dmtri.scooters.persistence.ScooterStatusDao;
import io.dmtri.scooters.persistence.ydb.Ydb;
import io.dmtri.scooters.persistence.ydb.YdbScooterStatusDao;
import io.dmtri.scooters.prometheus.ServerMetrics;
import io.grpc.Status;
import io.dmtri.scooters.service.ScootersApiGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ScootersApiImpl extends ScootersApiGrpc.ScootersApiImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ScootersApiImpl.class);
    private final ScooterStatusDao scooterStatusDao;

    public ScootersApiImpl(ScooterStatusDao scooterStatusDao) {
        this.scooterStatusDao = scooterStatusDao;
    }

    private <T, U> void handleFuture(CompletableFuture<T> future, StreamObserver<U> responseObserver,
            Function<T, U> mappingFunction) {
        future.handle((result, exception) -> {
            if (exception != null) {
                logger.error("Error handling request", exception);
                responseObserver.onError(Status.INTERNAL.withCause(exception)
                        .withDescription("An internal error occured: " + exception).asException());
            } else {
                responseObserver.onNext(mappingFunction.apply(result));
            }
            responseObserver.onCompleted();
            return result;
        });
    }

    @Override
    public void ping(Model.ScooterApiPingRequest request, StreamObserver<Model.ScooterApiPingResponse> responseObserver) {
        responseObserver.onNext(Model.ScooterApiPingResponse.newBuilder().setMessage("ok").build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendTelemetry(Model.ScooterTelemetry request,
            StreamObserver<Model.ScooterTelemetryResponse> responseObserver) {
        ServerMetrics.statusReceived.inc();
        handleFuture(scooterStatusDao.updateScooter(request), responseObserver,
                result -> Model.ScooterTelemetryResponse.newBuilder().setSpeedLimit(1000).build());
    }

    @Override
    public void getStatuses(Model.ScooterStatusesRequest request,
            StreamObserver<Model.ScooterStatusesResponse> responseObserver) {
        handleFuture(scooterStatusDao.getScooters(), responseObserver,
                result -> Model.ScooterStatusesResponse.newBuilder().addAllStatuses(result).build());
    }
}
