package com.invaders.musicon.musicon;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by d_d on 2/10/17.
 */

public class ActivityRecognizedService extends IntentService {
    public String currentActivity = "";

    private LocalBroadcastManager broadcaster;


    static final public String ACT_RESULT = "com.controlj.copame.backend.COPAService.REQUEST_PROCESSED";

    static final public String ACT_MESSAGE = "com.controlj.copame.backend.COPAService.COPA_MSG";

    public void sendResult(String message) {
        Intent intent = new Intent(ACT_RESULT);
        if(message != null)
            intent.putExtra(ACT_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }


    /***
     * Return the string of current activity.
     * @return String (in_vehicle, on_bicycle, on_foot, running, still, tilting, walking, unknown)
     */
    public String getCurrentActivity() {
        return this.currentActivity;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            switch (result.getMostProbableActivity().getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    this.currentActivity = "in_vehicle";
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    this.currentActivity = "on_bicycle";
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    this.currentActivity = "on_foot";
                    break;
                }
                case DetectedActivity.RUNNING: {
                    this.currentActivity = "running";
                    break;
                }
                case DetectedActivity.STILL: {
                    this.currentActivity = "still";
                    break;
                }
                case DetectedActivity.TILTING: {
                    this.currentActivity = "tilting";
                    break;
                }
                case DetectedActivity.WALKING: {
                    this.currentActivity = "walking";
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    this.currentActivity = "unknown";
                    break;
                }

                default: {
                    this.currentActivity = "unknown";
                    break;
                }
            }
            Log.d("Activity: ", this.currentActivity);
            sendResult(this.currentActivity);
        }
    }
}
