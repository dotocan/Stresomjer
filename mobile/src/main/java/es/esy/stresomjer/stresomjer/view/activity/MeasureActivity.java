package es.esy.stresomjer.stresomjer.view.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.DateFormat;
import java.util.Date;

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

public class MeasureActivity extends AppCompatActivity
        implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Result screen views
    private TextView tvRetry, tvSaveToDatabase;
    private TextView tvReceivedBpm, tvDate, tvTime;
    private TextView tvNoActivity, tvLightActivity, tvModerateActivity, tvHeavyActivity;

    // Loading screen views
    private RelativeLayout rlFullScreenLoadingPulsingHeart;
    private ImageView imgPulsingHeart, imgCancelMeasureAction;
    private TextView tvStartMeasuring;
    private TextView tvMeasureInstructions;

    private static final String LOG_TAG = "Stresomjer phone";
    private GoogleApiClient mGoogleApiClient;

    private int count = 0;

    private int bpm_value = 0;
    private String date_measured;
    private String time_measured;
    private String user_id;
    private String activity;

    // Used to switch rlFullScreenLoadingPulsingHeart between measuring and non-measuring state;
    private boolean isInMeasuringState = false;

    private ObjectAnimator objectAnimator;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        // Getting SharedPreferences data
        sharedPreferences = getSharedPreferences("Login", 0);
        user_id = sharedPreferences.getString(Constants.UNIQUE_ID, "");

        initViews();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        isInMeasuringState = false;
        toggleMeasuringState();
        showHeartScreen();
    }

    // tvStartMeasuring onClick method
    public void startMeasuring(View v) {
        isInMeasuringState = true;
        toggleMeasuringState();
        sendMeasureRequest();
    }

    // imgCancelMeasureAction onClick method
    public void cancelMeasureAction(View v) {
        isInMeasuringState = false;
        toggleMeasuringState();
        hideHeartScreen();
    }

    // tvRetry onClick method
    public void retryMeasurement(View v) {
        isInMeasuringState = false;
        toggleMeasuringState();
        showHeartScreen();
    }

    // tvSaveToDatabase onClick method
    public void saveMeasurement(View v) {
        if (bpm_value <= 0) {
            Toast.makeText(MeasureActivity.this, getString(R.string.measurement_required), Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(activity)) {
            Toast.makeText(MeasureActivity.this, getString(R.string.activity_level_required),
                    Toast.LENGTH_SHORT).show();
        } else {
            sendToDatabase(user_id, bpm_value, date_measured, time_measured, activity);
        }
    }

    // Sets all icon and text colors to grey
    private void resetActivitiesColors() {
        // setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom)

        tvNoActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_hotel_grey_48dp, 0, 0);
        tvNoActivity.setTextColor(ContextCompat.getColor(this, R.color.colorText));

        tvLightActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_walk_grey_48dp, 0, 0);
        tvLightActivity.setTextColor(ContextCompat.getColor(this, R.color.colorText));

        tvModerateActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_run_grey_48dp, 0, 0);
        tvModerateActivity.setTextColor(ContextCompat.getColor(this, R.color.colorText));

        tvHeavyActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bike_grey_48dp, 0, 0);
        tvHeavyActivity.setTextColor(ContextCompat.getColor(this, R.color.colorText));
    }

    // tvNoActivity onClick method
    public void setNoActivity(View v) {
        resetActivitiesColors();

        // Sets the selected activity icon and text color to red
        tvNoActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_hotel_red_48dp, 0, 0);
        tvNoActivity.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        activity = getString(R.string.none);
    }

    // tvLightActivity tvNoActivity onClick method
    public void setLightActivity(View v) {
        resetActivitiesColors();

        // Sets the selected activity icon and text color to red
        tvLightActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_walk_red_48dp, 0, 0);
        tvLightActivity.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        activity = getString(R.string.light);
    }

    // tvModerateActivity tvNoActivity onClick method
    public void setModerateActivity(View v) {
        resetActivitiesColors();

        // Sets the selected activity icon and text color to red
        tvModerateActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_run_red_48dp, 0, 0);
        tvModerateActivity.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        activity = getString(R.string.moderate);
    }

    // tvHeavyActivity tvNoActivity onClick method
    public void setHeavyActivity(View v) {
        resetActivitiesColors();

        // Sets the selected activity icon and text color to red
        tvHeavyActivity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_directions_bike_red_48dp, 0, 0);
        tvHeavyActivity.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        activity = getString(R.string.heavy);
    }


    public void sendToDatabase(String user_id, int bpm_value,
                               String date_measured, String time_measured, String activity) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SimpleMeasurementRequestInterface simpleMeasurementRequestInterface = retrofit.create(SimpleMeasurementRequestInterface.class);

        SimpleMeasurement simpleMeasurement = new SimpleMeasurement();
        simpleMeasurement.setUser_id(user_id);
        simpleMeasurement.setBpm_value(bpm_value);
        simpleMeasurement.setActivity(activity);
        simpleMeasurement.setDate_measured(date_measured);
        simpleMeasurement.setTime_measured(time_measured);

        SimpleMeasurementServerRequest request = new SimpleMeasurementServerRequest();
        request.setOperation(Constants.PUT_SIMPLE_MEASUREMENT_OPERATION);
        request.setSimpleMeasurement(simpleMeasurement);
        Call<SimpleMeasurementServerResponse> response = simpleMeasurementRequestInterface.operation(request);

        response.enqueue(new Callback<SimpleMeasurementServerResponse>() {
            @Override
            public void onResponse(Call<SimpleMeasurementServerResponse> call, Response<SimpleMeasurementServerResponse> response) {
                SimpleMeasurementServerResponse resp = response.body();
                Toast.makeText(MeasureActivity.this, resp.getMessage(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(MeasureActivity.this, MainActivity.class);
                startActivity(i);
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

        // Result screen views
        tvRetry = (TextView) findViewById(R.id.tv_retry);
        tvSaveToDatabase = (TextView) findViewById(R.id.tv_save_to_database);
        tvReceivedBpm = (TextView) findViewById(R.id.tv_received_bpm);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvNoActivity = (TextView) findViewById(R.id.tv_no_activity);
        tvLightActivity = (TextView) findViewById(R.id.tv_light_activity);
        tvModerateActivity = (TextView) findViewById(R.id.tv_moderate_activity);
        tvHeavyActivity = (TextView) findViewById(R.id.tv_heavy_activity);

        // Loading screen views
        rlFullScreenLoadingPulsingHeart = (RelativeLayout) findViewById(R.id.rl_full_screen_loading_pulsing_heart);
        imgPulsingHeart = (ImageView) findViewById(R.id.img_pulsing_heart);
        imgCancelMeasureAction = (ImageView) findViewById(R.id.img_cancel_measure_action);
        tvStartMeasuring = (TextView) findViewById(R.id.tv_start_measuring);
        tvMeasureInstructions = (TextView) findViewById(R.id.tv_measure_instructions);

        initObjectAnimator(imgPulsingHeart);
    }

    // Create a data map and put data in it
    private void sendMeasureRequest() {

        // TODO uncomment this code
        // Make the request urgent
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/start").setUrgent();
        putDataMapReq.getDataMap().putInt(Constants.START_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

        // Toast.makeText(MeasureActivity.this, "Started measuring", Toast.LENGTH_SHORT).show();
    }

    public void updateBpmText(int receivedBpmValue) {
        bpm_value = receivedBpmValue;
        String bpmText = getString(R.string.result_bpm) + " " + String.valueOf(receivedBpmValue);
        tvReceivedBpm.setText(bpmText);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Toast.makeText(MeasureActivity.this, "Connected to watch", Toast.LENGTH_SHORT).show();
        // sendMeasureRequest();
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
                    setDateTimeText();
                    hideHeartScreen();

                    isInMeasuringState = false;
                    toggleMeasuringState();
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void setDateTimeText() {
        date_measured = DateFormat.getDateInstance().format(new Date());
        time_measured = DateFormat.getTimeInstance().format(new Date());

        String dateTxt = getString(R.string.date) + " " + date_measured;
        String timeTxt = getString(R.string.time) + " " + time_measured;

        tvDate.setText(dateTxt);
        tvTime.setText(timeTxt);
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


    private void toggleMeasuringState() {
        if (isInMeasuringState) {
            tvMeasureInstructions.setText(getString(R.string.wait));
            tvStartMeasuring.setVisibility(View.GONE);
            imgCancelMeasureAction.setVisibility(View.VISIBLE);
            objectAnimator.start();
        } else {
            tvMeasureInstructions.setText(getString(R.string.measure_instructions));
            tvStartMeasuring.setVisibility(View.VISIBLE);
            imgCancelMeasureAction.setVisibility(View.GONE);
            objectAnimator.end();
        }
    }

    private void showHeartScreen() {
        rlFullScreenLoadingPulsingHeart.setVisibility(View.VISIBLE);
    }

    private void hideHeartScreen() {
        rlFullScreenLoadingPulsingHeart.setVisibility(View.GONE);
    }

    private void initObjectAnimator(ImageView imageView) {
        objectAnimator = ObjectAnimator.ofPropertyValuesHolder(imageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        objectAnimator.setDuration(310);

        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }
}