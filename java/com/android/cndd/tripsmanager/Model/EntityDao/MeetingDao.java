package com.android.cndd.tripsmanager.Model.EntityDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;

/**
 * Created by Minh Nhi on 3/8/2018.
 */
@Dao
public abstract class MeetingDao implements BaseDao<Meeting> {
    @Query("SELECT * FROM meeting WHERE plan_id = :plan_id")
    public abstract Meeting getMeeting(int plan_id);
}
