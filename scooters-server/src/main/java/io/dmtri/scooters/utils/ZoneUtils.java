package io.dmtri.scooters.utils;

import io.dmtri.scooters.Model;
import io.dmtri.scooters.map.MapModel;

public class ZoneUtils {
    public static boolean vectorInZone(Model.Vector v, MapModel.BoundingBox box) {
        return v.getX() >= box.getTopLeftCorner().getX() &&
                v.getX() <= box.getBottomRightCorner().getX() &&
                v.getY() >= box.getBottomRightCorner().getY() &&
                v.getY() <= box.getTopLeftCorner().getY();
    }
}
