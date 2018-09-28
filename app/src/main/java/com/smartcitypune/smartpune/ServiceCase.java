package com.smartcitypune.smartpune;

public class ServiceCase {
    String id;
    double lat;
    double lng;
    ServiceJob job;

    @Override
    public String toString() {
        return "ServiceCase{" +
                "id='" + id + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", job=" + job +
                '}';
    }

    public ServiceCase() {
    }

    public ServiceCase(String id, double lat, double lng, ServiceJob job) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ServiceJob getJob() {
        return job;
    }

    public void setJob(ServiceJob job) {
        this.job = job;
    }
}
