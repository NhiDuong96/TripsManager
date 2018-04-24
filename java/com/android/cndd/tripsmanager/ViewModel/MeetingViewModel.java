package com.android.cndd.tripsmanager.ViewModel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.EntityDao.DBContext;
import com.android.cndd.tripsmanager.EntityDao.MeetingDao;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;

/**
 * Created by Minh Nhi on 3/24/2018.
 */

public class MeetingViewModel extends ViewModel {

    private MeetingDao meetingDao;

    public MeetingViewModel(@NonNull Application application) {
        super(application);
        meetingDao = DBContext.getDBContext(application).meetingDao();
    }

    @Override
    public void Insert(Object obj) {
        meetingDao.insert((Meeting)obj);
    }

    @Override
    public void Delete(Object obj) {
        meetingDao.delete((Meeting)obj);
    }

    @Override
    public void Update(Object obj) {
        meetingDao.update((Meeting)obj);
    }

    public Meeting getMeetingFromPlanId(int planId){
        return meetingDao.getMeeting(planId);
    }
}
