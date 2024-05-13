package io.dmtri.scooters.prometheus;

import io.prometheus.metrics.core.metrics.Gauge;

public class MapApiMetrics {
    public static final Gauge speedLimitZonesCoverage =
            Gauge
                    .builder()
                    .name("speed_limit_zones_size")
                    .register();

    public static final Gauge speedLimitZonesCoveragePercentage =
            Gauge
                    .builder()
                    .name("speed_limit_zones_percentage")
                    .register();
}
