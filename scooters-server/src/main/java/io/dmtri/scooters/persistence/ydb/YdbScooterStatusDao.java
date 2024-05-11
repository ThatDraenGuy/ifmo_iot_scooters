package io.dmtri.scooters.persistence.ydb;

import com.google.protobuf.InvalidProtocolBufferException;
import io.dmtri.scooters.Model;
import io.dmtri.scooters.persistence.ScooterStatusDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.query.Params;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.transaction.TxControl;
import tech.ydb.table.values.PrimitiveValue;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class YdbScooterStatusDao implements ScooterStatusDao {
    private static final Logger logger = LoggerFactory.getLogger(YdbScooterStatusDao.class);
    private final String tableName;
    private final SessionRetryContext ctx;

    public YdbScooterStatusDao(String tableName, SessionRetryContext ctx) {
        this.tableName = tableName;
        this.ctx = ctx;
    }

    @Override
    public CompletableFuture<List<Model.ScooterTelemetry>> getScooters() {
        TxControl<TxControl.TxSnapshotRo> tx = TxControl.snapshotRo().setCommitTx(false);
        List<Model.ScooterTelemetry> result = new LinkedList<>();
        return getScootersRec(tx, result, "").thenApply(ignored -> result);
    }

    private CompletableFuture<Void> getScootersRec(TxControl<?> tx, List<Model.ScooterTelemetry> accumulator,
            String last_scooter_id) {
        final String query = "DECLARE $last_scooter_id AS Utf8;" + "SELECT payload, scooter_id FROM " + tableName
                + " WHERE scooter_id > $last_scooter_id ORDER BY scooter_id ASC";

        return ctx.supplyResult(session -> session
                .executeDataQuery(query, tx, Params.of("$last_scooter_id", PrimitiveValue.newText(last_scooter_id)))
                .thenApply(result -> {
                    result.getStatus().expectSuccess("Failed to get scooter statuses");
                    ResultSetReader reader = result.getValue().getResultSet(0);

                    while (reader.next()) {
                        try {
                            accumulator.add(Model.ScooterTelemetry.parseFrom(reader.getColumn("payload").getBytes()));
                        } catch (InvalidProtocolBufferException e) {
                            logger.error("Failed to decode scooter status payload from YDB", e);
                        }
                    }

                    return result;
                })).thenCompose(result -> {
                    if (result.getValue().getResultSet(0).isTruncated())
                        return getScootersRec(tx, accumulator, accumulator.get(accumulator.size() - 1).getScooterId());
                    else
                        return CompletableFuture.allOf();
                });
    }

    @Override
    public CompletableFuture<Optional<Model.ScooterTelemetry>> getScooterById(String scooterId) {
        final String query = "DECLARE $scooter_id AS Utf8;" + "SELECT payload FROM " + tableName + ";";

        return ctx.supplyResult(session -> session.executeDataQuery(query, TxControl.onlineRo(),
                Params.of("$scooter_id", PrimitiveValue.newText(scooterId)))).thenApply(result -> {
                    ResultSetReader reader = result.getValue().getResultSet(0);
                    if (reader.next()) {
                        try {
                            return Optional
                                    .of(Model.ScooterTelemetry.parseFrom(reader.getColumn("payload").getBytes()));
                        } catch (InvalidProtocolBufferException e) {
                            return Optional.empty();
                        }
                    } else {
                        return Optional.empty();
                    }
                });
    }

    @Override
    public CompletableFuture<Boolean> updateScooter(Model.ScooterTelemetry status) {
        final String query = "DECLARE $scooter_id AS Utf8;" + "DECLARE $payload AS String;"
                + "DECLARE $timestamp AS Uint64;" + "REPLACE INTO " + tableName + "(scooter_id, payload, update_ts)"
                + "VALUES ($scooter_id, $payload, $timestamp)";

        return ctx.supplyResult(session -> session.executeDataQuery(query, TxControl.serializableRw(),
                Params.of("$scooter_id", PrimitiveValue.newText(status.getScooterId()), "$payload",
                        PrimitiveValue.newBytes(status.toByteArray()), "$timestamp",
                        PrimitiveValue.newUint64(status.getTimestamp()))))
                .thenApply(result -> {
                    result.getStatus().expectSuccess("failed to update scooter status");
                    return result.getStatus().isSuccess();
                });
    }
}
