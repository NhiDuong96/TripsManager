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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.Model.PlanCategory.Meeting;
import com.android.cndd.tripsmanager.ViewHelper.PickDate;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewModel.IQueryViewObserver;
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

    private EditText description,address,startDate,startTime, endDate, endTime;

    private Date startDateTime, endDateTime;

    private int mTripId;

    private PlanViewModel planLiveData;

    private ProgressDialog saveProgress;
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
        startDate = findViewById(R.id.startdate);
        PickDate.from(this).setPickDate(startDate,startDateTime);
        startTime = findViewById(R.id.starttime);
        PickDate.from(this).setPickTime(startTime,startDateTime);

        endDate = findViewById(R.id.enddate);
        PickDate.from(this).setPickDate(endDate,endDateTime);
        endTime = findViewById(R.id.endtime);
        PickDate.from(this).setPickTime(endTime,endDateTime);

        Bundle bundle = getIntent().getBundleExtra("plan");

        mTripId = getIntent().getIntExtra(PlansListFragment.TAG,0);

        planLiveData = ViewModelProviders.of(this).get(PlanViewModel.class);

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
                Query<Plan> query = new QueryFactory.InsertOperation<>(planLiveData, new Plan(mTripId, PlanCategories.Meeting));
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
        final Meeting meeting = getMeeting(planLiveData.getLastItem().getId());
        MeetingViewModel model = ViewModelProviders.of(this).get(MeetingViewModel.class);
        Query<Meeting> query = new QueryFactory.InsertOperation<>(model, meeting);
        query.setUpdateUICode(1001);
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


    public Meeting getMeeting(int plan_id){
        Meeting meeting = new Meeting(plan_id,description.getText().toString());
        meeting.setLocationName(mLocationName.getText().toString());
        meeting.setAddress(address.getText().toString());
        meeting.setStartTime(startDateTime);
        meeting.setEndTime(endDateTime);
        return meeting;
    }
}
