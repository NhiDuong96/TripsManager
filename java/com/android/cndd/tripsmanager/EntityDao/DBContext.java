package com.android.cndd.tripsmanager.EntityDao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;
import com.android.cndd.tripsmanager.Model.Trip;


/**
 * Created by Minh Nhi on 1/28/2018.
 */
@Database(entities = {Trip.class, Plan.class, Meeting.class}, version = 1)
@TypeConverters({
        Converters.DateConverters.class,
        Converters.StatusConverters.class,
        Converters.PriorityConverters.class,
        Converters.PlanCategoryConverters.class
})
public abstract class DBContext extends RoomDatabase {
    public abstract TripDao tripDao();
    public abstract PlanDao planDao();
    public abstract MeetingDao meetingDao();


    private static DBContext dbContext;
    public static DBContext getDBContext(Context context){
        if(dbContext == null){
            dbContext = Room.databaseBuilder(context,DBContext.class, "database")
                    .allowMainThreadQueries().build();
        }
        return dbContext;
    }
}
