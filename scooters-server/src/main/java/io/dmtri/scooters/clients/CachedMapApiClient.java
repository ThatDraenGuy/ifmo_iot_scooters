package io.dmtri.scooters.clients;

import io.dmtri.scooters.map.MapModel;
import io.dmtri.scooters.service.ScootersMapApiGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedMapApiClient implements MapApiClient {
    private final static Logger logger = LoggerFactory.getLogger(CachedMapApiClient.class);
    private final static long cacheRetentionMillis = 5000;
    private final ScootersMapApiGrpc.ScootersMapApiBlockingStub mapApiClient;
    private volatile long lastCachedMillis = 0;
    private volatile MapModel.SpeedLimitedZones cachedZones;

    public CachedMapApiClient(ScootersMapApiGrpc.ScootersMapApiBlockingStub mapApiClient) {
        this.mapApiClient = mapApiClient;
    }

    public synchronized MapModel.SpeedLimitedZones getSpeedLimitedZones() {
        long now = System.currentTimeMillis();
        if (now - lastCachedMillis > cacheRetentionMillis) {
            cachedZones = mapApiClient.getSpeedLimitedZones(MapModel.GetSpeedLimitedZonesRequest.getDefaultInstance());
            lastCachedMillis = now;
            logger.info("Updated cache with " + cachedZones.getZonesCount() + " zones");
        }
        return cachedZones;
    }
}
