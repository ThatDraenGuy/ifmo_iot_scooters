package io.dmtri.scooters.clients;

import io.dmtri.scooters.map.MapModel;

public interface MapApiClient {
    MapModel.SpeedLimitedZones getSpeedLimitedZones();
}
