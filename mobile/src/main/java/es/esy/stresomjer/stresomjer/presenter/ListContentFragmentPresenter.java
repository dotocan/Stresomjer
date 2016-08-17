package es.esy.stresomjer.stresomjer.presenter;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.ArrayList;
import java.util.Arrays;

import es.esy.stresomjer.stresomjer.adapter.ListAdapter;
import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.helper.SimpleMeasurementRequestInterface;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;
import es.esy.stresomjer.stresomjer.model.User;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerResponse;
import es.esy.stresomjer.stresomjer.view.interfaces.ListContentFragmentView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dotocan@croz.net on 17.8.2016..
 */
public class ListContentFragmentPresenter extends MvpBasePresenter<ListContentFragmentView> {
    private ArrayList<SimpleMeasurement> simpleMeasurementList = new ArrayList<>();

    public ListContentFragmentPresenter() {

    }

    public void loadData(final boolean pullToRefresh, String user_id) {
        simpleMeasurementList = new ArrayList<>();

        if (isViewAttached()) {
            getView().showLoading(pullToRefresh);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SimpleMeasurementRequestInterface simpleMeasurementRequestInterface =
                retrofit.create(SimpleMeasurementRequestInterface.class);

        User user = new User();
        user.setUser_id(user_id);

        final SimpleMeasurementServerRequest request = new SimpleMeasurementServerRequest();
        request.setOperation(Constants.GET_SIMPLE_MEASUREMENTS_OPERATION);
        request.setUser(user);
        Call<SimpleMeasurementServerResponse> call = simpleMeasurementRequestInterface.getJSON(request);

        call.enqueue(new Callback<SimpleMeasurementServerResponse>() {
            @Override
            public void onResponse(Call<SimpleMeasurementServerResponse> call,
                                   Response<SimpleMeasurementServerResponse> response) {

                Log.v("Lista", "Došlo je do response-a");
                Log.v("Lista", String.valueOf(response.body().getSimpleMeasurements()));

                if (response.body().getSimpleMeasurements() != null) {
                    simpleMeasurementList = new ArrayList<>(Arrays.asList(response.body().getSimpleMeasurements()));
                    getView().hideEmptyList();
                    getView().setData(simpleMeasurementList);
                    getView().showContent();
                    getView().cancelRefreshIcon();
                } else {
                    getView().showContent();
                    getView().cancelRefreshIcon();
                    getView().showEmptyList();
                }

            }

            @Override
            public void onFailure(Call<SimpleMeasurementServerResponse> call, Throwable t) {
                Log.v("Lista", "Nije došlo do responsea");
                Log.v("Lista", t.getMessage());
                getView().showError(t, pullToRefresh);
            }
        });
    }
}
