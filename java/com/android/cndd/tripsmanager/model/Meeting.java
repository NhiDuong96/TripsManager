package com.android.cndd.tripsmanager.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.android.cndd.tripsmanager.view.IPlanViewer;
import com.android.cndd.tripsmanager.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Minh Nhi on 3/4/2018.
 */
@Entity(foreignKeys = {
        @ForeignKey(entity = Trip.class,
        parentColumns = "id",
        childColumns = "trip_id",
        onDelete = CASCADE)
})
public class Meeting implements IPlanViewer {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "trip_id")
    private int tripId;

    private PlanCategory planCategoryName;

    private String description;

    private String locationName;

    private String address;

    private double latitude;

    private double longitude;

    private Date startTime;

    private Date endTime;

    private Priority priority;

    private Status status;


    public Meeting(){}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getIconUrlId() {
        return R.drawable.ic_flight_takeoff_black_24dp;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTime() {
        SimpleDateFormat fm = new SimpleDateFormat("hh:mm");
        return fm.format(startTime);
    }

    @Override
    public String getDate() {
        SimpleDateFormat fm = new SimpleDateFormat("E,dd 'Thg' MM");
        return fm.format(startTime);
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getInformation() {
        return locationName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }


    public void setDescription(String destination) {
        this.description = destination;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public PlanCategory getPlanCategoryName() {
        return planCategoryName;
    }

    public void setPlanCategoryName(PlanCategory planCategoryName) {
        this.planCategoryName = planCategoryName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toHtmlLayout() {
        return "<html>" +
                "<body style=\"background-color: #26A69A;\">" +
                "<h1 style=\"text-align:center; color: blue\">Meeting</h1>" +
                "<p>Description: " +description+"</p>" +
                "<p>Location Name: " +locationName+"</p>" +
                "<p>Address: " +address+"</p>" +
                "<p>Start Time: " +startTime+"</p>" +
                "<p>End Time: " +endTime+"</p>" +
                "</body>" +
                "</html>";
    }
}
