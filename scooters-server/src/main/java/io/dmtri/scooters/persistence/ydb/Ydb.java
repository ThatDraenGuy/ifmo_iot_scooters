package io.dmtri.scooters.persistence.ydb;

import io.dmtri.scooters.config.YdbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.TokenAuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;

public class Ydb implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(Ydb.class);
    private final GrpcTransport transport;
    private final TableClient client;
    private final SessionRetryContext retryContext;

    public Ydb(YdbConfig ydbConfig) {
        AuthProvider authProvider;

        if (ydbConfig.token() != null && !ydbConfig.token().isEmpty()) {
            logger.info("Using TokenAuthProvider for YDB");
            authProvider = new TokenAuthProvider(ydbConfig.token());
        } else {
            logger.info("Token not found in configuration, using auth provider from environment");
            authProvider = CloudAuthHelper.getAuthProviderFromEnviron();
        }

        this.transport = GrpcTransport.forEndpoint(ydbConfig.endpoint(), ydbConfig.database())
                .withAuthProvider(authProvider).build();
        this.client = TableClient.newClient(transport).sessionPoolSize(200, 500).build();
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
