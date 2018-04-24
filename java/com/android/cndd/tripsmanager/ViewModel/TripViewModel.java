package com.android.cndd.tripsmanager.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.cndd.tripsmanager.EntityDao.TripDao;
import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.Model.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class TripViewModel extends ViewModel {
    private static final String TAG = "TripViewModel";
    private TripDao tripDao;
    private static TripLiveData tripLiveData;

    public TripViewModel(@NonNull Application application) {
        super(application);
        tripDao = getDBContext().tripDao();
        if(tripLiveData == null)
            tripLiveData = new TripLiveData(tripDao);
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

    public TripLiveData getTripLiveData(){
        return tripLiveData;
    }

    public Trip getTripFromId(int id){
        return tripDao.getTripById(id);
    }

    @Override
    public UpdateUIListener getUpdateUIListener() {
        return tripLiveData;
    }

    //class
    public class TripLiveData extends LiveData<List<ITripViewer>>
            implements UpdateUIListener {
        private List<ITripViewer> tripViewers = new ArrayList<>();
        private TripDao tripDao;
        private boolean isReady = false;

        private TripLiveData(TripDao tripDao){
            this.tripDao = tripDao;
            loadData();
        }

        @Override
        protected void onActive() {
            super.onActive();
            isReady = true;
            QueryTransaction.getTransaction().requestForUpdate(this);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            isReady = false;
        }

        @SuppressLint("StaticFieldLeak")
        public void loadData(){
            tripViewers.clear();
            new AsyncTask<Void, ITripViewer, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for(Integer id: tripDao.getAllIds()){
                        //SystemClock.sleep(1000);
                        publishProgress(tripDao.getTripById(id));
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(ITripViewer... values) {
                    super.onProgressUpdate(values);
                    if(values[0] == null) return;
                    tripViewers.add(values[0]);
                    setValue(tripViewers);
                }
            }.execute();
        }

        @Override
        public void onInsert(Query query, QueryArg args) {
            tripViewers.add(tripDao.getLastItem());
            setValue(tripViewers);
        }

        @Override
        public void onDelete(Query query, QueryArg queryArgs) {
            tripViewers.remove((ITripViewer)queryArgs.args[0]);
            setValue(tripViewers);
        }

        @Override
        public void onUpdate(Query query, QueryArg queryArgs) {
            for(int i = 0; i < tripViewers.size() ;i++){
                if(tripViewers.get(i).getId() == (int)queryArgs.args[0]){
                    tripViewers.set(i, (ITripViewer) query.getObj());
                }
            }
            setValue(tripViewers);
        }

        @Override
        public boolean isReadyForUpdate() {
            return isReady;
        }
    }
}
