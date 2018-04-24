package com.android.cndd.tripsmanager.EntityDao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

/**
 * Created by Minh Nhi on 3/1/2018.
 */

public interface BaseDao<T> {

    @Insert
    void insert(T obj);

    @Update
    void update(T obj);

    @Delete
    void delete(T obj);
}
