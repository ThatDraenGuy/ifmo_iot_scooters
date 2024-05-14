package io.dmtri.scooters.utils;

import io.dmtri.scooters.Model;

public class VectorLength {
    public static double calculateLength(Model.Vector vector) {
        return Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
    }
}
