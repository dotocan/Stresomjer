package es.esy.stresomjer.stresomjer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener {

    private SensorManager mSensorManager;
    private static final String LOG_TAG = "Stresomjer wear";

    private TextView tvBpm;
    private TextView tvStatus;

    private static final String START_KEY = "es.esy.stresomjer.stresomjer.start";
    private static final String BPM_KEY = "es.esy.stresomjer.stresomjer.bpm";
    private GoogleApiClient mGoogleApiClient;

    private int currentValue = 0;
    private int numOfMeasurements = 0;
    private static final int MAX_MEASUREMENTS = 5;
    private int avgBpmValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvBpm = (TextView) stub.findViewById(R.id.tv_bpm);
                tvStatus = (TextView) stub.findViewById(R.id.tv_status);
                tvStatus.setText(R.string.wait);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    // Create a data map and put data in it
    private void sendHeartbeatToPhone(int avgValue) {
        // Make the request urgent
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/bpm").setUrgent();
        putDataMapReq.getDataMap().putInt(BPM_KEY, avgValue);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

        Log.d(LOG_TAG, "Heartbeat sent to phone: " + avgValue);
    }

    private void startMeasuring(int count) {
        // Register us as a sensor listener
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        // Delay SENSOR_DELAY_UI is sufficiant
        boolean res = mSensorManager.registerListener(MainActivity.this, mHeartRateSensor, SensorManager.SENSOR_DELAY_UI);
        Log.d(LOG_TAG, " sensor registered: " + (res ? "yes" : "no"));

        tvBpm.setVisibility(View.INVISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/start") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(LOG_TAG, "Received data key: " + dataMap.getInt(START_KEY));
                    startMeasuring(dataMap.getInt(START_KEY));

                    // Reset num of measurements and avg bpm value to 0
                    numOfMeasurements = 0;
                    avgBpmValue = 0;

                    tvBpm.setText(String.valueOf(dataMap.getInt(START_KEY)));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Checking if we are getting heartbeat sensor data and if
        // that data is not 0

        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE && sensorEvent.values.length > 0) {

            int newValue = Math.round(sensorEvent.values[0]);

            // Only do something if the value differs from before and is not 0.
            if (currentValue != newValue && newValue != 0) {

                if (numOfMeasurements >= MAX_MEASUREMENTS) {
                    avgBpmValue = avgBpmValue / numOfMeasurements;
                    Log.d(LOG_TAG, "Avg value: "
                            + avgBpmValue);

                    mSensorManager.unregisterListener(this);
                    Log.d(LOG_TAG, " sensor unregistered");

                    tvStatus.setVisibility(View.INVISIBLE);
                    tvBpm.setVisibility(View.VISIBLE);
                    tvBpm.setText(String.valueOf(avgBpmValue));

                    sendHeartbeatToPhone(avgBpmValue);

                } else {
                    // Save the new value
                    numOfMeasurements++;
                    currentValue = newValue;
                    avgBpmValue += currentValue;

                    Log.d(LOG_TAG, "Num of measurements: "
                            + numOfMeasurements
                            + "\n"
                            + "BPM value: "
                            + currentValue);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
            Log.d(LOG_TAG, " sensor unregistered");
        }
    }
}