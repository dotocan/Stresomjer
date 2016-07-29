package es.esy.stresomjer.stresomjer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;

/**
 * Created by domin on 7/29/2016.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder>{

    private ArrayList<SimpleMeasurement> simpleMeasurementList;

    public MainAdapter(ArrayList<SimpleMeasurement> simpleMeasurementList) {
        this.simpleMeasurementList = simpleMeasurementList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.tvBpmValue.setText(String.valueOf(simpleMeasurementList.get(position).getBpm_value()));
        holder.tvDate.setText("...");
        holder.tvTime.setText("...");
        holder.tvActivity.setText(simpleMeasurementList.get(position).getActivity());
    }

    @Override
    public int getItemCount() {
        return simpleMeasurementList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBpmValue,tvDate, tvTime, tvActivity;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tvBpmValue = (TextView) itemView.findViewById(R.id.tv_bpm_value);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvActivity = (TextView) itemView.findViewById(R.id.tv_activity);
        }
    }
}
