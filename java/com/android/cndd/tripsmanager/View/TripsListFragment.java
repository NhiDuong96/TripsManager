package com.android.cndd.tripsmanager.view;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cndd.tripsmanager.model.Trip;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.viewhelper.Editor;
import com.android.cndd.tripsmanager.viewhelper.MyRecyclerView;
import com.android.cndd.tripsmanager.viewhelper.NotificationService;
import com.android.cndd.tripsmanager.viewhelper.TripsCardViewAdapter;
import com.android.cndd.tripsmanager.viewmodel.QueryTransaction;
import com.android.cndd.tripsmanager.viewmodel.TripViewModel;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class TripsListFragment extends Fragment
        implements MyRecyclerView.OnTouchRemovalListener
        , TripsCardViewAdapter.OnChangedNotification<Trip>{
    private static final String TAG = "TripsListFragment";
    private static final int INDEX = 1000;

    private TripsCardViewAdapter tripsAdapter;

    private MyRecyclerView recyclerView;

    private OnItemSelectedListener listener;

    private TripViewModel tripViewModel;

    private TabHost tabs;


    public interface OnItemSelectedListener{
        void onItemSelectedListener(ITripViewer viewer);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

        tripViewModel.getTripLiveData().observe(this,(listTrips)->{
            tripsAdapter = new TripsCardViewAdapter(R.layout.trips_item_layout, listTrips);
            tripsAdapter.setOnChangedNotification(this);
            recyclerView.setAdapter(tripsAdapter);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trips_fragment_layout,container,false);

        recyclerView = v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setTouchRemovalListener(this);

        tabs = v.findViewById(R.id.tabhost);
        setTagHost();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //create a new trip
            Intent intent = new Intent(getContext(),TripsCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("action", Editor.CREATE);
            intent.putExtra("trip_update", bundle);
            startActivity(intent);
        });
        return v;
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


    @Override
    public void onRemovedItem(Object item) {
        ITripViewer viewer = (ITripViewer) item;
        Trip trip = tripViewModel.getTripFromId(viewer.getId());
        QueryTransaction.getTransaction()
                .newOperation(QueryTransaction.DELETE, tripViewModel ,trip)
                .execute();
    }

    @Override
    public void onSeletedItem(Object item) {
        listener.onItemSelectedListener((ITripViewer) item);
    }

    @Override
    public void onTurnOnNotification(Trip target) {
        if(target.isNotify()) return;
        Intent intent = new Intent(getContext(), NotificationService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", (ITripViewer)target);
        bundle.putInt("id", INDEX + target.getId());
        intent.putExtra("notify", bundle);

        PendingIntent pendingIntent = PendingIntent.getService(getContext(),INDEX + target.getId(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        AlarmManager alarm = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        if(alarm == null){
            Log.e(TAG, "onTurnOnNotification: alarm null");
            return;
        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, 5);

        alarm.setExact(AlarmManager.RTC_WAKEUP, target.getStartTime().getTime(), pendingIntent);
        //alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(getContext(), "Turn On Notification", Toast.LENGTH_SHORT).show();

        target.setNotify(true);
        QueryTransaction.getTransaction()
                .newOperation(QueryTransaction.UPDATE, tripViewModel,target)
                .execute();
    }

    @Override
    public void onTurnOffNotification(Trip target) {
        if(!target.isNotify()) return;
        AlarmManager manager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(),INDEX + target.getId(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pendingIntent);

        Toast.makeText(getContext(), "Turn Off Notification", Toast.LENGTH_SHORT).show();
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
}
