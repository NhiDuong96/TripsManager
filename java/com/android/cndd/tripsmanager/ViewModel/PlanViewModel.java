package com.android.cndd.tripsmanager.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.android.cndd.tripsmanager.Model.DBContext;
import com.android.cndd.tripsmanager.Model.EntityDao.PlanDao;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.Model.PlanCategory.IPlanViewer;
import com.android.cndd.tripsmanager.Viewer.PlanViewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class PlanViewModel extends ViewModel<Plan>{
    private PlanLiveData planLiveData;
    private DBContext dbContext;
    private PlanDao planDao;
    private MutableLiveData<IPlanViewer> selected;

    public PlanViewModel(@NonNull Application application) {
        super(application);
        dbContext = DBContext.getDBContext(application);
        planDao = dbContext.planDao();
    }

    public void select(PlanViewer viewer){
        MutableLiveData<IPlanViewer> iPlanViewerMutableLiveData = new MutableLiveData<>();
        Plan plan = getPlanById(viewer.getId());
        iPlanViewerMutableLiveData.setValue((IPlanViewer) getObjFromPlan(plan));
        selected = iPlanViewerMutableLiveData;
    }

    public IPlanViewer getSelected(){
        return selected.getValue();
    }

    @Override
    public void Insert(Plan obj) {
        planDao.insert(obj);
    }

    @Override
    public void Delete(Plan obj) {
        planDao.delete(obj);
    }

    @Override
    public void Update(Plan obj) {
        planDao.update(obj);
    }

    public PlanLiveData getPlanLiveData(int tripId){
        if(planLiveData == null){
            planLiveData = new PlanLiveData(dbContext,tripId);
        }
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


    public class PlanLiveData extends LiveData<List<PlanViewer>>
        implements IQueryUIObserver<Plan>{
        private static final String TAG = "PlanLiveData";
        private List<PlanViewer> planViewers = new ArrayList<>();
        private DBContext context;
        private PlanDao dao;
        private int tripId;

        PlanLiveData(DBContext context, int tripId){
            this.context = context;
            this.tripId = tripId;
            dao = context.planDao();
            loadData();
        }

        @Override
        protected void onActive() {
            super.onActive();
            QueryTransaction.getTransaction().requestForUpdate(this);
        }


        @SuppressLint("StaticFieldLeak")
        public void loadData(){
            new AsyncTask<Void, PlanViewer, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    for(int plan_id : dao.getAllIds(tripId)){
                        Plan plan = dao.getPlanById(plan_id);
                        publishProgress(PlanViewer.convertFromObj(plan, getObjFromPlan(plan)));
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(PlanViewer... values) {
                    super.onProgressUpdate(values);
                    if(values[0] == null) return;
                    planViewers.add(values[0]);
                    setValue(planViewers);
                }
            }.execute();
        }

        @Override
        public void onInsert(Query<Plan> query, Query.QueryArg args) {
            try {
                Plan plan = planDao.getLastItem();
                planViewers.add(PlanViewer.convertFromObj(plan, getObjFromPlan(plan)));
            }
            catch (Exception ex){
                return;
            }
            setValue(planViewers);
        }

        @Override
        public void onDelete(Query<Plan> query, Query.QueryArg queryArgs) {
            try {
                PlanViewer viewer = (PlanViewer) queryArgs.args[0];
                planViewers.remove(viewer);
            }catch (Exception ex){
                return;
            }
            setValue(planViewers);
        }

        @Override
        public void onUpdate(Query<Plan> query, Query.QueryArg args) {

        }

        @Override
        public int getUpdateUICode() {
            return 1001;
        }
    }
}
