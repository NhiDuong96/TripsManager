package com.android.cndd.tripsmanager.View;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.cndd.tripsmanager.Model.PlanCategories;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;
import com.android.cndd.tripsmanager.ViewHelper.PickDate;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewModel.IQueryViewObserverAdapter;
import com.android.cndd.tripsmanager.ViewModel.MeetingViewModel;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.Task;

import java.util.Date;

/**
 * Created by Minh Nhi on 3/8/2018.
 */

public class MeetingCreateActivity extends AppCompatActivity {
    public static final String TAG = "MeetingCreateActivity";
    public static final int REQUEST_CODE = 1001;

    private GeoDataClient mGeoDataClient;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private AutoCompleteTextView mLocationName;

    private EditText type, description,address,startDate,startTime, endDate, endTime;

    private Date startDateTime, endDateTime;

    private LatLng mLatLng;

    private int mTripId;
    private int mActionId;
    private int mPlanId;

    private PlanViewModel planLiveData;
    private MeetingViewModel meetingViewModel;

    private Meeting currentMeeting;

    private ProgressDialog saveProgress;

    private String[] arr = {"Activity", "Meeting", "Tour", "Concert", "Theatre"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_meeting_create_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGeoDataClient = Places.getGeoDataClient(this,null);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(adapter);

        mLocationName = findViewById(R.id.location_name);

        description = findViewById(R.id.description);

        address = findViewById(R.id.address);

        startDateTime = new Date();
        endDateTime = new Date();
        startDate = findViewById(R.id.startdate);
        PickDate.from(this).setPickDate(startDate,startDateTime);
        startTime = findViewById(R.id.starttime);
        PickDate.from(this).setPickTime(startTime,startDateTime);

        endDate = findViewById(R.id.enddate);
        PickDate.from(this).setPickDate(endDate,endDateTime);
        endTime = findViewById(R.id.endtime);
        PickDate.from(this).setPickTime(endTime,endDateTime);

        planLiveData = ViewModelProviders.of(this).get(PlanViewModel.class);
        meetingViewModel = ViewModelProviders.of(this).get(MeetingViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("plan");
        mActionId = bundle.getInt("action");
        mTripId = bundle.getInt("tripId");
        if(mActionId == 1){
            mPlanId = bundle.getInt("planId");
            currentMeeting = meetingViewModel.getMeetingFromPlanId(mPlanId);
            mLocationName.setText(currentMeeting.getLocationName());
            description.setText(currentMeeting.getDescription());
            address.setText(currentMeeting.getAddress());
            startDate.setText(PickDate.convertToDate(currentMeeting.getStartTime()));
            startTime.setText(PickDate.convertToTime(currentMeeting.getStartTime()));
            endDate.setText(PickDate.convertToDate(currentMeeting.getEndTime()));
            endTime.setText(PickDate.convertToTime(currentMeeting.getEndTime()));
        }

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGeoDataClient,null,null);
        mLocationName.setAdapter(mPlaceAutocompleteAdapter);
        mLocationName.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
        AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
        assert item != null;

        Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(item.getPlaceId());

        placeResult.addOnCompleteListener(task -> {
            try {
                PlaceBufferResponse places = task.getResult();
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                //set UI here
                address.setText(place.getAddress());
                mLatLng = place.getLatLng();
                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
            }
        });
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trips,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                Log.e(TAG, "onOptionsItemSelected: save");
                Query query;
                if(mActionId == 0)
                    query = QueryFactory.initOperation(QueryFactory.INSERT, planLiveData, new Plan(mTripId, PlanCategories.Meeting));
                else{
                    query = QueryFactory.initOperation(QueryFactory.UPDATE, planLiveData, planLiveData.getPlanById(mPlanId));
                }
                QueryTransaction.getTransaction().execOnBackground(query, new IQueryViewObserverAdapter() {
                    @Override
                    public void onStartQuery() {
                        saveProgress = new ProgressDialog(MeetingCreateActivity.this,R.style.CustomDialog);
                        saveProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        saveProgress.setMessage("Saving");
                        saveProgress.show();
                    }
                    @Override
                    public void onSuccessQuery(Query query) {
                        savePlan();
                    }
                });
                break;
            case android.R.id.home:
                Log.e(TAG, "onOptionsItemSelected: home");
                //
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void savePlan(){
        Query query;
        if(mActionId == 0){
            Meeting meeting = getMeeting(planLiveData.getLastItem().getId(), -1);
            query = QueryFactory.initOperation(QueryFactory.INSERT, meetingViewModel, meeting);
        }else{
            Meeting meeting = getMeeting(mPlanId, currentMeeting.getId());
            query = QueryFactory.initOperation(QueryFactory.UPDATE, meetingViewModel, meeting);
            query.setArguments(mPlanId);
        }
        QueryTransaction.getTransaction().execOnBackground(query, new IQueryViewObserverAdapter() {
            @Override
            public void onEndQuery() {
                saveProgress.dismiss();
            }

            @Override
            public void onSuccessQuery(Query query) {
                MeetingCreateActivity.this.finish();
            }
        });
    }


    public Meeting getMeeting(int plan_id, int meeting_id){
        Meeting meeting = new Meeting();
        if(mActionId == 1) meeting.setId(meeting_id);
        meeting.setPlanId(plan_id);
        meeting.setDescription(description.getText().toString());
        meeting.setLocationName(mLocationName.getText().toString());
        meeting.setAddress(address.getText().toString());
        meeting.setLatitude(mLatLng.latitude);
        meeting.setLongitude(mLatLng.longitude);
        meeting.setStartTime(startDateTime);
        meeting.setEndTime(endDateTime);
        return meeting;
    }
}
