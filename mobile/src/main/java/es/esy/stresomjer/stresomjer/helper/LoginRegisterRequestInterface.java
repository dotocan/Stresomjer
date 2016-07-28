package es.esy.stresomjer.stresomjer.helper;

import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.SimpleMeasurementServerResponse;
import es.esy.stresomjer.stresomjer.model.retrofit.UserServerRequest;
import es.esy.stresomjer.stresomjer.model.retrofit.UserServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by domin on 6/9/2016.
 */
public interface LoginRegisterRequestInterface {
    @POST("login-register/")
    Call<UserServerResponse> operation(@Body UserServerRequest userServerRequest);
}
