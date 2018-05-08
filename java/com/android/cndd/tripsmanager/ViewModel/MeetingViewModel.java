package com.android.cndd.tripsmanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.dao.DBContext;
import com.android.cndd.tripsmanager.dao.MeetingDao;
import com.android.cndd.tripsmanager.model.Meeting;

import java.util.List;

/**
 * Created by Minh Nhi on 3/24/2018.
 */

public class MeetingViewModel extends ViewModel {

    private MeetingDao meetingDao;
    private LiveData<List<Meeting>> meetingLiveData;

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

    public LiveData<List<Meeting>> getMeetingLiveData(int trip_id) {
        if(meetingLiveData == null){
            meetingLiveData = meetingDao.getAllMeetings(trip_id);
        }
        return meetingLiveData;
    }

    public Meeting getMeetingById(int id){
        return meetingDao.getMeetingById(id);
    }
}
