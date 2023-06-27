package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.gravefinder.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private MapView mvMap;
    private TextView tvHeadLine;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private LocationRequest locationRequest;

    private double userLatitude;
    private double userLongitude;

    private DatabaseReference database;
    private String graveID;
    private Grave currentGrave;
    private double graveLatitude;
    private double graveLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvHeadLine = findViewById(R.id.tvHeadLine);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        database = FirebaseDatabase.getInstance().getReference();
        graveID = (String) b.get("id");
        Log.d("graveID", graveID);
        database.child("graves").child(graveID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGrave = snapshot.getValue(Grave.class);
                graveLatitude = currentGrave.getLatitude();
                graveLongitude = currentGrave.getLongitude();
                if(graveLongitude == 0 && graveLatitude == 0)
                    Toast.makeText(MapsActivity.this, "Location hasn't been added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Initialize mapView
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
        {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mvMap = findViewById(R.id.mvMap);
        mvMap.onCreate(mapViewBundle);
        mvMap.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady", Toast.LENGTH_LONG).show();
        mMap = googleMap;

        if(graveLatitude == 0 && graveLongitude ==0){
            tvHeadLine.setText("Location hasn't been added");
        }
        //mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        LatLng grave = new LatLng(graveLatitude, graveLongitude);
        mMap.addMarker(new MarkerOptions().position(grave).title("הקבר שנבחר"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(grave));
        mMap.setMinZoomPreference(16);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mvMap.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String s = marker.getTitle() + "\n" + marker.getSnippet();
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        /*tvLat.setText("N" + latLng.latitude);
        tvLong.setText("E" + latLng.longitude);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvMap.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mvMap.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mvMap.onStop();
    }

    @Override
    protected void onPause() {
        mvMap.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mvMap.onDestroy();
        super.onDestroy();
    }

}