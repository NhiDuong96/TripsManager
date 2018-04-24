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

import com.android.cndd.tripsmanager.Model.IPlanViewer;
import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.Model.PlanCategories;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.BackgroundContainer;
import com.android.cndd.tripsmanager.ViewHelper.IViewAdapter;
import com.android.cndd.tripsmanager.ViewHelper.OnInteractItemListener;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationStartListener;
import com.android.cndd.tripsmanager.ViewHelper.PlansCardViewViewAdapter;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class PlansListFragment extends Fragment implements OnInteractItemListener.OnInteractItemAdapter<IPlanViewer> {
    public static final String TAG = "PlansListFragment";

    public static final int REQUEST = 1001;

    private Context context;

    private RecyclerView listView;

    private PlansCardViewViewAdapter adapter;

    private ITripViewer mTripViewer;

    private BackgroundContainer backgroundContainer;

    private PlanViewModel planViewModel;

    private PlanViewModel.PlanLiveData planLiveData;

    private OnFragmentAnimationStartListener startAnimation;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        planViewModel = ViewModelProviders.of(getActivity()).get(PlanViewModel.class);

        planLiveData = planViewModel.initPlanLiveData(mTripViewer.getId());
        planLiveData.observe(this,(listPlans) -> {
            Log.e(TAG, "onActivityCreated: Update ui");
            if(adapter == null){
                Log.e(TAG, "onActivityCreated: Update ui first");
                context = getContext();
                if(context == null || listPlans == null) return;
                adapter = new PlansCardViewViewAdapter(R.layout.plans_item_layout,listPlans);
                listView.setAdapter(adapter);
                adapter.setOnTouchListener(new OnInteractItemListener<>(context,this));
            }
            else {
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plans_fragment_layout,container,false);
        listView = v.findViewById(R.id.list_item);
        listView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);

        Bundle bundle = getActivity().getIntent().getBundleExtra("plans");
        if(bundle != null)
            mTripViewer = (ITripViewer) bundle.getSerializable("trip");
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

    @Override
    public RecyclerView getViewList() {
        return listView;
    }

    @Override
    public IViewAdapter<IPlanViewer> getViewAdapter() {
        return adapter;
    }

    @Override
    public BackgroundContainer getBackgroundContainer() {
        return backgroundContainer;
    }

    @Override
    public void onRemovedItem(IPlanViewer item) {
        Plan plan = planViewModel.getPlanById(item.getId());
        Query query = QueryFactory.initOperation(QueryFactory.DELETE, planViewModel, plan);
        query.setArguments(item);
        QueryTransaction.getTransaction().execOnMainThread(query);
    }

    @Override
    public void onSeletedItem(IPlanViewer item) {
        planViewModel.select(item);
        startAnimation.onAnimationStart();
    }
}
