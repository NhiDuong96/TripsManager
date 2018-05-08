package com.android.cndd.tripsmanager.viewhelper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.cndd.tripsmanager.view.ITripViewer;
import com.android.cndd.tripsmanager.model.Trip;
import com.android.cndd.tripsmanager.R;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public class TripsCardViewAdapter extends RecyclerView.Adapter<TripsCardViewAdapter.ViewHolder>
    implements IRemoveViewAnimation {

    private int resLayoutId;
    private List<Trip> list;
    private View.OnTouchListener mTouchListener;

    public interface OnChangedNotification<T>{
        void onTurnOnNotification(T target);
        void onTurnOffNotification(T target);
    }
    private OnChangedNotification<Trip> changedNotification;

    public TripsCardViewAdapter(int resLayoutId, List<Trip> list){
        this.resLayoutId = resLayoutId;
        this.list = list;
    }

    @Override
    public void setOnTouchListener(View.OnTouchListener listener){
        mTouchListener = listener;
    }

    @Override
    public Object getItem(int position){
        return list.get(position);
    }

    @Override
    public int getIdItem(int position) {
        return list.get(position).getId();
    }

    @Override
    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
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
        ITripViewer viewer = list.get(position);
        if(viewer.getIconUrl() != null){
            holder.circleImageView.setImageBitmap(null);
        }
        holder.trip = list.get(position);
        holder.title.setText(viewer.getTitle());
        holder.time.setText(viewer.getTime());
        holder.interval.setText(viewer.getInterval());
        holder.notify.setChecked(list.get(position).isNotify());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnChangedNotification(OnChangedNotification<Trip> changedNotification){
        this.changedNotification = changedNotification;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView title,time,interval;
        Switch notify;
        Trip trip;

        ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            interval = itemView.findViewById(R.id.interval);
            notify = itemView.findViewById(R.id.notify);
            notify.setOnCheckedChangeListener(checked);
        }

        private CompoundButton.OnCheckedChangeListener checked = (buttonView, isChecked) -> {
            if(changedNotification == null || trip == null) return;
            if(isChecked){
                changedNotification.onTurnOnNotification(trip);
            }else{
                changedNotification.onTurnOffNotification(trip);
            }

        };
    }

}
