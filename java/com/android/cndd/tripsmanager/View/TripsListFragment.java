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
import android.widget.TextView;

import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.Model.Trip;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.BackgroundContainer;
import com.android.cndd.tripsmanager.ViewHelper.OnActionTouchItemListener;
import com.android.cndd.tripsmanager.ViewHelper.TripsCardViewAdapter;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;
import com.android.cndd.tripsmanager.ViewModel.TripViewModel;

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

    private TabHost tabs;

    public interface OnItemSelectedListener{
        void onItemSelectedListener(ITripViewer viewer);
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
                        new OnActionTouchItemListener<ITripViewer>(context, listView,tripsAdapter,backgroundContainer){
                            @Override
                            protected void onRemoveItem(ITripViewer item) {
                                removeItem(item);
                            }

                            @Override
                            protected void onTouchItem(ITripViewer item) {
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

    public void removeItem(ITripViewer viewer){
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
        tabs = v.findViewById(R.id.tabhost);
        setTagHost();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //create a new trip
            Intent intent = new Intent(getContext(),TripsCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("action",0);
            intent.putExtra("trip_update", bundle);
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

    private void setTagHost() {
        tabs.setup();

        setupTab("active",R.id.layout1);
        setupTab("follow",R.id.layout2);
        setupTab("past",R.id.layout3);
    }

    private void setupTab(String tag, int layoutId) {
        View tabview = createTabView(tabs.getContext(), tag);
        TabHost.TabSpec setContent = tabs.newTabSpec(tag)
                                    .setIndicator(tabview)
                                    .setContent(layoutId);
        tabs.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_background, null);
        TextView tv = view.findViewById(R.id.tabsText);
        tv.setText(text);
        return view;
    }
}
