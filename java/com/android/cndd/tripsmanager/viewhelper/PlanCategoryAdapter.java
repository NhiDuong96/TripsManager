package com.android.cndd.tripsmanager.viewhelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.view.PlanCategoryActivity;


public class PlanCategoryAdapter extends ArrayAdapter<PlanCategoryActivity.PlanName> {

    private int header;
    private Context context;
    public PlanCategoryAdapter(@NonNull Context context, int resource,
                               @NonNull PlanCategoryActivity.PlanName[] objects, int header) {
        super(context, resource, objects);
        this.header = header;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(header,parent,false);
        if(position == 0){
            TextView textView = v.findViewById(R.id.header);
            textView.setText("Travel");
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_airplan_layer, 0, 0, 0);
            return v;
        }
        else if(position == 4){
            TextView textView = v.findViewById(R.id.header);
            textView.setText("Activity");
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_activity_layer, 0, 0, 0);
            return v;
        }
        else if(position == 8){
            TextView textView = v.findViewById(R.id.header);
            textView.setText("Other");
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_other_layer, 0, 0, 0);
            return v;
        }
        return super.getView(position, convertView, parent);
    }
}
