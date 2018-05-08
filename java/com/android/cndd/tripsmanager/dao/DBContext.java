package com.android.cndd.tripsmanager.dao;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.model.Meeting;
import com.android.cndd.tripsmanager.model.Trip;

import java.util.concurrent.Executors;


/**
 * Created by Minh Nhi on 1/28/2018.
 */
@Database(entities = {Trip.class, Meeting.class}, version = 1, exportSchema = false)
@TypeConverters({
        Converters.DateConverters.class,
        Converters.StatusConverters.class,
        Converters.PriorityConverters.class,
        Converters.PlanCategoryConverters.class
})
public abstract class DBContext extends RoomDatabase {
    public abstract TripDao tripDao();
    public abstract MeetingDao meetingDao();


    private static DBContext dbContext;
    public static DBContext getDBContext(Context context){
        if(dbContext == null){
            dbContext = buildDatabase(context);
        }
        return dbContext;
    }

    private static DBContext buildDatabase(final Context context){
        return Room.databaseBuilder(context,
                DBContext.class, "my-database")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            //seed database here
                            //VD:
                            //getDBContext(context).planCategoryDao().insertAll(PlanCategory.populateData());
                        });
                    }
                })
                .allowMainThreadQueries()
                .build();
    }
}
