package com.android.cndd.tripsmanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.dao.TripDao;
import com.android.cndd.tripsmanager.model.Trip;

import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class TripViewModel extends ViewModel {
    private static final String TAG = "TripViewModel";
    private TripDao tripDao;
    private LiveData<List<Trip>> listLiveData;

    public TripViewModel(@NonNull Application application) {
        super(application);
        tripDao = getDBContext().tripDao();
    }

    public LiveData<List<Trip>> getTripLiveData(){
        if(listLiveData == null){
            listLiveData = tripDao.getAllTrips();
        }
        return listLiveData;
    }

    @Override
    public void Insert(Object obj) {
        tripDao.insert((Trip)obj);
    }

    @Override
    public void Delete(Object obj) {
        tripDao.delete((Trip)obj);
    }

    @Override
    public void Update(Object obj) {
        tripDao.update((Trip)obj);
    }


    public Trip getTripFromId(int id){
        return tripDao.getTripById(id);
    }

}
