package es.esy.stresomjer.stresomjer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.esy.stresomjer.stresomjer.R;
import es.esy.stresomjer.stresomjer.model.SimpleMeasurement;

/**
 * Created by dotocan@croz.net on 17.8.2016..
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<SimpleMeasurement> simpleMeasurementList;
    private Context context;

    public ListAdapter(ArrayList<SimpleMeasurement> simpleMeasurementList, Context context) {
        this.simpleMeasurementList = simpleMeasurementList;
        this.context = context;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        String noActivity = context.getString(R.string.none);
        String lightActivity = context.getString(R.string.light);
        String moderateActivity = context.getString(R.string.moderate);
        String heavyActivity = context.getString(R.string.heavy);

        int receivedBpm = simpleMeasurementList.get(position).getBpm_value();
        String bpmText = String.valueOf(receivedBpm) + " " + context.getString(R.string.bpm);
        String receivedActivity = simpleMeasurementList.get(position).getActivity();
        String receivedDateMeasured = simpleMeasurementList.get(position).getDate_measured();
        String receivedTimeMeasured = simpleMeasurementList.get(position).getTime_measured();
        String dateTimeText = receivedDateMeasured + " at " + receivedTimeMeasured;

        if (receivedActivity.equals(noActivity)) {
            Picasso.with(context)
                    .load(R.drawable.ic_hotel_grey_48dp)
                    .placeholder(R.drawable.ic_hotel_grey_48dp)
                    .into(holder.imgRowMeasurement);
        } else if (receivedActivity.equals(lightActivity)) {
            Picasso.with(context)
                    .load(R.drawable.ic_directions_walk_grey_48dp)
                    .placeholder(R.drawable.ic_directions_walk_grey_48dp)
                    .into(holder.imgRowMeasurement);
        } else if (receivedActivity.equals(moderateActivity)) {
            Picasso.with(context)
                    .load(R.drawable.ic_directions_run_grey_48dp)
                    .placeholder(R.drawable.ic_directions_run_grey_48dp)
                    .into(holder.imgRowMeasurement);
        } else if (receivedActivity.equals(heavyActivity)) {
            Picasso.with(context)
                    .load(R.drawable.ic_directions_bike_grey_48dp)
                    .placeholder(R.drawable.ic_directions_bike_grey_48dp)
                    .into(holder.imgRowMeasurement);
        }

        holder.tvRowBpm.setText(bpmText);
        holder.tvRowDatetime.setText("...");
        holder.tvRowDatetime.setText(dateTimeText);
    }

    @Override
    public int getItemCount() {
        return simpleMeasurementList.size();
    }

    public List<SimpleMeasurement> getData() {
        return simpleMeasurementList;
    }

    public void setData(List data) {
        simpleMeasurementList = data;
    }

    // Custom view holder class
    public class ListViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgRowMeasurement;
        private TextView tvRowBpm, tvRowDatetime;

        public ListViewHolder(View itemView) {
            super(itemView);

            imgRowMeasurement = (ImageView) itemView.findViewById(R.id.img_row_measurement);
            tvRowBpm = (TextView) itemView.findViewById(R.id.tv_row_bpm);
            tvRowDatetime = (TextView) itemView.findViewById(R.id.tv_row_datetime);
        }
    }
}
