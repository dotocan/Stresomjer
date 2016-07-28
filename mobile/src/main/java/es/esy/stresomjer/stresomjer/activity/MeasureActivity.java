package es.esy.stresomjer.stresomjer.activity;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.helper.SimpleMeasurementRequestInterface;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeasureActivity extends AppCompatActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemSelectedListener {

    private Button btnMeasure, btnSend;
    private TextView tvReceivedBpm;
    private Spinner spinnerActivities;

    private static final String LOG_TAG = "Stresomjer phone";
    private GoogleApiClient mGoogleApiClient;

    private int count = 0;
    private String activity;
    private String user_id;
    private int bpm_value;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // Getting SharedPreferences data
        sharedPreferences = getSharedPreferences("Login", 0);
        user_id = sharedPreferences.getString(Constants.UNIQUE_ID, "");

        initViews();

        btnMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvReceivedBpm.setText(R.string.wait);
                startMeasuring();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpm_value = Integer.parseInt(tvReceivedBpm.getText().toString());
                sendToDatabase(user_id, bpm_value, activity);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void sendToDatabase(String user_id, int bpm_value, String activity) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SimpleMeasurementRequestInterface simpleMeasurementRequestInterface = retrofit.create(SimpleMeasurementRequestInterface.class);

        SimpleMeasurement simpleMeasurement = new SimpleMeasurement();
        simpleMeasurement.setUser_id(user_id);
        simpleMeasurement.setBpm_value(bpm_value);
        simpleMeasurement.setActivity(activity);

        Toast.makeText(MeasureActivity.this,
                "User id: " + simpleMeasurement.getUser_id() +
                        "\n\nActivity: " + simpleMeasurement.getActivity() +
                        "\n\nBPM: " + String.valueOf(simpleMeasurement.getBpm_value()),
                Toast.LENGTH_SHORT).show();

        SimpleMeasurementServerRequest request = new SimpleMeasurementServerRequest();
        request.setOperation(Constants.PUT_SIMPLE_MEASUREMENT_OPERATION);
        request.setSimpleMeasurement(simpleMeasurement);
        Call<SimpleMeasurementServerResponse> response = simpleMeasurementRequestInterface.operation(request);

        response.enqueue(new Callback<SimpleMeasurementServerResponse>() {
            @Override
            public void onResponse(Call<SimpleMeasurementServerResponse> call, Response<SimpleMeasurementServerResponse> response) {
                SimpleMeasurementServerResponse resp = response.body();
                Toast.makeText(MeasureActivity.this, resp.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SimpleMeasurementServerResponse> call, Throwable t) {
                Toast.makeText(MeasureActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initViews() {
        // Adding Toolbar to the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnMeasure = (Button) findViewById(R.id.btn_measure);
        btnSend = (Button) findViewById(R.id.btn_send);
        tvReceivedBpm = (TextView) findViewById(R.id.tv_received_bpm);
        spinnerActivities = (Spinner) findViewById(R.id.spinner_activities);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities_array, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerActivities.setAdapter(adapter);

        spinnerActivities.setOnItemSelectedListener(this);

    }

    // Create a data map and put data in it
    private void startMeasuring() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/start");
        putDataMapReq.getDataMap().putInt(Constants.START_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

//        btnMeasure.setVisibility(View.INVISIBLE);
    }

    public void updateBpmText(int receivedBpmValue) {
        tvReceivedBpm.setText(String.valueOf(receivedBpmValue));
//        btnMeasure.setVisibility(View.VISIBLE);
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
                if (item.getUri().getPath().compareTo("/bpm") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(LOG_TAG, "Received data key: " + dataMap.getInt(Constants.BPM_KEY));
                    updateBpmText(dataMap.getInt(Constants.BPM_KEY));
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
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(
                MeasureActivity.this,
                String.valueOf(parent.getItemAtPosition(position)),
                Toast.LENGTH_SHORT).show();

        activity = String.valueOf(parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
