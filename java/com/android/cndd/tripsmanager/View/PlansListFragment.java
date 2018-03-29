package com.android.cndd.tripsmanager.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.BackgroundContainer;
import com.android.cndd.tripsmanager.ViewHelper.OnActionTouchItemListener;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationStartListener;
import com.android.cndd.tripsmanager.ViewHelper.PlansCardViewAdapter;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;
import com.android.cndd.tripsmanager.Viewer.PlanViewer;
import com.android.cndd.tripsmanager.Viewer.TripViewer;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class PlansListFragment extends Fragment {
    public static final String TAG = "PlansListFragment";

    public static final int REQUEST = 1001;

    private Context context;

    private RecyclerView listView;

    private PlansCardViewAdapter adapter;

    private TripViewer mTripViewer;

    private BackgroundContainer backgroundContainer;

    private PlanViewModel planViewModel;

    private PlanViewModel.PlanLiveData planLiveData;

    private OnFragmentAnimationStartListener startAnimation;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        planViewModel = ViewModelProviders.of(getActivity()).get(PlanViewModel.class);
        planLiveData = planViewModel.getPlanLiveData(mTripViewer.getId());
        planLiveData.observe(this,(listPlans) -> {
            if(adapter == null){
                context = getContext();
                if(context == null || listPlans == null) return;
                adapter = new PlansCardViewAdapter(R.layout.plans_item_layout,listPlans);
                listView.setAdapter(adapter);
                OnActionTouchItemListener listener =
                        new OnActionTouchItemListener<PlanViewer>(context, listView, adapter,backgroundContainer) {
                            @Override
                            protected void onRemoveItem(PlanViewer viewer) {
                                removeItem(viewer);
                            }

                            @Override
                            protected void onTouchItem(PlanViewer viewer) {
                                planViewModel.select(viewer);
                                startAnimation.onAnimationStart();
                            }
                        };
                adapter.setOnTouchListener(listener);
            }
            else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void removeItem(PlanViewer viewer){
        Plan plan = planViewModel.getPlanById(viewer.getId());
        Query<Plan> query = new QueryFactory.DeleteOperation<>(planViewModel, plan);
        query.setUpdateUIListener(planLiveData);
        query.setArguments(viewer);
        QueryTransaction.getTransaction().execOnMainThread(query);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plans_fragment_layout,container,false);
        listView = v.findViewById(R.id.list_item);
        listView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        if(bundle != null)
            mTripViewer = (TripViewer) bundle.getSerializable(MainActivity.TRIP);
        backgroundContainer = v.findViewById(R.id.listViewBackground);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(),PlanCategoryActivity.class);
            startActivityForResult(intent,REQUEST);
        });
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST && resultCode == RESULT_OK){
            String name = data.getStringExtra(PlanCategoryActivity.TAG);
            Intent intent = null;
            switch (PlanCategories.valueOf(name)){
                case Meeting:
                    //intent = new Intent(getContext(),MeetingCreateActivity.class);
                    break;
                case Restaurant:
                    break;
            }
            intent = new Intent(getContext(),MeetingCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("action", 0);
            bundle.putInt("tripId", mTripViewer.getId());
            intent.putExtra("plan", bundle);
            startActivity(intent);
        }
    }

    public void setClickListener(OnFragmentAnimationStartListener startAnimation) {
        this.startAnimation = startAnimation;
    }
}
