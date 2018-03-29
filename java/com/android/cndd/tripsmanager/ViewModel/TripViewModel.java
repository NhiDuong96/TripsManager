package com.android.cndd.tripsmanager.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.cndd.tripsmanager.Model.EntityDao.TripDao;
import com.android.cndd.tripsmanager.Model.Trip;
import com.android.cndd.tripsmanager.Viewer.TripViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class TripViewModel extends ViewModel<Trip> {
    private static final String TAG = "TripViewModel";
    private TripDao tripDao;
    private TripLiveData tripLiveData;

    public TripViewModel(@NonNull Application application) {
        super(application);
        tripDao = getDBContext().tripDao();
        tripLiveData = new TripLiveData(tripDao);
    }

    @Override
    public void Insert(Trip obj) {
        tripDao.insert(obj);
    }

    @Override
    public void Delete(Trip obj) {
        tripDao.delete(obj);
    }

    @Override
    public void Update(Trip obj) {
        tripDao.update(obj);
    }

    public TripLiveData getTripLiveData(){
        return tripLiveData;
    }

    public Trip getTripFromId(int id){
        return tripDao.getTripById(id);
    }


    //class
    public class TripLiveData extends LiveData<List<TripViewer>>
            implements IQueryUIObserver<Trip> {
        private List<TripViewer> tripViewers = new ArrayList<>();
        private TripDao tripDao;

        TripLiveData(TripDao tripDao){
            this.tripDao = tripDao;
            loadData();
        }


        @Override
        protected void onActive() {
            super.onActive();
            QueryTransaction.getTransaction().requestForUpdate(this);
        }


        @SuppressLint("StaticFieldLeak")
        public void loadData(){
            tripViewers.clear();
            new AsyncTask<Void, TripViewer, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for(Integer id: tripDao.getAllIds()){
                        //SystemClock.sleep(1000);
                        publishProgress(TripViewer.convertFromObj(tripDao.getTripById(id)));
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(TripViewer... values) {
                    super.onProgressUpdate(values);
                    if(values[0] == null) return;
                    tripViewers.add(values[0]);
                    setValue(tripViewers);
                }
            }.execute();
        }

        @Override
        public void onInsert(Query<Trip> query, Query.QueryArg args) {
            try {
                Trip trip = tripDao.getLastItem();
                TripViewer viewer = TripViewer.convertFromObj(trip);
                tripViewers.add(viewer);
            }catch (Exception ex){
                return;
            }
            Log.e(TAG, "onInsert: ");
            setValue(tripViewers);
        }

        @Override
        public void onDelete(Query<Trip> query, Query.QueryArg queryArgs) {
            try {
                TripViewer viewer = (TripViewer) queryArgs.args[0];
                tripViewers.remove(viewer);
            }catch (Exception ex){
                Log.e(TAG, "onDelete: ", ex);
                return;
            }
            setValue(tripViewers);
        }

        @Override
        public void onUpdate(Query<Trip> query, Query.QueryArg queryArgs) {
            try {
                int position = (int) queryArgs.args[0];
                tripViewers.set(position, TripViewer.convertFromObj(query.getObj()));
            }catch (Exception ex){
                Log.e(TAG, "Update: ", ex);
                return;
            }
            setValue(tripViewers);
        }

        @Override
        public int getUpdateUICode() {
            return 1000;
        }
    }
}
