package com.android.cndd.tripsmanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.android.cndd.tripsmanager.model.PlanCategory;
import com.android.cndd.tripsmanager.view.IPlanViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Minh Nhi on 3/4/2018.
 */

public class PlanViewModel extends ViewModel {
    private MutableLiveData<IPlanViewer> selected;

    private PlanLiveData planLiveData;

    private MeetingViewModel meetingViewModel;


    public PlanViewModel(@NonNull Application application) {
        super(application);
    }

    public void initialize(FragmentActivity activity){
        meetingViewModel = ViewModelProviders.of(activity).get(MeetingViewModel.class);
        //...
    }

    public ViewModel getViewModel(IPlanViewer viewer){
        switch (viewer.getPlanCategoryName()){
            case Meeting:
                return meetingViewModel;
        }
        return null;
    }

    public void select(IPlanViewer viewer){
        MutableLiveData<IPlanViewer> iPlanViewerMutableLiveData = new MutableLiveData<>();
        iPlanViewerMutableLiveData.setValue(viewer);
        selected = iPlanViewerMutableLiveData;
    }

    public PlanLiveData getPlanLiveData(int trip_id){
        if(planLiveData == null){
            planLiveData = new PlanLiveData(trip_id);
        }
        return planLiveData;
    }

    public MutableLiveData<IPlanViewer> getSelected(){
        return selected;
    }

    public Object getPlan(IPlanViewer viewer){
        switch (viewer.getPlanCategoryName()){
            case Meeting:
                return meetingViewModel.getMeetingById(viewer.getId());

        }
        return null;
    }

    @Override
    public void Insert(Object obj) {
        getViewModel((IPlanViewer) obj).Insert(obj);
    }

    @Override
    public void Delete(Object obj) {
        getViewModel((IPlanViewer) obj).Delete(obj);
    }

    @Override
    public void Update(Object obj) {
        getViewModel((IPlanViewer) obj).Update(obj);
    }


    public class PlanLiveData extends LiveData<List<IPlanViewer>>{
        private List<IPlanViewer> plans;

        private HashMap<PlanCategory, List<IPlanViewer>> map;

        public PlanLiveData(int trip_id){
            map = new HashMap<>();
            plans = new ArrayList<>();

            meetingViewModel.getMeetingLiveData(trip_id).observeForever(list -> {
                if(list == null) return;
                if(map.containsKey(PlanCategory.Meeting)){
                    map.remove(PlanCategory.Meeting);
                }
                map.put(PlanCategory.Meeting, new ArrayList<>(list));
                updateUI();
            });

            //...
        }

        private void updateUI(){
            plans.clear();
            for(List<IPlanViewer> list : map.values()){
                plans.addAll(list);
            }
            setValue(plans);
        }

    }
}
