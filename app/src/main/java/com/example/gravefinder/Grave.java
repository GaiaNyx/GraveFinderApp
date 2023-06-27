package com.example.gravefinder;

import java.util.ArrayList;

public class Grave {
    private String graveId;
    private String graveName;
    private String birthDate;
    private String deathDate;
    private String description;
    private Double longitude;
    private Double latitude;

    public Grave(String graveId, String graveName, String birthDate, String deathDate,
                 String description, Double longitude, Double latitude) {
        this.graveId = graveId;
        this.graveName = graveName;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Grave() {
    }

    public String getGraveId() {
        return graveId;
    }

    public void setGraveId(String graveId) {
        this.graveId = graveId;
    }

    public String getGraveName() {
        return graveName;
    }

    public void setGraveName(String graveName) {
        this.graveName = graveName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

}
