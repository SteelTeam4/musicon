package com.invaders.musicon.musicon.models;

/**
 * Created by vipul on 2/11/17.
 */

public class TrackModel {

    private String id;
    private String trackName;
    private String uri;
    private TrackFeatures features;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public TrackFeatures getFeatures() {
        return features;
    }

    public void setFeatures(TrackFeatures features) {
        this.features = features;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}
