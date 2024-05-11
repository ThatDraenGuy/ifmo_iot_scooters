package io.dmtri.scooters.persistence;

import io.dmtri.scooters.Model;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface ScooterStatusDao {
    CompletableFuture<List<Model.ScooterTelemetry>> getScooters();

    CompletableFuture<Optional<Model.ScooterTelemetry>> getScooterById(String scooterId);

    CompletableFuture<Boolean> updateScooter(Model.ScooterTelemetry status);
}
