package com.android.cndd.tripsmanager.Model.EntityDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.cndd.tripsmanager.Model.Trip;

import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

@Dao
public abstract class TripDao implements BaseDao<Trip> {

    @Query("SELECT id FROM trip")
    public abstract List<Integer> getAllIds();

    @Query("SELECT * FROM trip WHERE title = :title")
    public abstract Trip getTripFromTitle(String title);

    @Query("SELECT * FROM trip WHERE id = :mId")
    public abstract Trip getTripById(int mId);

    @Query("SELECT * FROM trip ORDER BY id DESC LIMIT 1")
    public abstract Trip getLastItem();

}
