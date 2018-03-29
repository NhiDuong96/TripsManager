package com.android.cndd.tripsmanager.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.Viewer.PlanViewer;

import java.util.List;

/**
 * Created by Minh Nhi on 3/8/2018.
 */

public class PlansAdapter extends ArrayAdapter<PlanViewer> {
    private Context context;
    private int layoutId;
    private List<PlanViewer> list;
    private View.OnTouchListener mTouchListener;

    public PlansAdapter(@NonNull Context context, int resource, @NonNull List<PlanViewer> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutId = resource;
        this.list = objects;
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        mTouchListener = listener;
    }

    @Override
    public long getItemId(int position) {
        PlanViewer item = getItem(position);
        return item.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(layoutId,parent,false);
            holder.date = view.findViewById(R.id.date);
            holder.time = view.findViewById(R.id.time);
            holder.date = view.findViewById(R.id.date);
            holder.desciption = view.findViewById(R.id.description);
            holder.infomation = view.findViewById(R.id.information);
            holder.icon = view.findViewById(R.id.icon);
            view.setOnTouchListener(mTouchListener);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        PlanViewer viewer = list.get(position);
        holder.date.setText(viewer.getDate());
        holder.time.setText(viewer.getTime());
        holder.icon.setImageResource(viewer.getIconUrl());
        holder.desciption.setText(viewer.getDesciption());
        holder.infomation.setText(viewer.getInformation());
        return view;
    }

    static class ViewHolder{
        TextView date,time,desciption,infomation;
        ImageView icon;
    }
}
