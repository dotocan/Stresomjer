package es.esy.stresomjer.stresomjer.view.fragment;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.ArrayList;
import java.util.List;

import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.adapter.ListAdapter;
import es.esy.stresomjer.stresomjer.helper.Constants;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;
import es.esy.stresomjer.stresomjer.presenter.ListContentFragmentPresenter;
import es.esy.stresomjer.stresomjer.view.interfaces.ListContentFragmentView;

public class ListContentFragment extends MvpLceViewStateFragment<SwipeRefreshLayout,
        List<SimpleMeasurement>, ListContentFragmentView, ListContentFragmentPresenter>
        implements ListContentFragmentView, SwipeRefreshLayout.OnRefreshListener {

    private TextView tvEmptyList;
    private RecyclerView recyclerView;

    private ListAdapter listAdapter;
    private ArrayList<SimpleMeasurement> simpleMeasurementList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private String user_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public ListContentFragmentPresenter createPresenter() {
        return new ListContentFragmentPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Getting SharedPreferences data
        sharedPreferences = getActivity().getSharedPreferences("Login", 0);
        user_id = sharedPreferences.getString(Constants.UNIQUE_ID, "");

        View rootView = inflater.inflate(R.layout.fragment_list_content, container, false);
        initViews(rootView);

        return rootView;
    }

    public void initViews(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_measurements);
        tvEmptyList = (TextView) rootView.findViewById(R.id.tv_empty_list);
        hideEmptyList();

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.loadingView);

        // Changing the progress bar color
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Creating adapter for RecyclerView
        listAdapter = new ListAdapter(simpleMeasurementList, getActivity().getApplicationContext());

        contentView.setOnRefreshListener(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(listAdapter);
    }

    @Override
    public void setData(List data) {
        listAdapter.setData(data);
        listAdapter.notifyDataSetChanged();
    }

    public void cancelRefreshIcon(){
        contentView.setRefreshing(false);
    }

    @Override
    public void showEmptyList() {
        tvEmptyList.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyList() {
        tvEmptyList.setVisibility(View.GONE);
    }

    @Override
    public LceViewState<List<SimpleMeasurement>, ListContentFragmentView> createViewState() {
        return new RetainingLceViewState<>();
    }

    @Override
    public List<SimpleMeasurement> getData() {
        return listAdapter == null ? new ArrayList<SimpleMeasurement>() : listAdapter.getData();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadData(pullToRefresh, user_id);
    }

    @Override
    public void onRefresh() {
        this.loadData(true);
    }

}
