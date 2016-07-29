package es.esy.stresomjer.stresomjer.helper;

import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by domin on 7/28/2016.
 */
public interface SimpleMeasurementRequestInterface {
    @POST("simple-measurements/")
    Call<SimpleMeasurementServerResponse> operation(@Body SimpleMeasurementServerRequest simpleMeasurementServerRequest);

    @POST("simple-measurements/")
    Call<SimpleMeasurementServerResponse> getJSON(@Body SimpleMeasurementServerRequest simpleMeasurementServerRequest);
}
