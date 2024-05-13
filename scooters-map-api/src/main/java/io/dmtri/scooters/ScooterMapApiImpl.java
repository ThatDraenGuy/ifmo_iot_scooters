package io.dmtri.scooters;

import io.dmtri.scooters.map.MapGenerator;
import io.dmtri.scooters.map.MapModel;
import io.dmtri.scooters.service.ScootersMapApiGrpc;
import io.grpc.stub.StreamObserver;

public class ScooterMapApiImpl extends ScootersMapApiGrpc.ScootersMapApiImplBase {
    private final MapGenerator mapGenerator;

    public ScooterMapApiImpl(MapGenerator mapGenerator) {
        this.mapGenerator = mapGenerator;
    }

    @Override
    public void getSpeedLimitedZones(MapModel.GetSpeedLimitedZonesRequest request, StreamObserver<MapModel.SpeedLimitedZones> responseObserver) {
        MapModel.SpeedLimitedZones zones = mapGenerator.generateSpeedLimitedZones();

        responseObserver.onNext(zones);
        responseObserver.onCompleted();
    }
}
