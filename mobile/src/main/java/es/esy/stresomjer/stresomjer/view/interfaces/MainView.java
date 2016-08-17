package es.esy.stresomjer.stresomjer.view.interfaces;

import android.content.Intent;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by dotocan@croz.net on 8.8.2016..
 */
public interface MainView extends MvpView {
    // Switch to another Activity
    void switchActivity(Intent i);
}
