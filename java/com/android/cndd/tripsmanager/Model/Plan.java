package com.android.cndd.tripsmanager.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.Option.Status;

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
public class Plan {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "trip_id")
    private int tripId;

    private PlanCategories name;

    private Status status;

    public Plan(int tripId, PlanCategories name) {
        this.tripId = tripId;
        this.name = name;
    }

    public int getId() {
        return id;
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

    public PlanCategories getName() {
        return name;
    }

    public void setName(PlanCategories name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
