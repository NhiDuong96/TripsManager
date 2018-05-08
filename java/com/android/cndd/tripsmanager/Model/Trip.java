package com.android.cndd.tripsmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.view.ITripViewer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

@Entity(indices = {@Index(value = {"title"}, unique = true)})
public class Trip implements ITripViewer {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    private String destination;

    private Date startTime;

    private Date endTime;

    private Priority priority;

    private Status status;

    private String description;

    private boolean notify;

    public Trip(){}

    public Trip(@NonNull String title, String destination, Date startTime,
                Date endTime, Priority priority, Status status, String description) {
        this.title = title;
        this.destination = destination;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.status = status;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getIconUrl() {
        return null;
    }


    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getTime() {
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
        return fm.format(startTime) + " - " + fm.format(endTime);
    }

    @Override
    public String getInterval() {
        long numOfDay = (endTime.getTime() - startTime.getTime())/(1000*60*60*24) + 1;
        return String.valueOf(numOfDay) + ((numOfDay <= 1)? " day" : " days");
    }

    public String getDestination() {
        return destination;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
