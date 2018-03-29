package com.android.cndd.tripsmanager.Model.EntityDao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.android.cndd.tripsmanager.Model.Plan;

import java.util.List;

/**
 * Created by Minh Nhi on 3/8/2018.
 */
@Dao
public abstract class PlanDao implements BaseDao<Plan> {

    @Query("SELECT id FROM `plan` WHERE trip_id = :trip_id")
    public abstract List<Integer> getAllIds(int trip_id);

    @Query("SELECT * FROM `plan` ORDER BY id DESC LIMIT 1")
    public abstract Plan getLastItem();

    @Query("SELECT * FROM `plan` WHERE id = :plan_id")
    public abstract Plan getPlanById(int plan_id);
}
