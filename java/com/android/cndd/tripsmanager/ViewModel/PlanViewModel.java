package com.android.cndd.tripsmanager.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.cndd.tripsmanager.EntityDao.DBContext;
import com.android.cndd.tripsmanager.EntityDao.PlanDao;
import com.android.cndd.tripsmanager.Model.IPlanViewer;
import com.android.cndd.tripsmanager.Model.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class PlanViewModel extends ViewModel{
    private static PlanLiveData planLiveData;
    private DBContext dbContext;
    private PlanDao planDao;
    private MutableLiveData<IPlanViewer> selected;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        dbContext = DBContext.getDBContext(application);
        planDao = dbContext.planDao();
    }

    public void select(IPlanViewer viewer){
        MutableLiveData<IPlanViewer> iPlanViewerMutableLiveData = new MutableLiveData<>();
        iPlanViewerMutableLiveData.setValue(viewer);
        selected = iPlanViewerMutableLiveData;
    }

    public MutableLiveData<IPlanViewer> getSelected(){
        return selected;
    }

    @Override
    public void Insert(Object obj) {
        planDao.insert((Plan)obj);
    }

    @Override
    public void Delete(Object obj) {
        planDao.delete((Plan)obj);
    }

    @Override
    public void Update(Object obj) {
        planDao.update((Plan)obj);
    }

    public PlanLiveData initPlanLiveData(int tripId){
        if(planLiveData == null){
            planLiveData = new PlanLiveData(dbContext);
        }
        planLiveData.loadData(tripId);
        return planLiveData;
    }

    public Plan getPlanById(int id){
        return planDao.getPlanById(id);
    }

    public Plan getLastItem(){
        return planDao.getLastItem();
    }

    public Object getObjFromPlan(Plan plan){
        Object obj = null;
        switch (plan.getName()){
            case Meeting:
                obj = dbContext.meetingDao().getMeeting(plan.getId());
                break;
            case Flight:

                break;
        }
        return obj;
    }

    @Override
    public UpdateUIListener getUpdateUIListener() {
        return planLiveData;
    }

    public class PlanLiveData extends LiveData<List<IPlanViewer>>
        implements UpdateUIListener {
        private static final String TAG = "PlanLiveData";
        private List<IPlanViewer> planViewers = new ArrayList<>();
        private PlanDao dao;
        private boolean isReady = false;
        private int tripId = -1;

        PlanLiveData(DBContext context){
            dao = context.planDao();
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
        public void loadData(int tripId){
            if(this.tripId == tripId) return;
            planViewers.clear();
            new AsyncTask<Void, IPlanViewer, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for(int plan_id : dao.getAllIds(tripId)){
                        Plan plan = dao.getPlanById(plan_id);
                        publishProgress((IPlanViewer) getObjFromPlan(plan));
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(IPlanViewer... values) {
                    super.onProgressUpdate(values);
                    if(values[0] == null) return;
                    planViewers.add(values[0]);
                    setValue(planViewers);
                }
            }.execute();
        }

        @Override
        public void onInsert(Query query, QueryArg args) {
            try {
                Plan plan = planDao.getLastItem();
                planViewers.add((IPlanViewer) getObjFromPlan(plan));
            }
            catch (Exception ex){
                return;
            }
            setValue(planViewers);
        }

        @Override
        public void onDelete(Query query, QueryArg queryArgs) {
            Log.e(TAG, "onDelete: plan");
            try {
                planViewers.remove((IPlanViewer) queryArgs.args[0]);
            }catch (Exception ex){
                return;
            }
            setValue(planViewers);
        }

        @Override
        public void onUpdate(Query query, QueryArg queryArgs) {
            try {
                for(int i = 0; i < planViewers.size(); i++){
                    if(planViewers.get(i).getId() == (int)queryArgs.args[0]){
                        IPlanViewer viewer = (IPlanViewer) query.getObj();
                        planViewers.set(i, viewer);
                        selected.setValue(viewer);
                        break;
                    }
                }
            }catch (Exception ex){
                Log.e(TAG, "Update: ", ex);
                return;
            }
            setValue(planViewers);
        }

        @Override
        public boolean isReadyForUpdate() {
            return isReady;
        }
    }
}
