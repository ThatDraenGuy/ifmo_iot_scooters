package io.dmtri.scooters;

import io.dmtri.scooters.clients.MapApiClient;
import io.dmtri.scooters.map.MapModel;
import io.dmtri.scooters.persistence.ScooterStatusDao;
import io.dmtri.scooters.prometheus.ScooterApiMetrics;
import io.dmtri.scooters.utils.VectorLength;
import io.dmtri.scooters.utils.ZoneUtils;
import io.grpc.Status;
import io.dmtri.scooters.service.ScootersApiGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ScootersApiImpl extends ScootersApiGrpc.ScootersApiImplBase {
    private static final Logger logger = LoggerFactory.getLogger(ScootersApiImpl.class);
    private final ScooterStatusDao scooterStatusDao;
    private final MapApiClient mapApiClient;

    public ScootersApiImpl(ScooterStatusDao scooterStatusDao, MapApiClient mapApiClient) {
        this.scooterStatusDao = scooterStatusDao;
        this.mapApiClient = mapApiClient;
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
        ScooterApiMetrics.statusReceived.labelValues(request.getScooterId()).inc();
        ScooterApiMetrics.scootersSpeed.observe(VectorLength.calculateLength(request.getSpeed()));

        float newSpeedLimit = 1000;
        MapModel.SpeedLimitedZones zones = mapApiClient.getSpeedLimitedZones();

        for (MapModel.SpeedLimitZone zone : zones.getZonesList()) {
            if (!ZoneUtils.vectorInZone(request.getCoordinates(), zone.getBox())) continue;
            newSpeedLimit = Math.min(newSpeedLimit, zone.getSpeedLimit());
        }

        float finalNewSpeedLimit = newSpeedLimit;
        handleFuture(scooterStatusDao.updateScooter(request), responseObserver,
                result -> Model.ScooterTelemetryResponse.newBuilder().setSpeedLimit(finalNewSpeedLimit).build());
    }

    @Override
    public void getStatuses(Model.ScooterStatusesRequest request,
                            StreamObserver<Model.ScooterStatusesResponse> responseObserver) {
        handleFuture(scooterStatusDao.getScooters(), responseObserver,
                result -> Model.ScooterStatusesResponse.newBuilder().addAllStatuses(result).build());
    }

    @Override
    public void getSpeedLimitedZones(MapModel.GetSpeedLimitedZonesRequest request, StreamObserver<MapModel.SpeedLimitedZones> responseObserver) {
        responseObserver.onNext(mapApiClient.getSpeedLimitedZones());
        responseObserver.onCompleted();
    }
}
