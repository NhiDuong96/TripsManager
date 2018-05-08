package com.android.cndd.tripsmanager.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.cndd.tripsmanager.model.Trip;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.viewhelper.DatePicker;
import com.android.cndd.tripsmanager.viewhelper.Editor;
import com.android.cndd.tripsmanager.viewhelper.TimePicker;
import com.android.cndd.tripsmanager.viewmodel.IProgressQueryObserver;
import com.android.cndd.tripsmanager.viewmodel.Query;
import com.android.cndd.tripsmanager.viewmodel.QueryTransaction;
import com.android.cndd.tripsmanager.viewmodel.TripViewModel;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import java.util.Date;

/**
 * Created by Minh Nhi on 3/5/2018.
 */

public class TripsCreateActivity extends AppCompatActivity {
    public static final String TAG = "TripsCreateActivity";
    public static final int QUERY_CODE = 1000;

    private GeoDataClient mGeoDataClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mDestination;

    private EditText mTitle, mDescription;

    private TimePicker mStartTime, mEndTime;

    private DatePicker mStartDate, mEndDate;

    private Date startDate, endDate;

    private ProgressDialog saveProgress;

    private int mAction;
    private int mTripId;

    private TripViewModel tripViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_create_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGeoDataClient = Places.getGeoDataClient(this,null);
        //trips title
        mTitle = findViewById(R.id.title);
        //trips destination name
        mDestination = findViewById(R.id.destination);

        startDate = new Date();
        endDate = new Date();
        //trips start date
        mStartDate = findViewById(R.id.startdate);
        mStartDate.addDate(startDate);
        //trips start time
        mStartTime = findViewById(R.id.starttime);
        mStartTime.addTime(startDate);
        //trip end date
        mEndDate = findViewById(R.id.enddate);
        mEndDate.addDate(endDate);
        //trips end time
        mEndTime = findViewById(R.id.endtime);
        mEndTime.addTime(endDate);

        //description
        mDescription = findViewById(R.id.description);

        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("trip_update");
        mAction = bundle.getInt("action");
        if(mAction == Editor.UPDATE){
            ITripViewer viewer = (ITripViewer) bundle.getSerializable("trip");
            if(viewer != null){
                mTripId = viewer.getId();
                Trip trip = tripViewModel.getTripFromId(mTripId);
                mTitle.setText(trip.getTitle());
                mDestination.setText(trip.getDestination());
                mStartDate.setText(DatePicker.convertToDate(trip.getStartTime()));
                mStartTime.setText(TimePicker.convertToTime(trip.getStartTime()));
                mEndDate.setText(DatePicker.convertToDate(trip.getEndTime()));
                mEndTime.setText(TimePicker.convertToTime(trip.getEndTime()));
                mDescription.setText(trip.getDescription());
            }
        }

        mAdapter = new PlaceAutocompleteAdapter(this ,mGeoDataClient, null, null);
        mDestination.setAdapter(mAdapter);
    }

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
                if(mAction == Editor.CREATE){
                    Trip trip = getObj(-1);
                    QueryTransaction.getTransaction()
                            .newOperation(QueryTransaction.INSERT, tripViewModel, trip)
                            .execute(observer);

                }else{
                    Trip trip = getObj(mTripId);
                    QueryTransaction.getTransaction()
                            .newOperation(QueryTransaction.UPDATE, tripViewModel,trip)
                            .execute(observer);
                }
                break;
            case android.R.id.home:
                Log.e(TAG, "onOptionsItemSelected: home");
                //
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private IProgressQueryObserver observer = new IProgressQueryObserver() {

        @Override
        public void onStartQuery() {
            saveProgress = new ProgressDialog(TripsCreateActivity.this,R.style.CustomDialog);
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
            TripsCreateActivity.this.finish();
        }

        @Override
        public void onFailQuery(Query query) {
            Toast.makeText(TripsCreateActivity.this, "Can not save change!", Toast.LENGTH_SHORT).show();
        }
    };

    public Trip getObj(int trip_id){
        Trip trip = new Trip();
        if(mAction == Editor.UPDATE) trip.setId(trip_id);
        trip.setTitle(mTitle.getText().toString());
        trip.setDestination(mDestination.getText().toString());
        trip.setStartTime(startDate);
        trip.setEndTime(endDate);
        trip.setNotify(false);
        trip.setDescription(mDescription.getText().toString());
        return trip;
    }
}
