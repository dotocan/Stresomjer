package es.esy.stresomjer.stresomjer.view.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.widget.AdapterView;
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

public class MeasureActivity extends AppCompatActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AdapterView.OnItemSelectedListener {

    private TextView tvRetry, tvSaveToDatabase;
    private TextView tvReceivedBpm, tvDate, tvTime;
    private RelativeLayout rlFullScreenLoadingPulsingHeart;
    private ImageView imgPulsingHeart;
    // TextViews with images
    private TextView tvNoActivity, tvLightActivity, tvModerateActivity, tvHeavyActivity;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        startMeasuring();
    }

    // tvRetry onClick method
    public void retryMeasurement(View v) {
        startMeasuring();
    }

    // tvSaveToDatabase onClick method
    public void saveMeasurement(View v) {
        if (TextUtils.isEmpty(activity)) {
            Toast.makeText(MeasureActivity.this, getString(R.string.activity_level_required), Toast.LENGTH_SHORT).show();
        } else {
            sendToDatabase(user_id, bpm_value, activity);
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

        // Initializing the widgets
        tvRetry = (TextView) findViewById(R.id.tv_retry);
        tvSaveToDatabase = (TextView) findViewById(R.id.tv_save_to_database);
        tvReceivedBpm = (TextView) findViewById(R.id.tv_received_bpm);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);

        tvNoActivity = (TextView) findViewById(R.id.tv_no_activity);
        tvLightActivity = (TextView) findViewById(R.id.tv_light_activity);
        tvModerateActivity = (TextView) findViewById(R.id.tv_moderate_activity);
        tvHeavyActivity = (TextView) findViewById(R.id.tv_heavy_activity);

        rlFullScreenLoadingPulsingHeart = (RelativeLayout) findViewById(R.id.rl_full_screen_loading_pulsing_heart);
        imgPulsingHeart = (ImageView) findViewById(R.id.img_pulsing_heart);

        createPulsingEffect(imgPulsingHeart);
    }

    // Creating the pulse animation
    private void createPulsingEffect(ImageView imageView) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(imageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();
    }

    // Create a data map and put data in it
    private void startMeasuring() {
        showPulsingHeart();

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/start");
        putDataMapReq.getDataMap().putInt(Constants.START_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    public void updateBpmText(int receivedBpmValue) {
        bpm_value = receivedBpmValue;
        String bpmText = getString(R.string.result_bpm) + String.valueOf(receivedBpmValue);
        tvReceivedBpm.setText(bpmText);
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

                    setDateTimeText();
                    hidePulsingHeart();
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    public void setDateTimeText() {
        String currentDateString = DateFormat.getDateInstance().format(new Date());
        String currentTimeString = DateFormat.getTimeInstance().format(new Date());

        String dateTxt = getString(R.string.date) + currentDateString;
        String timeTxt = getString(R.string.time) + currentTimeString;

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity = String.valueOf(parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showPulsingHeart() {
        rlFullScreenLoadingPulsingHeart.setVisibility(View.VISIBLE);
    }

    private void hidePulsingHeart() {
        rlFullScreenLoadingPulsingHeart.setVisibility(View.GONE);
    }
}
