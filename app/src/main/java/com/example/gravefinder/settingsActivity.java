package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class settingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Switch switchWork;
    private Calendar calendar;

    private EditText etEmail;
    private EditText etPhone;
    private EditText etName;
    private EditText etPassword;
    private Button btnMenu;
    private Button btnSave;
    private TextView messageTitle;

    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userID;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        TextView messageTitle = new TextView(this);
        messageTitle.setText("לשמור?");
        messageTitle.setGravity(Gravity.RIGHT);

        switchWork = findViewById(R.id.switchWork);
        switchWork.setOnCheckedChangeListener(this);

        calendar = Calendar.getInstance();

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();


        database.child("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                etEmail.setText(snapshot.child("email").getValue(String.class));
                etPassword.setText(snapshot.child("password").getValue(String.class));
                etName.setText(snapshot.child("name").getValue(String.class));
                etPhone.setText(snapshot.child("phone").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sharedPreferences = getSharedPreferences("isChecked", 0);
        Boolean value = sharedPreferences.getBoolean("isChecked", true);
        switchWork.setChecked(value);


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (switchWork.isChecked()) {

            switchWork.setChecked(true);
            sharedPreferences.edit().putBoolean("isChecked", true).apply();
            // code for setting the specific time for the alarm
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // if alarm time has already passed, increment day by 1
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            }

            PeriodicWorkRequest memorialWorkRequest =
                        new PeriodicWorkRequest.Builder(BgWorker.class,30, TimeUnit.MINUTES)
                                .setInitialDelay(calendar.getTimeInMillis() - System.currentTimeMillis(),
                                        java.util.concurrent.TimeUnit.MILLISECONDS)
                                .addTag("memorial work").build();
                WorkManager.getInstance(this).enqueueUniquePeriodicWork("send logs",
                        ExistingPeriodicWorkPolicy.REPLACE, (PeriodicWorkRequest) memorialWorkRequest);
            }
        else if (!switchWork.isChecked()){
            switchWork.setChecked(false);
            sharedPreferences.edit().putBoolean("isChecked", false).apply();
            WorkManager.getInstance(this).cancelAllWorkByTag("memorial work");
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()){
            Intent i = new Intent(this, menuActivity.class);
            startActivity(i);
        }
        else if (view.getId() == btnSave.getId()){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCustomTitle(messageTitle)
                    .setMessage("Please check your personal information again")
                    .setPositiveButton("check", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString())
                                    .build();
                            currentUser.updateEmail(etEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                            currentUser.updatePassword(etPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                            database.child("users").child(userID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.child("email").getRef().setValue(etEmail.getText().toString());
                                    snapshot.child("password").getRef().setValue(etPassword.getText().toString());
                                    snapshot.child("name").getRef().setValue(etName.getText().toString());
                                    snapshot.child("phone").getRef().setValue(etPhone.getText().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(getApplicationContext(),"Changes saved",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resource, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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