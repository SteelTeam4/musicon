package com.invaders.musicon.musicon.service;

import com.invaders.musicon.musicon.models.PlaylistModel;
import com.invaders.musicon.musicon.models.TrackFeatures;
import com.invaders.musicon.musicon.models.TrackModel;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AbstractRequest;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.AlbumRequest;
import com.wrapper.spotify.methods.AudioFeatureRequest;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.RemoveFromMySavedTracksRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.methods.UserRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.AudioFeature;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SimpleTrack;
import com.wrapper.spotify.models.SnapshotResult;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by vipul on 2/10/17.
 */


public class SpotifyUtils extends AsyncTask <String, Void, String> {


    protected String doInBackground(String... params) {
            getPlayLists(params[0], params[1]);
            return "Done";
    }

    protected void onPostExecute(Void feed) {
    }



    private static Api getApiObject(String code) {

        final Api api = Api.builder()
                .clientId("a1c8dbd2755d4603a4bf953abfce567e")
                .clientSecret("398e6bba264642c18b6882581b91ec76")
                .redirectURI("musicon://callback")
                .build();
        api.setAccessToken(code);
        return api;
    }

    public static List<PlaylistModel> getPlayLists(String code, String userId) {

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



    public static List<TrackModel> getTracksFromPlayList(String code, String userId, String playlistId) {

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


    public static TrackFeatures getFeaturesForTrack(String code, String trackId) {

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


    public static void addTrackToPlayList(String code, String userId, String playlistId, String trackId) {

        final Api api = getApiObject(code);

        try {
            AddTrackToPlaylistRequest request = api.addTracksToPlaylist(userId, playlistId, new ArrayList<String>(Arrays.asList(trackId))).build();
            SnapshotResult result = request.get();

            String id = result.getSnapshotId();
            System.out.println(id);
        }

        catch(Exception e) {

        }
        finally {

        }

    }

    public static void removeTrackFromPlayList(String code, String userId, String playlistId, String trackId) {

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




}
