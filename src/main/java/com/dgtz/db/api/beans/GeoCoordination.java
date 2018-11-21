package com.dgtz.db.api.beans;

/**
 * Created by sardor on 6/1/16.
 */
public class GeoCoordination {
    private double lat;
    private double lng;

    public GeoCoordination() {
        this.lat = 0.000;
        this.lng = 0.000;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
