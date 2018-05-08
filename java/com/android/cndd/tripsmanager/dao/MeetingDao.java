package com.android.cndd.tripsmanager.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.cndd.tripsmanager.model.Meeting;

import java.util.List;

/**
 * Created by Minh Nhi on 3/8/2018.
 */
@Dao
public abstract class MeetingDao implements BaseDao<Meeting> {
    @Query("SELECT * FROM meeting WHERE trip_id = :trip_id")
    public abstract Meeting getMeeting(int trip_id);

    @Query("SELECT * FROM meeting WHERE trip_id = :trip_id")
    public abstract LiveData<List<Meeting>> getAllMeetings(int trip_id);

    @Query("SELECT * FROM Meeting WHERE id = :id")
    public abstract Meeting getMeetingById(int id);
}
