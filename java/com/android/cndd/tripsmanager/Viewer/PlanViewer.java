package com.android.cndd.tripsmanager.Viewer;

import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;
import com.android.cndd.tripsmanager.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Minh Nhi on 3/7/2018.
 */

public class PlanViewer implements Serializable{
    private int id;
    private int iconUrl;
    private String desciption;
    private String time;
    private String date;
    private String information;

    private static SimpleDateFormat fm1 = new SimpleDateFormat("E, 'THG' MM dd");
    private static SimpleDateFormat fm2 = new SimpleDateFormat("hh:mm");
    public PlanViewer(int id){
        this.id = id;
    }
    public PlanViewer(int iconUrl, String desciption, String time, String date, String information) {
        this.iconUrl = iconUrl;
        this.desciption = desciption;
        this.time = time;
        this.date = date;
        this.information = information;
    }

    public int getId() {
        return id;
    }

    public int getIconUrl() {
        return iconUrl;
    }

    public String getDesciption() {
        return desciption;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getInformation() {
        return information;
    }

    public static PlanViewer convertFromObj(Plan plan, Object obj){
        PlanViewer planViewer = new PlanViewer(plan.getId());
        switch (plan.getName()){
            case Meeting:
                if(obj instanceof Meeting) {
                    planViewer.iconUrl = R.drawable.ic_flight_takeoff_black_24dp;
                    planViewer.desciption = ((Meeting) obj).getDescription();
                    planViewer.date = fm1.format(((Meeting) obj).getStartTime());
                    planViewer.time = fm2.format(((Meeting) obj).getStartTime());
                    planViewer.information = ((Meeting) obj).getLocationName();
                }
                break;
            case Activity:

                break;
            case Restaurant:

                break;
        }
        return planViewer;
    }
}
