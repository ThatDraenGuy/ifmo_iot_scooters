package io.dmtri.scooters.prometheus;

import io.prometheus.metrics.core.metrics.Counter;

public class ServerMetrics {
    public static final Counter statusReceived =
            Counter
                    .builder()
                    .name("scooters_status_received")
                    .register();


}
