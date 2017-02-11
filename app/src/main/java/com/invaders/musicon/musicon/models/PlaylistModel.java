package com.invaders.musicon.musicon.models;

import java.util.List;

/**
 * Created by vipul on 2/11/17.
 */

public class PlaylistModel {

    private String uri;
    private String playlistName;
    private String playlistId;
    private int totalTracks;
    private List<TrackModel> tracks;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public List<TrackModel> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackModel> tracks) {
        this.tracks = tracks;
    }
}
