package com.android.cndd.tripsmanager.Model.PlanCategory;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.Plan;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by Minh Nhi on 3/4/2018.
 */
@Entity(foreignKeys = {
        @ForeignKey(entity = Plan.class,
        parentColumns = "id",
        childColumns = "plan_id",
        onDelete = CASCADE)
})
public class Meeting implements IPlanViewer{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "plan_id")
    private int planId;

    public Meeting(int planId, String description) {
        this.planId = planId;
        this.description = description;
    }

    private String description;

    private String locationName;

    private String address;

    private Date startTime;

    private Date endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getDescription() {
        return description;
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
    public String toHtmlLayout() {
        return "<html>" +
                "<body>" +
                "<h1 style=\"text-align:center; color: blue\">Meeting</h1>" +
                "<p>Description: " +description+"</p>" +
                "<p>Location Name: " +locationName+"</p>" +
                "<p>Address: " +address+"</p>" +
                "<p>Start Time: " +startTime+"</p>" +
                "<p>End Time: " +endTime+"</p>" +
                "</body>" +
                "</html>";
    }

    @Override
    public int getIConResId() {
        return 0;
    }

    @Override
    public String getCategoryName() {
        return PlanCategories.Meeting.name();
    }
}
