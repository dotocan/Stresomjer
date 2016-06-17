package es.esy.stresomjer.stresomjer;

import es.esy.stresomjer.stresomjer.model.ServerRequest;
import es.esy.stresomjer.stresomjer.model.ServerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by domin on 6/9/2016.
 */
public interface RequestInterface {

    /*
    The endpoint is defined using @POST annotation. Request URL
    is http://stresomjer.esy.es/login-register/,
    where http://stresomjer.esy.es/ is base url and login-register/ is endpoint.
     */
    @POST("login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
