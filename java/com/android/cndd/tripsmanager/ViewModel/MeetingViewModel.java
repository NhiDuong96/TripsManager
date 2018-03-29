package com.android.cndd.tripsmanager.ViewModel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.Model.DBContext;
import com.android.cndd.tripsmanager.Model.EntityDao.MeetingDao;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;

/**
 * Created by Minh Nhi on 3/24/2018.
 */

public class MeetingViewModel extends ViewModel<Meeting> {

    private MeetingDao meetingDao;

    public MeetingViewModel(@NonNull Application application) {
        super(application);
        meetingDao = DBContext.getDBContext(application).meetingDao();
    }

    @Override
    public void Insert(Meeting obj) {
        meetingDao.insert(obj);
    }

    @Override
    public void Delete(Meeting obj) {
        meetingDao.delete(obj);
    }

    @Override
    public void Update(Meeting obj) {
        meetingDao.update(obj);
    }

    public Meeting getMeetingFromPlanId(int planId){
        return meetingDao.getMeeting(planId);
    }
}
