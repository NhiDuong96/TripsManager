package com.android.cndd.tripsmanager.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.cndd.tripsmanager.model.PlanCategory;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.viewhelper.Editor;
import com.android.cndd.tripsmanager.viewhelper.MyRecyclerView;
import com.android.cndd.tripsmanager.viewhelper.OnSlideAnimationStartListener;
import com.android.cndd.tripsmanager.viewhelper.PlansCardViewAdapter;
import com.android.cndd.tripsmanager.viewmodel.PlanViewModel;
import com.android.cndd.tripsmanager.viewmodel.QueryTransaction;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class PlansListFragment extends Fragment implements MyRecyclerView.OnTouchRemovalListener{
    public static final String TAG = "PlansListFragment";

    public static final int REQUEST = 1001;

    private Context context;

    private MyRecyclerView listView;

    private PlansCardViewAdapter adapter;

    private ITripViewer mTripViewer;

    private PlanViewModel planViewModel;

    private OnSlideAnimationStartListener startAnimation;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        if(activity == null) return;
        planViewModel = ViewModelProviders.of(activity).get(PlanViewModel.class);
        planViewModel.initialize(activity);

        planViewModel.getPlanLiveData(mTripViewer.getId())
                .observe(this, listPlans -> {
                    adapter = new PlansCardViewAdapter(R.layout.plans_item_layout,listPlans);
                    listView.setAdapter(adapter);
                });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plans_fragment_layout,container,false);

        Bundle bundle = getActivity().getIntent().getBundleExtra("plans");
        mTripViewer = (ITripViewer) bundle.getSerializable("trip");

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTripViewer.getTitle());


        listView = v.findViewById(R.id.list_item);
        listView.setHasFixedSize(true);
        listView.setLayoutManager( new LinearLayoutManager(getContext()));
        listView.setTouchRemovalListener(this);

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(),PlanCategoryActivity.class);
            startActivityForResult(intent,REQUEST);
        });

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST && resultCode == RESULT_OK){
            Class<?> mClass = (Class<?>) data.getSerializableExtra("class");
            PlanCategory category = (PlanCategory) data.getSerializableExtra("type");
            if(mClass == null || category == null) return;

            Intent intent = new Intent(getContext(), mClass);
            Bundle bundle = new Bundle();
            bundle.putInt("action", Editor.CREATE);
            bundle.putInt("tripId", mTripViewer.getId());
            bundle.putSerializable("plan_type",category);
            intent.putExtra("plan", bundle);
            startActivity(intent);
        }
    }

    public void setClickListener(OnSlideAnimationStartListener startAnimation) {
        this.startAnimation = startAnimation;
    }


    @Override
    public void onRemovedItem(Object item) {
        IPlanViewer viewer = (IPlanViewer)item;
        QueryTransaction.getTransaction()
                .newOperation(QueryTransaction.DELETE, planViewModel, planViewModel.getPlan(viewer))
                .execute();
    }

    @Override
    public void onSeletedItem(Object item) {
        planViewModel.select((IPlanViewer) item);
        startAnimation.onAnimationStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter = null;
    }
}
