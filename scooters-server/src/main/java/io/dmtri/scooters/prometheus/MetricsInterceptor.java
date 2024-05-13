package io.dmtri.scooters.prometheus;

import io.grpc.*;
import io.prometheus.metrics.core.metrics.Histogram;

public class MetricsInterceptor implements ServerInterceptor {
    private static final Histogram grpcRequests = Histogram
            .builder()
            .labelNames("method", "status")
            .classicUpperBounds(.005, .01, .03, .05, .1, .2, .3, .5, .8, 1.0, 2.0, 3.0, 5.0, 10)
            .name("grpc_requests")
            .register();


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        return serverCallHandler.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
            private final long start = System.currentTimeMillis();
            @Override
            public void close(Status status, Metadata trailers) {
                grpcRequests
                        .labelValues(serverCall.getMethodDescriptor().getFullMethodName(), status.getCode().name())
                        .observe((double) (System.currentTimeMillis() - start) / 1000);
                super.close(status, trailers);
            }
        }, metadata);
    }
}
