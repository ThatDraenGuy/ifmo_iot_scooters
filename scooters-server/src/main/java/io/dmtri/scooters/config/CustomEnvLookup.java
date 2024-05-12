package io.dmtri.scooters.config;

import org.apache.commons.configuration2.interpol.Lookup;

public class CustomEnvLookup implements Lookup {
    @Override
    public Object lookup(String s) {
        if (System.getenv().containsKey(s)) {
            return System.getenv(s);
        } else {
            return "";
        }
    }
}
