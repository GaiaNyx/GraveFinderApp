package com.example.gravefinder;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class gravePageActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference database;
    private TextView tvGraveName;
    private TextView tvBirthDate;
    private TextView tvDeathDate;
    private TextView tvDescription;
    private Button btnNavigate;
    private Button btnAddNavigation;
    private Button btnPicsAndMemos;
    private Button btnAddPicsAndMemos;
    private Button btnFave;
    private Button btnAddFave;
    private Button btnMenu;
    private Grave currentGrave;
    private FirebaseUser currentUser;
    private String userID;
    private String graveID;

    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grave_page);

        tvGraveName = findViewById(R.id.tvGraveName);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvDeathDate = findViewById(R.id.tvDeathDate);
        tvDescription = findViewById(R.id.tvDescription);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnNavigate = findViewById(R.id.btnNavigate);
        btnNavigate.setOnClickListener(this);

        btnAddNavigation = findViewById(R.id.btnAddNavigation);
        btnAddNavigation.setOnClickListener(this);

        btnPicsAndMemos = findViewById(R.id.btnPicsAndMemos);
        btnPicsAndMemos.setOnClickListener(this);

        btnAddPicsAndMemos = findViewById(R.id.btnAddPicsAndMemos);
        btnAddPicsAndMemos.setOnClickListener(this);

        btnFave = findViewById(R.id.btnFave);
        btnFave.setOnClickListener(this);

        btnAddFave = findViewById(R.id.btnAddFave);
        btnAddFave.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        Intent i = getIntent();
        Bundle b = i.getExtras();
        database = FirebaseDatabase.getInstance().getReference();
        graveID = (String) b.get("id");
        Log.d("graveID", graveID);
        database.child("graves").child(graveID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentGrave = snapshot.getValue(Grave.class);
                    tvGraveName.setText(currentGrave.getGraveName());
                    tvBirthDate.setText(currentGrave.getBirthDate());
                    tvDeathDate.setText(currentGrave.getDeathDate());
                    tvDescription.setText(currentGrave.getDescription());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()) {
            Intent i = new Intent(this, menuActivity.class);
            startActivity(i);
        } else if (view.getId() == btnPicsAndMemos.getId()) {
            Intent i = new Intent(this, picsAndMemosActivity.class);
            i.putExtra("id", graveID);
            startActivity(i);
        } else if (view.getId() == btnAddPicsAndMemos.getId()) {
            Intent i = new Intent(this, addPicsAndMemosActivity.class);
            i.putExtra("id", graveID);
            startActivity(i);
        } else if (view.getId() == btnNavigate.getId()) {
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("id", graveID);
            startActivity(i);
        } else if (view.getId() == btnAddNavigation.getId()) {
            setCurrentLocation();
            Toast.makeText(getApplicationContext(),"נוסף",Toast.LENGTH_LONG).show();
        } else if (view.getId() == btnFave.getId()) {
            Intent i = new Intent(this, myGravesActivity.class);
            i.putExtra("id", userID);
            startActivity(i);
        } else if (view.getId() == btnAddFave.getId()) { //adding to the user's faves
                database.child("users").child(currentUser.getUid()).child("myGraves")
                        .child(currentGrave.getGraveId()).setValue(currentGrave);
                }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (isGPSEnabled()) {
                    setCurrentLocation();
                }else {
                    turnOnGPS();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                setCurrentLocation();
            }
        }
    }

    private void setCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(gravePageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(gravePageActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(gravePageActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        database.child("graves").child(graveID).child("latitude").setValue(latitude);
                                        database.child("graves").child(graveID).child("longitude").setValue(longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(gravePageActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(gravePageActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsItem:
                Intent i1 = new Intent(this, settingsActivity.class);
                this.startActivity(i1);
                break;
            case R.id.logOutItem:
                FirebaseAuth.getInstance().signOut();
                Intent i2 = new Intent(this, MainActivity.class);
                this.startActivity(i2);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}