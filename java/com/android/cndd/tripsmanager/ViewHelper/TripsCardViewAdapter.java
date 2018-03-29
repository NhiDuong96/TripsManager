package com.android.cndd.tripsmanager.ViewHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.Viewer.TripViewer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public class TripsCardViewAdapter extends RecyclerView.Adapter<TripsCardViewAdapter.ViewHolder>
    implements IAdapterHelper<TripViewer> {

    private int resLayoutId;
    private List<TripViewer> list;
    private View.OnTouchListener mTouchListener;

    public TripsCardViewAdapter(int resLayoutId, List<TripViewer> list){
        this.resLayoutId = resLayoutId;
        this.list = list;
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        mTouchListener = listener;
    }

    @Override
    public TripViewer getItem(int position){
        return list.get(position);
    }

    @Override
    public int getIdItem(int position) {
        return list.get(position).getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resLayoutId, parent, false);
        v.setOnTouchListener(mTouchListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.circleImageView.setImageResource(R.drawable.ic_launcher_background);
        holder.title.setText(list.get(position).getTitle());
        holder.time.setText(list.get(position).getTime());
        holder.interval.setText(list.get(position).getInterval());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView title,time,interval;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            interval = itemView.findViewById(R.id.interval);
        }
    }

}
