package io.dmtri.scooters.map;

import io.dmtri.scooters.Model;
import io.dmtri.scooters.config.MapConfig;
import io.dmtri.scooters.prometheus.MapApiMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class MapGenerator {
    private final static Logger logger = LoggerFactory.getLogger(MapGenerator.class);

    private final MapConfig mapConfig;

    public MapGenerator(MapConfig mapConfig) {
        this.mapConfig = mapConfig;
    }

    public MapModel.SpeedLimitedZones generateSpeedLimitedZones() {
        long version = System.currentTimeMillis() / mapConfig.regenerationIntervalMillis();
        Random random = new Random(version);
        int zonesCount = random.nextInt(mapConfig.minZones(), mapConfig.maxZones() + 1);
        MapModel.SpeedLimitedZones zones = generateSpeedLimitedZones(random, zonesCount);
        reportTotalAreaCovered(zones);
        return zones.toBuilder().setVersion(version).build();
    }

    private void reportTotalAreaCovered(MapModel.SpeedLimitedZones zones) {
        float area = 0;

        for (int i = 0; i < zones.getZonesCount(); i++) {
            MapModel.BoundingBox box1 = zones.getZones(i).getBox();
            area += calculateArea(zones.getZones(i).getBox());
            for (int j = i+1; j < zones.getZonesCount(); j++) {
                MapModel.BoundingBox box2 = zones.getZones(j).getBox();
                float intersectingLeft = Math.max(box1.getTopLeftCorner().getX(), box2.getTopLeftCorner().getX());
                float intersectingRight = Math.min(box1.getBottomRightCorner().getX(), box2.getBottomRightCorner().getX());
                float intersectingTop = Math.min(box1.getTopLeftCorner().getY(), box2.getTopLeftCorner().getY());
                float intersectingBottom = Math.max(box1.getBottomRightCorner().getY(), box2.getBottomRightCorner().getY());

                // Is intersected?
                if (intersectingLeft < intersectingRight && intersectingBottom < intersectingTop) {
                    area -= calculateArea(MapModel.BoundingBox
                            .newBuilder()
                            .setTopLeftCorner(Model.Vector.newBuilder().setX(intersectingLeft).setY(intersectingTop))
                            .setBottomRightCorner(Model.Vector.newBuilder().setX(intersectingRight).setY(intersectingBottom))
                            .build()
                    );
                }
            }
        }

        float percentage = area / (mapConfig.mapSize() * mapConfig.mapSize() * 4) * 100;

        MapApiMetrics.speedLimitZonesCoverage.set(area);
        MapApiMetrics.speedLimitZonesCoveragePercentage.set(percentage);

        logger.info("Generated " + zones.getZonesCount() + " zones covering " + area + "m2 (" + percentage + ")");
    }

    private float calculateArea(MapModel.BoundingBox box) {
        float width = box.getBottomRightCorner().getX() - box.getTopLeftCorner().getX();
        float height = box.getTopLeftCorner().getY() - box.getBottomRightCorner().getY();

        return width * height;
    }

    private MapModel.SpeedLimitedZones generateSpeedLimitedZones(Random random, int count) {
        MapModel.SpeedLimitedZones.Builder builder = MapModel.SpeedLimitedZones.newBuilder();

        for (int i = 0; i < count; i++)
            builder.addZones(generateSpeedLimitZone(random));

        return builder.build();
    }

    private MapModel.SpeedLimitZone generateSpeedLimitZone(Random random) {
        MapModel.BoundingBox zone = generateZone(random);
        float speedLimit = random.nextFloat(mapConfig.minSpeedLimit(), mapConfig.maxSpeedLimit());

        return MapModel.SpeedLimitZone.newBuilder()
                .setBox(zone)
                .setSpeedLimit(speedLimit)
                .build();
    }

    private MapModel.BoundingBox generateZone(Random random) {
        float width = random.nextFloat(mapConfig.minZoneSize(), mapConfig.maxZoneSize());
        float height = random.nextFloat(mapConfig.minZoneSize(), mapConfig.maxZoneSize());
        float xOrigin = random.nextFloat(-mapConfig.mapSize(), mapConfig.mapSize() - width);
        float yOrigin = random.nextFloat(-mapConfig.mapSize(), mapConfig.mapSize() - height);

        return MapModel.BoundingBox.newBuilder()
                .setTopLeftCorner(Model.Vector.newBuilder().setX(xOrigin).setY(yOrigin + height))
                .setBottomRightCorner(Model.Vector.newBuilder().setX(xOrigin + width).setY(yOrigin))
                .build();
    }
}
