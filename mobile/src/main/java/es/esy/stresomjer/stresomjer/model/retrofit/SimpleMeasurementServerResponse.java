package es.esy.stresomjer.stresomjer.model.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;

/**
 * Created by domin on 7/27/2016.
 */
public class SimpleMeasurementServerResponse {
    private String result;
    private String message;

    @SerializedName("simple_measurement")
    private SimpleMeasurement[] simpleMeasurements;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SimpleMeasurement[] getSimpleMeasurements() {
        return simpleMeasurements;
    }

    public void setSimpleMeasurements(SimpleMeasurement[] simpleMeasurements) {
        this.simpleMeasurements = simpleMeasurements;
    }
}
