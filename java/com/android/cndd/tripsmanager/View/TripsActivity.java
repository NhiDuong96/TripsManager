package com.android.cndd.tripsmanager.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;

/**
 * Created by Minh Nhi on 3/10/2018.
 */

public class TripsActivity extends AppCompatActivity implements TripsListFragment.OnItemSelectedListener{
    private static final String TAG = "TripsActivity";
    public static final String TRIP = "trip";

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trips_activity_layout);


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_layout,new TripsListFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selected = null;
            switch (item.getItemId()){
                case R.id.action_item1:
                    selected = new TripsListFragment();
                    break;
                case R.id.action_item2:
                    selected = new AlertsFragment();
                    break;
                case R.id.action_item3:
                    selected = new ProfileFragment();
                    break;
                case R.id.action_item4:
                    selected = new AboutFragment();
                    break;
                case R.id.action_item5:
                    selected = new MoreFragment();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout,selected)
                    .commit();
            return true;
        });
        //
        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);
    }

    @Override
    public void onItemSelectedListener(ITripViewer viewer) {
        Log.d("A", "onItemSelectedListener: ");
        Intent intent = new Intent(this, PlansActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip", viewer);
        intent.putExtra("plans",bundle);
        startActivity(intent);
    }
}
