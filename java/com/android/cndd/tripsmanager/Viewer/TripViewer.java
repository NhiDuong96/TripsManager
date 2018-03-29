package com.android.cndd.tripsmanager.Viewer;

import com.android.cndd.tripsmanager.Model.Trip;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class TripViewer implements Serializable{
    private final int id;
    private String iconUrl;
    private final String title;
    private String time;
    private String interval;

    static SimpleDateFormat fm = new SimpleDateFormat("E,dd 'thg' MM,yyyy");
    public TripViewer(int id, String title){
        this.id = id;
        this.title = title;
    }
    public TripViewer(int id, String iconUrl, String title, String time, String interval) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.title = title;
        this.time = time;
        this.interval = interval;
    }

    public int getId() {
        return id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getInterval() {
        return interval;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public static TripViewer convertFromObj(Trip trip){
        TripViewer viewer = new TripViewer(trip.getId(),trip.getTitle());
        viewer.iconUrl = "";
        viewer.time = fm.format(trip.getStartTime()) + " - " + fm.format(trip.getEndTime());
        viewer.setInterval("1 day");
        return viewer;
    }
}
