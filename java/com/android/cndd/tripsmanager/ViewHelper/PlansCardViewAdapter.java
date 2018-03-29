package com.android.cndd.tripsmanager.ViewHelper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cndd.tripsmanager.Model.IPlanViewer;
import com.android.cndd.tripsmanager.R;

import java.util.List;

/**
 * Created by Minh Nhi on 3/22/2018.
 */

public class PlansCardViewAdapter extends RecyclerView.Adapter<PlansCardViewAdapter.ViewHolder>
    implements IAdapterHelper<IPlanViewer> {

    private int resLayoutId;
    private List<IPlanViewer> list;
    private View.OnTouchListener mTouchListener;

    public PlansCardViewAdapter(int resLayoutId, List<IPlanViewer> list){
        this.resLayoutId = resLayoutId;
        this.list = list;
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        mTouchListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resLayoutId, parent,false);
        v.setOnTouchListener(mTouchListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IPlanViewer viewer = list.get(position);
        holder.date.setText(viewer.getDate());
        holder.time.setText(viewer.getTime());
        holder.icon.setImageResource(viewer.getIconUrlId());
        holder.desciption.setText(viewer.getDescription());
        holder.infomation.setText(viewer.getInformation());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public IPlanViewer getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getIdItem(int position) {
        return list.get(position).getId();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date,time,desciption,infomation;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
            date = view.findViewById(R.id.date);
            desciption = view.findViewById(R.id.description);
            infomation = view.findViewById(R.id.information);
            icon = view.findViewById(R.id.icon);
        }
    }
}
