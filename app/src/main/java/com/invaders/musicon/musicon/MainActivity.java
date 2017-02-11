package com.invaders.musicon.musicon;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.invaders.musicon.musicon.models.PlaylistModel;
import com.invaders.musicon.musicon.models.SpotifyUtils;
import com.invaders.musicon.musicon.models.TrackModel;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Track;

import org.apache.commons.io.filefilter.FalseFileFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    SpotifyUtils spotifyUtils;
    private List<TrackModel> tracksToArrange;
    private ArrayList<ArrayList<String>> arrangedTracks;
    private HashMap<String,String> playlistMap;
    private boolean readyToControl = false;

    private static final String CLIENT_ID = "a1c8dbd2755d4603a4bf953abfce567e";
    private static final String REDIRECT_URI = "musicon://callback";
    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;
    private static String oAuthToken;

    private static final String USER_NAME = "vmassa";

    private static final String TAG = "MainActivityTAG";
    private SpotifyPlayer mPlayer;
    private PlaybackState mCurrentPlaybackState;

    private static final List<String> TEST_ALBUM_TRACKS = Arrays.asList(
            "spotify:track:0MllcbBCVEvDJ1jQBbfnp0",
            "spotify:track:0Cim88rt6HG172g8yLSYS5",
            "spotify:track:1mq49Np3oRodGq0HM4h3C9",
            "spotify:track:7djK8tEU7rmzqU73aKBBEG",
            "spotify:track:0CSBgxYPRCSd1Qzo1EzAN3"
    );

    private BroadcastReceiver mNetworkStateReceiver;
    private Metadata mMetadata;
    private int songIdx;

    private String current_playing_song = new String("slow walking");
    Queue<String> activity_window;
    HashMap<String,Integer> activity_count;

    class PlayListLoaderBalancerTask extends AsyncTask<String,String,String> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,"Setting up","Wait...");
            arrangedTracks = new ArrayList<>();
            for ( int i = 0; i < 5; i++ ) {
                arrangedTracks.add( new ArrayList<String>() );
            }
        }

        protected String doInBackground(String... params) {
            List<PlaylistModel> rawPlayLists = spotifyUtils.getPlayListsRaw(params[0],params[1]);
            playlistMap = new HashMap<>();
            boolean checkExists = false;
            for ( PlaylistModel playList : rawPlayLists ) {
                if( playList.getPlaylistName().equals("0") || playList.getPlaylistName().equals("1") || playList.getPlaylistName().equals("2") ||
                        playList.getPlaylistName().equals("3") || playList.getPlaylistName().equals("4") ) {
                    playlistMap.put( playList.getPlaylistName(),playList.getUri() );
                    checkExists = true;
                }
            }

            if ( checkExists ) {
                Log.d(TAG, "Party! Everything done!");
                Log.d( TAG, playlistMap + "" );
                return "done";
            }

            List<PlaylistModel> playLists = spotifyUtils.getPlayLists(params[0],params[1]);

            for ( PlaylistModel playList : playLists ) {
                Log.d( "XXXX", playList.getUri() );
                if ( playList.getUri().equals("spotify:user:vmassa:playlist:4lUNKm1cZRgOH3qhO6Znss") ) {

                    tracksToArrange = spotifyUtils.getTracksFromPlayList(params[ 0 ], params[ 1 ], playList.getPlaylistId() );
                    Log.d(  "XXXX", tracksToArrange + "" );
                    break;
                }
            }
            Collections.sort( tracksToArrange );
            for ( TrackModel tm : tracksToArrange ) {
                if (tm.getFeatures().getTempo() < 85){
                    arrangedTracks.get( 0 ).add( tm.getUri() );
                } else if ( tm.getFeatures().getTempo() >= 85 && tm.getFeatures().getTempo() < 100 ) {
                    arrangedTracks.get( 1 ).add( tm.getUri() );
                } else if ( tm.getFeatures().getTempo() >= 100 && tm.getFeatures().getTempo() <= 120 ) {
                    arrangedTracks.get( 2 ).add( tm.getUri() );
                } else if ( tm.getFeatures().getTempo() >= 120 && tm.getFeatures().getTempo() <= 135 ) {
                    arrangedTracks.get( 3 ).add( tm.getUri() );
                } else {
                    arrangedTracks.get( 4 ).add( tm.getUri() );
                }
            }
            List<Playlist> playlists = new ArrayList<>();
            Log.d( TAG,"Creating playlists" );


            for (int j = 0; j < 5; j ++) {
                playlists.add(spotifyUtils.createPlaylist(oAuthToken, USER_NAME, j + ""));
            }

            Log.d( TAG,"Created playlists" );


            for ( int i = 0; i < playlists.size(); i++ ) {
                Log.d( "Playlist: ", playlists.get(i).getUri() );
                playlistMap.put(playlists.get(i).getName(),playlists.get(i).getUri());
                spotifyUtils.addTracksToPlayList( oAuthToken,USER_NAME,playlists.get(i).getId(),arrangedTracks.get(i) );
            }
            return "resp";
        }

        protected void onPostExecute(String v) {
            progressDialog.dismiss();
            readyToControl = true;
            Log.d( "XXXX" + v, "complete" );
        }
    }

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            Log.d(TAG, "OK!");
        }

        @Override
        public void onError(Error error) {
            Log.d( TAG, "ERROR:" + error );
        }
    };

    private BroadcastReceiver receiver;

    public GoogleApiClient mApiClient;

    private TextView activity_text;

    private TextView act_speed;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static int ACTIVITY_WINDOW_SIZE = 50;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            long curTime = System.currentTimeMillis();
            String current_activity=new String("");
            if ((curTime - lastUpdate) > 200) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
                if (speed < 300 && speed > 50){
                    Log.d("V","i'm in "+ System.currentTimeMillis() + "slow walking");
                    current_activity = "slow walking";
                }
                else if (speed > 300 && speed <800) {
                    Log.d("V","i'm in "+ System.currentTimeMillis() + "walking");
                    current_activity = "walking";
                }
                else if (speed > 800 && speed <2500) {
                    Log.d("V","i'm in "+ System.currentTimeMillis() + "Jogging");
                    current_activity = "Jogging";
                }
                else if (speed > 2500 ) {
                    Log.d("V","i'm in "+ System.currentTimeMillis() + "Sprint");
                    current_activity = "Sprint";
                }else{
                    current_activity = "still";
                }

                last_x = x;
                last_y = y;
                last_z = z;
                if (readyToControl) {
                    Log.d( TAG,"Ready to control!" );
                    if (activity_window.size() == ACTIVITY_WINDOW_SIZE) {
                        activity_window.add(current_activity);
                        update_activity_count(current_activity, 1);
                        String removed_activity = activity_window.remove();
                        update_activity_count(removed_activity, -1);
                        String new_playing_song = findMaxActivity();
                        Log.d( TAG, activity_count + " " );
                        if ((!new_playing_song.equals(current_playing_song))) {
                            //switch songs
                            Log.d("V", "Switching song to " + new_playing_song);
                            act_speed.setText(new_playing_song);
                            current_playing_song = new_playing_song;
                            mPlayer.setShuffle(mOperationCallback,true);
                            switch (current_playing_song) {
                                case "slow walking":
                                    mPlayer.playUri(mOperationCallback, playlistMap.get("1"), 0, 0);
                                    break;

                                case "walking":
                                    mPlayer.playUri(mOperationCallback, playlistMap.get("2"), 0, 0);
                                    break;
                                case "Jogging":
                                    mPlayer.playUri(mOperationCallback, playlistMap.get("3"), 0, 0);
                                    break;

                                case "Sprint":
                                    mPlayer.playUri(mOperationCallback, playlistMap.get("4"), 0, 0);
                                    break;

                                default:
                                    mPlayer.playUri(mOperationCallback, playlistMap.get("0"), 0, 0);
                                    break;
                            }
                            Toast.makeText(getApplicationContext(),"speed: " + current_playing_song,Toast.LENGTH_LONG).show();
                        }
                    } else {
                        activity_window.add(current_activity);
                        update_activity_count(current_activity, 1);
                    }
                }
            }
        }
    }



    public void update_activity_count(String activity,int update_count){
        if (activity_count.containsKey(activity)){
            activity_count.put(activity,activity_count.get(activity) + update_count);
            //System.out.println(activity+(activity_count.get(activity) + update_count));
        }
        else{
            activity_count.put(activity,1);
        }
    }

    public String findMaxActivity(){
        String max_activity = new String("");
        int max_count = 0;
        for (String ac : activity_count.keySet()){
            if (activity_count.get(ac) > max_count ){
                max_count = activity_count.get(ac);
                max_activity = ac;
            }
        }
        //System.out.println(activity_count.get(max_activity));
        return max_activity;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 6000, pendingIntent );
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(ActivityRecognizedService.ACT_RESULT)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(ActivityRecognizedService.ACT_MESSAGE);
                activity_text.setText(s);
                Toast.makeText(getApplicationContext(),"activity_text: " + s,Toast.LENGTH_LONG).show();
            }
        };

        //mApiClient.connect();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        spotifyUtils = new SpotifyUtils();
        activity_count = new HashMap<String,Integer>();
        activity_count.put("slow walking",0);
        activity_count.put("walking",0);
        activity_count.put("Jogging",0);
        activity_count.put("Sprint",0);
        activity_window = new LinkedList<>();

        initUIElements();
    }


    private void initUIElements() {
        activity_text = (TextView)findViewById(R.id.activity_value);
        act_speed = (TextView)findViewById((R.id.speed_act));
        Button bSpotifyLogin = (Button)findViewById(R.id.bSpotifyLogin);
        Button bPlay = (Button)findViewById(R.id.bPlay);
        Button bNext = (Button)findViewById(R.id.bNext);
        bSpotifyLogin.setOnClickListener(this);
        bPlay.setOnClickListener(this);
        bNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch ( id ) {
            case R.id.bSpotifyLogin:
                onLoginButtonClicked();
                break;
            case R.id.bPlay:
                Log.d(TAG,"Clicked Play");
                onPlayButtonClicked();
                break;
            case R.id.bNext:
                Log.d(TAG,"Clicked Next");
                break;
        }
    }


    private void openLoginWindow() {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming","playlist-modify-private", "playlist-modify-public"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        if (mPlayer == null) {
            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
            oAuthToken = authResponse.getAccessToken();
            // we call an asych to run the code to get the playlist related information
            // new SpotifyUtils().execute(authResponse.getAccessToken(), USER_NAME);


            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
            // the second argument in order to refcount it properly. Note that the method
            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
            // one passed in here. If you pass different instances to Spotify.getPlayer() and
            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                @Override
                public void onInitialized(SpotifyPlayer player) {
                    mPlayer.addNotificationCallback(MainActivity.this);
                    mPlayer.addConnectionStateCallback(MainActivity.this);
                    mPlayer.setShuffle(mOperationCallback,true);
                }

                @Override
                public void onError(Throwable error) {
                    Log.d(TAG,"Error in initialization: " + error.getMessage());
                }
            });

        } else {
            mPlayer.login(authResponse.getAccessToken());
            oAuthToken = authResponse.getAccessToken();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    onAuthenticationComplete(response);
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG,"Auth result: " + response.getType());
            }
        }
    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    public void onLoginButtonClicked() {
        if (!isLoggedIn()) {
            Log.d(TAG,"Logging in");
            openLoginWindow();
        } else {
            mPlayer.logout();
        }
    }

    public void onPlayButtonClicked() {
        Log.d(TAG, "Clicked Play");
        Log.d(TAG,""+songIdx);
        songIdx = 0;
        mPlayer.playUri(mOperationCallback,TEST_ALBUM_TRACKS.get(songIdx++),0,0);
    }


    public void onSkipToNextButtonClicked() {
        mPlayer.skipToNext(mOperationCallback);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {
        // Remember kids, always use the English locale when changing case for non-UI strings!
        // Otherwise you'll end up with mysterious errors when running in the Turkish locale.
        // See: http://java.sys-con.com/node/46241
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        mMetadata = mPlayer.getMetadata();
        Log.i(TAG, "Player state: " + mCurrentPlaybackState);
        Log.i(TAG, "Metadata: " + mMetadata);
        if (mCurrentPlaybackState.isPlaying)
            if (mMetadata.currentTrack != null)
                if (mMetadata.currentTrack.name != null)
                    activity_text.setText(mMetadata.currentTrack.name + mMetadata.currentTrack.artistName);
//        if ( event == PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone ) {
//            Log.d( TAG, "Song just got over :(" );
//            if ( songIdx >= TEST_ALBUM_TRACKS.size() ) {
//                this.finish();
//            } else {
//                Log.d(TAG,""+songIdx);
//                mPlayer.playUri(mOperationCallback,TEST_ALBUM_TRACKS.get(songIdx++),0,0);
//
//            }
//        }
    }

    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the broadcast receiver for network events. Note that we also unregister
        // this receiver again in onPause().
        mNetworkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mPlayer != null) {
                    Connectivity connectivity = getNetworkConnectivity(getBaseContext());
                    Log.d(TAG, "Network state changed: " + connectivity.toString());
                    mPlayer.setConnectivityStatus(mOperationCallback, connectivity);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);

        if (mPlayer != null) {
            mPlayer.addNotificationCallback(MainActivity.this);
            mPlayer.addConnectionStateCallback(MainActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkStateReceiver);

        // Note that calling Spotify.destroyPlayer() will also remove any callbacks on whatever
        // instance was passed as the refcounted owner. So in the case of this particular example,
        // it's not strictly necessary to call these methods, however it is generally good practice
        // and also will prevent your application from doing extra work in the background when
        // paused.
        if (mPlayer != null) {
            mPlayer.removeNotificationCallback(MainActivity.this);
            mPlayer.removeConnectionStateCallback(MainActivity.this);
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        try {
            new PlayListLoaderBalancerTask().execute(oAuthToken,USER_NAME);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error e) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

}
