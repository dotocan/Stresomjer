package es.esy.stresomjer.stresomjer.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.adapter.MainAdapter;
import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.helper.SimpleMeasurementRequestInterface;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;
import es.esy.stresomjer.stresomjer.model.User;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CardContentFragment extends Fragment {

    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private ArrayList<SimpleMeasurement> simpleMeasurementList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Getting SharedPreferences data
        sharedPreferences = getActivity().getSharedPreferences("Login", 0);
        user_id = sharedPreferences.getString(Constants.UNIQUE_ID, "");

        View rootView = inflater.inflate(R.layout.fragment_card_content, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_cards);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        adapter = new MainAdapter(simpleMeasurementList);

        loadJSON();

        return rootView;
    }

    private void loadJSON() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                //.client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SimpleMeasurementRequestInterface simpleMeasurementRequestInterface = retrofit.create(SimpleMeasurementRequestInterface.class);

        User user = new User();
        user.setUser_id(user_id);

        final SimpleMeasurementServerRequest request = new SimpleMeasurementServerRequest();
        request.setOperation(Constants.GET_SIMPLE_MEASUREMENTS_OPERATION);
        request.setUser(user);
        Call<SimpleMeasurementServerResponse> call = simpleMeasurementRequestInterface.getJSON(request);

        call.enqueue(new Callback<SimpleMeasurementServerResponse>() {
            @Override
            public void onResponse(Call<SimpleMeasurementServerResponse> call, Response<SimpleMeasurementServerResponse> response) {
                Log.v("Lista", "Došlo je do response-a");
                Log.v("Lista", String.valueOf(response.body().getSimpleMeasurements()));

                simpleMeasurementList = new ArrayList<>(Arrays.asList(response.body().getSimpleMeasurements()));

                adapter = new MainAdapter(simpleMeasurementList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<SimpleMeasurementServerResponse> call, Throwable t) {
                Log.v("Lista", "Nije došlo do responsea");
                Log.v("Lista", t.getMessage());
            }
        });
    }
}
