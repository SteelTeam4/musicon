package com.invaders.musicon.musicon.models;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.invaders.musicon.musicon.MainActivity;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.AudioFeatureRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.RemoveFromMySavedTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.AudioFeature;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SnapshotResult;
import com.wrapper.spotify.models.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by vipul on 2/10/17.
 */


public class SpotifyUtils {



    private Api getApiObject(String code) {

        final Api api = Api.builder()
                .clientId("a1c8dbd2755d4603a4bf953abfce567e")
                .clientSecret("398e6bba264642c18b6882581b91ec76")
                .redirectURI("musicon://callback")
                .build();
        api.setAccessToken(code);
        return api;
    }

    public List<PlaylistModel> getPlayLists(String code, String userId) {

        final Api api = getApiObject(code);

        List<PlaylistModel> modelList = null;
        try {
            final UserPlaylistsRequest request = api.getPlaylistsForUser(userId).build();
            final Page<SimplePlaylist> playlistsPage = request.get();
            PlaylistModel playlistModel = null;
            modelList = new ArrayList<PlaylistModel>();

            for (SimplePlaylist playlist : playlistsPage.getItems()) {
                playlistModel = new PlaylistModel();
                playlistModel.setUri(playlist.getUri());
                playlistModel.setTotalTracks(playlist.getTracks().getTotal());
                playlistModel.setPlaylistName(playlist.getName());
                playlistModel.setPlaylistId(playlist.getId());
                playlistModel.setTracks(getTracksFromPlayList(code, playlist.getOwner().getId(), playlist.getId()));
                modelList.add(playlistModel);

            }
        } catch (Exception e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        finally {
            return modelList;
        }
    }


    public List<PlaylistModel> getPlayListsRaw(String code, String userId) {

        final Api api = getApiObject(code);

        List<PlaylistModel> modelList = null;
        try {
            final UserPlaylistsRequest request = api.getPlaylistsForUser(userId).build();
            final Page<SimplePlaylist> playlistsPage = request.get();
            PlaylistModel playlistModel = null;
            modelList = new ArrayList<PlaylistModel>();

            for (SimplePlaylist playlist : playlistsPage.getItems()) {
                playlistModel = new PlaylistModel();
                playlistModel.setUri(playlist.getUri());
                playlistModel.setTotalTracks(playlist.getTracks().getTotal());
                playlistModel.setPlaylistName(playlist.getName());
                playlistModel.setPlaylistId(playlist.getId());
//                playlistModel.setTracks(getTracksFromPlayList(code, playlist.getOwner().getId(), playlist.getId()));
                modelList.add(playlistModel);
            }
        } catch (Exception e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        finally {
            return modelList;
        }
    }


    public List<TrackModel> getTracksFromPlayList(String code, String userId, String playlistId) {

        final Api api = getApiObject(code);

        List<TrackModel> modelList = null;
        try{
            final PlaylistTracksRequest request = api.getPlaylistTracks(userId, playlistId).build();
            final Page<PlaylistTrack> trackListPage = request.get();
            TrackModel trackModel = null;
            modelList = new ArrayList<TrackModel>();

            for (PlaylistTrack trackData : trackListPage.getItems()) {
                trackModel = new TrackModel();
                Track track  = trackData.getTrack();
                trackModel.setTrackName(track.getName());
                trackModel.setId(track.getId());
                trackModel.setUri(track.getUri());
                trackModel.setFeatures(getFeaturesForTrack(code, track.getId()));
                modelList.add(trackModel);
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong!" + e.getMessage());
        }
        finally {
            return modelList;
        }

    }


    public TrackFeatures getFeaturesForTrack(String code, String trackId) {

        final Api api = getApiObject(code);
        TrackFeatures features = null;

        try {
            features = new TrackFeatures();
            AudioFeatureRequest  audFeatRequest = api.getAudioFeature(trackId).build();
            AudioFeature audFeat = audFeatRequest.get();
            features.setTrackId(audFeat.getId());
            features.setUri(audFeat.getUri());
            features.setTempo(audFeat.getTempo());
            features.setDanceability(audFeat.getDanceability());
            features.setLiveness(audFeat.getLiveness());
            features.setLoudness(audFeat.getLoudness());
            features.setEnergy(audFeat.getEnergy());
        }
        catch(Exception e) {
            System.out.println("Something went wrong!" + e.getMessage());
        }
        finally {
            return features;
        }
    }


    public void addTracksToPlayList(String code, String userId, String playlistId, ArrayList<String> trackURIs) {

        final Api api = getApiObject(code);

        AddTrackToPlaylistRequest request;

        try {
            request = api.addTracksToPlaylist(userId, playlistId, trackURIs).build();
            request.postJson();
        }

        catch(Exception e) {
            e.printStackTrace();
            System.out.println("Something went wrong!" + e.getMessage());
        }
        finally {

        }

    }

    public void removeTrackFromPlayList(String code, String userId, String playlistId, String trackId) {

        final Api api = getApiObject(code);

        try {
            RemoveFromMySavedTracksRequest request = api.removeFromMySavedTracks(new ArrayList<String>(Arrays.asList(trackId))).build();
            String result = request.get();
            System.out.println(result);
        }

        catch(Exception e) {

        }
        finally {

        }
    }

    public Playlist createPlaylist(String code, String userId, String playlistTitle) {

        final Api api = getApiObject(code);
        final PlaylistCreationRequest request = api.createPlaylist(userId, playlistTitle)
                .publicAccess(false)
                .build();

        Playlist playlist = null;
        try {
            playlist = request.get();
            System.out.println("You just created this playlist!");
            System.out.println("Its title is " + playlist.getName());
        } catch (Exception e) {
            System.out.println("Something went wrong in Create Playlist!" + e.getMessage());
        }finally {
            return playlist;
        }
    }




}
