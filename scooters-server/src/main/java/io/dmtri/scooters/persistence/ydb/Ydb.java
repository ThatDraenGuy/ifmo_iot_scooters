package io.dmtri.scooters.persistence.ydb;

import io.dmtri.scooters.config.YdbConfig;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.auth.iam.CloudAuthIdentity;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;

public class Ydb implements AutoCloseable {
    private final GrpcTransport transport;
    private final TableClient client;
    private final SessionRetryContext retryContext;

    public Ydb(YdbConfig ydbConfig) {
        this.transport = GrpcTransport.forEndpoint(ydbConfig.getEndpoint(), ydbConfig.getDatabase())
                .withAuthProvider(CloudAuthHelper.getServiceAccountJsonAuthProvider(ydbConfig.getToken())).build();
        this.client = TableClient.newClient(transport).build();
        this.retryContext = SessionRetryContext.create(this.client).maxRetries(3).build();
    }

    @Override
    public void close() {
        client.close();
        transport.close();
    }

    public SessionRetryContext getCtx() {
        return retryContext;
    }
}
