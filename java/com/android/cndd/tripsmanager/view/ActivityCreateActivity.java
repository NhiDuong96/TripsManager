package com.android.cndd.tripsmanager.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cndd.tripsmanager.model.Meeting;
import com.android.cndd.tripsmanager.model.PlanCategory;
import com.android.cndd.tripsmanager.viewhelper.DatePicker;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.viewhelper.Editor;
import com.android.cndd.tripsmanager.viewhelper.TimePicker;
import com.android.cndd.tripsmanager.viewmodel.IProgressQueryObserver;
import com.android.cndd.tripsmanager.viewmodel.MeetingViewModel;
import com.android.cndd.tripsmanager.viewmodel.Query;
import com.android.cndd.tripsmanager.viewmodel.QueryTransaction;
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

public class ActivityCreateActivity extends AppCompatActivity{
    public static final String TAG = "ActivityCreateActivity";

    private GeoDataClient mGeoDataClient;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private AutoCompleteTextView mLocationName;

    private EditText description,address;

    private Date startDateTime, endDateTime;

    private LatLng mLatLng;

    private int mTripId;
    private int mActionId;

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

        mLocationName = findViewById(R.id.location_name);

        description = findViewById(R.id.description);

        address = findViewById(R.id.address);

        startDateTime = new Date();
        endDateTime = new Date();

        DatePicker startDate = findViewById(R.id.startdate);
        startDate.addDate(startDateTime);
        TimePicker startTime = findViewById(R.id.starttime);
        startTime.addTime(startDateTime);

        DatePicker endDate = findViewById(R.id.enddate);
        endDate.addDate(endDateTime);
        TimePicker endTime = findViewById(R.id.endtime);
        endTime.addTime(endDateTime);

        meetingViewModel = ViewModelProviders.of(this).get(MeetingViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("plan");
        mActionId = bundle.getInt("action");
        mTripId = bundle.getInt("tripId");
        if(mActionId == Editor.UPDATE){
            int mPlanId = bundle.getInt("planId");
            currentMeeting = meetingViewModel.getMeetingById(mPlanId);
            mLocationName.setText(currentMeeting.getLocationName());
            description.setText(currentMeeting.getDescription());
            address.setText(currentMeeting.getAddress());
            startDate.setText(DatePicker.convertToDate(currentMeeting.getStartTime()));
            startTime.setText(TimePicker.convertToTime(currentMeeting.getStartTime()));
            endDate.setText(DatePicker.convertToDate(currentMeeting.getEndTime()));
            endTime.setText(TimePicker.convertToTime(currentMeeting.getEndTime()));
        }

        TextView type = findViewById(R.id.type);
        PlanCategory category = (PlanCategory) bundle.getSerializable("plan_type");
        if (category != null)
            type.setText(category.toString());


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
                if(mActionId == Editor.CREATE){
                    Meeting meeting = setMeeting();
                    QueryTransaction.getTransaction()
                            .newOperation(QueryTransaction.INSERT, meetingViewModel, meeting)
                            .execute(observer);
                }else{
                    Meeting meeting = updateMeeting();
                    QueryTransaction.getTransaction()
                            .newOperation(QueryTransaction.UPDATE, meetingViewModel, meeting)
                            .execute(observer);
                }
                break;
            case android.R.id.home:
                Log.e(TAG, "onOptionsItemSelected: home");

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private IProgressQueryObserver observer = new IProgressQueryObserver() {
        @Override
        public void onStartQuery() {
            saveProgress = new ProgressDialog(ActivityCreateActivity.this,R.style.CustomDialog);
            saveProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            saveProgress.setMessage("Saving");
            saveProgress.show();
        }

        @Override
        public void onEndQuery() {
            saveProgress.dismiss();
        }

        @Override
        public void onSuccessQuery(Query query) {
            ActivityCreateActivity.this.finish();
        }

        @Override
        public void onFailQuery(Query query) {
            Toast.makeText(ActivityCreateActivity.this, "Can not save change!", Toast.LENGTH_SHORT).show();
        }
    };

    public Meeting updateMeeting(){
        try {
            currentMeeting.setPlanCategoryName(PlanCategory.Meeting);
            currentMeeting.setDescription(description.getText().toString());
            currentMeeting.setLocationName(mLocationName.getText().toString());
            currentMeeting.setAddress(address.getText().toString());
            if (mLatLng != null) {
                currentMeeting.setLatitude(mLatLng.latitude);
                currentMeeting.setLongitude(mLatLng.longitude);
            }
            currentMeeting.setStartTime(startDateTime);
            currentMeeting.setEndTime(endDateTime);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return currentMeeting;
    }


    private Meeting setMeeting(){
        Meeting meeting = new Meeting();
        try {
            meeting.setTripId(mTripId);
            meeting.setPlanCategoryName(PlanCategory.Meeting);
            meeting.setDescription(description.getText().toString());
            meeting.setLocationName(mLocationName.getText().toString());
            meeting.setAddress(address.getText().toString());
            if(mLatLng != null) {
                meeting.setLatitude(mLatLng.latitude);
                meeting.setLongitude(mLatLng.longitude);
            }
            meeting.setStartTime(startDateTime);
            meeting.setEndTime(endDateTime);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return meeting;
    }
}
