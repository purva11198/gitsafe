package com.smartcitypune.smartpune;

import java.io.Serializable;

public class RoadProblem implements Serializable {
    public String userId;
    public double lat;
    public double lng;
    public String message_text;
    public String category;
    public Integer date;
    public String fireStorageReference;
    public String status;

    public RoadProblem() {
    }

    public RoadProblem(String userId, double lat, double lng, String message_text, String category, Integer date, String fireStorageReference, String status) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
        this.message_text = message_text;
        this.category = category;
        this.date = date;
        this.fireStorageReference = fireStorageReference;
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoadProblem{" +
                "userId='" + userId + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", message_text='" + message_text + '\'' +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", fireStorageReference='" + fireStorageReference + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getFireStorageReference() {
        return fireStorageReference;
    }

    public void setFireStorageReference(String fireStorageReference) {
        this.fireStorageReference = fireStorageReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
