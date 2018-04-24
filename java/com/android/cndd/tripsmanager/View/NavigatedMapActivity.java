package com.android.cndd.tripsmanager.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.android.cndd.tripsmanager.Model.IPlanViewer;
import com.android.cndd.tripsmanager.Modules.DirectionFinder;
import com.android.cndd.tripsmanager.Modules.DirectionFinderListener;
import com.android.cndd.tripsmanager.Modules.Route;
import com.android.cndd.tripsmanager.View.PlaceAutocompleteAdapter;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Minh Nhi on 3/15/2018.
 */

public class NavigatedMapActivity extends FragmentActivity implements
        GoogleMap.OnMarkerClickListener,
        DirectionFinderListener,
        OnMapReadyCallback {
    private static final String TAG = "NavigatedMapActivity";

    private GeoDataClient mGeoDataClient;

    private FusedLocationProviderClient mFusedLocationClient;

    private LatLng mMyLocation;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutoCOmpleteTextView;

    private GoogleMap mMap;

    private ProgressDialog progressDialog;

    private IPlanViewer planViewer;

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_demo);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //trips destination name
        mAutoCOmpleteTextView = findViewById(R.id.destination);

        Bundle bundle = getIntent().getBundleExtra("plan");

        planViewer = (IPlanViewer) bundle.getSerializable("viewer");

        mAutoCOmpleteTextView.setText(planViewer.getInformation());

        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, null, null);
        mAutoCOmpleteTextView.setAdapter(mAdapter);
        mAutoCOmpleteTextView.setOnItemClickListener(itemClickListener);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        turnGPSOn();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getLastLocation();
    }

    private void sendRequest(LatLng origin, LatLng destination) {
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(16.077019, 108.152780), 13));

        mMap.setOnMarkerClickListener(this);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        sendRequest(mMyLocation , planViewer.getLatLng());
                    } else {
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                getLastLocation();
            break;
        }
    }

    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void turnGPSOn(){
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(off==0){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
        }
    }


    private AdapterView.OnItemClickListener itemClickListener = (parent, view, position, id) -> {
        mAutoCOmpleteTextView.clearFocus();
        AutocompletePrediction item = mAdapter.getItem(position);

        assert item != null;
        Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(item.getPlaceId());

        placeResult.addOnCompleteListener(task -> {
            try {
                PlaceBufferResponse places = task.getResult();
                // Get the Place object from the buffer.
                final Place place = places.get(0);
                //set UI here
                sendRequest(mMyLocation , place.getLatLng());

//                mMap.addMarker(new MarkerOptions()
//                .position(place.getLatLng())
//                .title(place.getName().toString()));
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),13));

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
            }
        });
    };

    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline: polylinePaths ) {
                polyline.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {

        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                   // .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                   // .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

    }
}
