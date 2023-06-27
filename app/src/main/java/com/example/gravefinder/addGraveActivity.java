package com.example.gravefinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class addGraveActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText etGraveName;
    private EditText etBirthDate;
    private EditText etDeathDate;
    private EditText etDescription;
    private Button btnMenu;
    private Button btnSave;
    private TextView messageTitle;
    private String graveId;

    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grave);

        etGraveName = findViewById(R.id.etGraveName);
        etDescription = findViewById(R.id.etDescription);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        etBirthDate = findViewById(R.id.etBirthDate);
        etBirthDate.setOnClickListener(this);
        etDeathDate = findViewById(R.id.etDeathDate);
        etDeathDate.setOnClickListener(this);
        TextView messageTitle = new TextView(this);
        messageTitle.setText("לשמור?");
        messageTitle.setGravity(Gravity.RIGHT);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()){
            Intent i = new Intent(this, menuActivity.class);
            startActivity(i);
        }

        else if (view.getId() == etBirthDate.getId()){
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialogDeath = new DatePickerDialog(
                    addGraveActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    etBirthDate.setText(day + "/" + (month+1) +"/" + year);
                }
            }, year,month,day);
            datePickerDialogDeath.show();
        }

        else if (view.getId() == etDeathDate.getId()){
            final Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialogBirth = new DatePickerDialog(
                    addGraveActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    etDeathDate.setText(day + "/" + (month+1) +"/" + year);
                }
            }, year,month,day);
            datePickerDialogBirth.show();
        }


        else if (view.getId() == btnSave.getId()){


            graveId = UUID.randomUUID().toString();
            Grave grave = new Grave(graveId, etGraveName.getText().toString(),
                    etBirthDate.getText().toString(), etDeathDate.getText().toString(),
                    etDescription.getText().toString(), 0.0, 0.0);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCustomTitle(messageTitle)
                    .setMessage("Please check the information again. Afterwards you will be able to add photos and location on the grave page")
                    .setPositiveButton("check", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            database.child("graves").child(grave.getGraveId()).setValue(grave);
                            Toast.makeText(getApplicationContext(),"נשמר",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();

        }
}

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

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