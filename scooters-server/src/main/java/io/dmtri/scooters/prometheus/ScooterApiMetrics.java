package io.dmtri.scooters.prometheus;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;

public class ScooterApiMetrics {
    public static final Counter statusReceived =
            Counter
                    .builder()
                    .name("scooters_status_received")
                    .labelNames("scooter_id")
                    .register();


    public static final Histogram scootersSpeed =
            Histogram
                    .builder()
                    .name("scooters_speed")
                    .classicLinearUpperBounds(0, 1, 31)
                    .register();
}
