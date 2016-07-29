package es.esy.stresomjer.stresomjer.model.retrofit;

import com.google.gson.annotations.SerializedName;

import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;
import es.esy.stresomjer.stresomjer.model.User;

/**
 * Created by domin on 7/27/2016.
 */
public class SimpleMeasurementServerRequest {
    private String operation;
    @SerializedName("simple_measurement")
    private SimpleMeasurement simpleMeasurement;
    @SerializedName("user")
    private User user;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public SimpleMeasurement getSimpleMeasurement() {
        return simpleMeasurement;
    }

    public void setSimpleMeasurement(SimpleMeasurement simpleMeasurement) {
        this.simpleMeasurement = simpleMeasurement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
