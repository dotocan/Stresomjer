package es.esy.stresomjer.stresomjer.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import es.esy.stresomjer.stresomjer.R;

public class MeasureActivity extends AppCompatActivity  implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private Button btnMeasure;
    private TextView tvReceivedBpm;
    private static final String LOG_TAG = "Stresomjer phone";

    private static final String START_KEY = "es.esy.stresomjer.stresomjer.start";
    private static final String BPM_KEY = "es.esy.stresomjer.stresomjer.bpm";
    private GoogleApiClient mGoogleApiClient;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        btnMeasure = (Button) findViewById(R.id.btn_measure);
        tvReceivedBpm = (TextView) findViewById(R.id.tv_received_bpm);

        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvReceivedBpm.setText(R.string.wait);
                startMeasuring();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    /* ==================== MY CUSTOM METHODS START ==================== */
    // Create a data map and put data in it
    private void startMeasuring() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/start");
        putDataMapReq.getDataMap().putInt(START_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

//        btnMeasure.setVisibility(View.INVISIBLE);
    }

    public void updateBpmText(int receivedBpmValue) {
        tvReceivedBpm.setText(String.valueOf(receivedBpmValue));
//        btnMeasure.setVisibility(View.VISIBLE);
    }
    /* ==================== MY CUSTOM METHODS END ==================== */

    /* ==================== GOOGLE API METHODS START ==================== */
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
                if (item.getUri().getPath().compareTo("/bpm") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(LOG_TAG, "Received data key: " + dataMap.getInt(BPM_KEY));
                    updateBpmText(dataMap.getInt(BPM_KEY));
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
    /* ==================== GOOGLE API METHODS END ==================== */

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
}
