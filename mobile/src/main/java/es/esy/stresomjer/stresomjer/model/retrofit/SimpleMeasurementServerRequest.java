package es.esy.stresomjer.stresomjer.model.retrofit;

import com.google.gson.annotations.SerializedName;

import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;

/**
 * Created by domin on 7/27/2016.
 */
public class SimpleMeasurementServerRequest {
    private String operation;
    @SerializedName("simple_measurement")
    private SimpleMeasurement simpleMeasurement;

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
}
