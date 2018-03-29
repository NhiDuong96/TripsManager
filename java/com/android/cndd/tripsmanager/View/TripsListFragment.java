package com.android.cndd.tripsmanager.View;

import android.app.Activity;
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
import android.widget.TabHost;

import com.android.cndd.tripsmanager.Model.Trip;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.BackgroundContainer;
import com.android.cndd.tripsmanager.ViewHelper.OnActionTouchItemListener;
import com.android.cndd.tripsmanager.ViewHelper.TripsCardViewAdapter;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;
import com.android.cndd.tripsmanager.ViewModel.TripViewModel;
import com.android.cndd.tripsmanager.Viewer.TripViewer;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class TripsListFragment extends Fragment {
    private static final String TAG = "TripsListFragment";

    private Context context;

    private TripsCardViewAdapter tripsAdapter;

    private RecyclerView listView;

    private BackgroundContainer backgroundContainer;

    private OnItemSelectedListener listener;

    private TripViewModel tripViewModel;

    private TripViewModel.TripLiveData dataLiveUpdateUI;

    public interface OnItemSelectedListener{
        void onItemSelectedListener(TripViewer viewer);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

        dataLiveUpdateUI = tripViewModel.getTripLiveData();

        dataLiveUpdateUI.observe(this,(listTrips)->{
            if(tripsAdapter == null){
                context = getContext();
                if(context == null || listTrips == null) return;
                tripsAdapter = new TripsCardViewAdapter(R.layout.trips_item_layout, listTrips);
                listView.setAdapter(tripsAdapter);

                OnActionTouchItemListener removalTouchListener =
                        new OnActionTouchItemListener<TripViewer>(context, listView,tripsAdapter,backgroundContainer){
                            @Override
                            protected void onRemoveItem(TripViewer item) {
                                removeItem(item);
                            }

                            @Override
                            protected void onTouchItem(TripViewer item) {
                                listener.onItemSelectedListener(item);
                            }
                        };
                tripsAdapter.setOnTouchListener(removalTouchListener);
            }else{
                Log.e(TAG, "onActivityCreated: data set changed");
                tripsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void removeItem(TripViewer viewer){
        Trip trip = tripViewModel.getTripFromId(viewer.getId());
        Query<Trip> query = new QueryFactory.DeleteOperation<>(tripViewModel ,trip);
        query.setUpdateUIListener(dataLiveUpdateUI);
        query.setArguments(viewer);
        QueryTransaction.getTransaction().execOnMainThread(query);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trips_fragment_layout,container,false);
        listView = v.findViewById(R.id.cardList);
        listView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layoutManager);

        backgroundContainer = v.findViewById(R.id.listViewBackground);
        TabHost tabs = v.findViewById(R.id.tabhost);
        setTagHost(tabs);
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //create a new trip
            Intent intent = new Intent(getContext(),TripsCreateActivity.class);
            startActivity(intent);
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof OnItemSelectedListener){
            listener = (OnItemSelectedListener) activity;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        tripsAdapter = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void setTagHost(TabHost tabs) {
        tabs.setup();

        TabHost.TabSpec active = tabs.newTabSpec("active");
        active.setIndicator("active");
        active.setContent(R.id.layout1);
        tabs.addTab(active);

        TabHost.TabSpec past = tabs.newTabSpec("past");
        past.setIndicator("past");
        past.setContent(R.id.layout2);
        tabs.addTab(past);

        TabHost.TabSpec follow = tabs.newTabSpec("follow");
        follow.setIndicator("follow");
        follow.setContent(R.id.layout3);
        tabs.addTab(follow);
    }
}
