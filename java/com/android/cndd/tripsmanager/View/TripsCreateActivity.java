package com.android.cndd.tripsmanager.View;

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

import com.android.cndd.tripsmanager.Model.Trip;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.PickDate;
import com.android.cndd.tripsmanager.ViewModel.IQueryViewObserver;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;
import com.android.cndd.tripsmanager.ViewModel.TripViewModel;
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

    private AutoCompleteTextView mAutoCOmpleteTextView;

    private EditText mTitle, mDescription, mStartDate, mStartTime, mEndDate, mEndTime;

    private Date startDate, endDate;

    private ProgressDialog saveProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_create_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGeoDataClient = Places.getGeoDataClient(this,null);
        //trips title
        mTitle = findViewById(R.id.title);
        //trips destination name
        mAutoCOmpleteTextView = findViewById(R.id.destination);
        //
        startDate = new Date();
        endDate = new Date();
        //trips start date
        mStartDate = findViewById(R.id.startdate);
        PickDate.from(this).setPickDate(mStartDate,startDate);
        //trips start time
        mStartTime = findViewById(R.id.starttime);
        PickDate.from(this).setPickTime(mStartTime,startDate);
        //trips end date
        mEndDate = findViewById(R.id.enddate);
        PickDate.from(this).setPickDate(mEndDate,endDate);
        //trips end time
        mEndTime = findViewById(R.id.endtime);
        PickDate.from(this).setPickTime(mEndTime,endDate);
        //description
        mDescription = findViewById(R.id.description);

        mAdapter = new PlaceAutocompleteAdapter(this ,mGeoDataClient, null, null);
        mAutoCOmpleteTextView.setAdapter(mAdapter);
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
                Trip trip = getObj();
                TripViewModel model = ViewModelProviders.of(this).get(TripViewModel.class);
                Query<Trip> query = new QueryFactory.InsertOperation<>(model, trip);
                query.setUpdateUICode(1000);
                QueryTransaction transaction = QueryTransaction.getTransaction();
                transaction.execOnBackground(query, observer);
                break;
            case android.R.id.home:
                Log.e(TAG, "onOptionsItemSelected: home");
                //
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private IQueryViewObserver observer = new IQueryViewObserver() {

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

        }
    };

    public Trip getObj(){
        return new Trip(
                mTitle.getText().toString(),
                mAutoCOmpleteTextView.getText().toString(),
                startDate,endDate,
                null,
                null,
                mDescription.getText().toString());
    }
}
