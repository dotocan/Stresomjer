package es.esy.stresomjer.stresomjer.view.interfaces;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.ArrayList;
import java.util.List;

import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;

/**
 * Created by dotocan@croz.net on 17.8.2016..
 */
public interface ListContentFragmentView extends MvpLceView<List<SimpleMeasurement>>{
    void showLoading(boolean pullToRefresh);
    void showContent();
    void showError(Throwable e, boolean pullToRefresh);
    void setData(List data);
    void cancelRefreshIcon();
    void showEmptyList();
    void hideEmptyList();
}
