package es.esy.stresomjer.stresomjer.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by domin on 7/24/2016.
 */

public class SimpleMeasurement {
    private String user_id;
    private int bpm_value;
    private String activity;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getBpm_value() {
        return bpm_value;
    }

    public void setBpm_value(int bpm_value) {
        this.bpm_value = bpm_value;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
