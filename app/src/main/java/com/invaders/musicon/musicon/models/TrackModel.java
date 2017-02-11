package com.invaders.musicon.musicon.models;

import java.util.Comparator;

/**
 * Created by vipul on 2/11/17.
 */

public class TrackModel implements Comparable<TrackModel>{

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

    public int compareTo( TrackModel other) {
        double thisTempo = this.getFeatures().getTempo();
        double otherTempo = other.getFeatures().getTempo();
        if (  thisTempo > otherTempo ) {
            return 1;
        } else if ( thisTempo < otherTempo ) {
            return -1;
        } else {
            return 0;
        }

    }
}
