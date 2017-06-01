package com.clabs.majorproject.singleton;


import com.google.android.gms.maps.model.LatLng;

public class LocationSingleton {

    private static LocationSingleton locationSingleton;
    private static LatLng latLng;

    private LocationSingleton(){}

    public static LocationSingleton getInstance() {
        if (locationSingleton == null)
            return new LocationSingleton();
        return locationSingleton;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        LocationSingleton.latLng = latLng;
    }
}
