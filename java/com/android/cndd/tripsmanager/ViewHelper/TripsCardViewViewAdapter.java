package com.android.cndd.tripsmanager.ViewHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public class TripsCardViewViewAdapter extends RecyclerView.Adapter<TripsCardViewViewAdapter.ViewHolder>
    implements IViewAdapter<ITripViewer> {

    private int resLayoutId;
    private List<ITripViewer> list;
    private View.OnTouchListener mTouchListener;

    public TripsCardViewViewAdapter(int resLayoutId, List<ITripViewer> list){
        this.resLayoutId = resLayoutId;
        this.list = list;
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        mTouchListener = listener;
    }

    @Override
    public ITripViewer getItem(int position){
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
        ITripViewer viewer = list.get(position);
        if(viewer.getIconUrl() != null){
            holder.circleImageView.setImageBitmap(null);
        }

        holder.title.setText(viewer.getTitle());
        holder.time.setText(viewer.getTime());
        holder.interval.setText(viewer.getInterval());
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
