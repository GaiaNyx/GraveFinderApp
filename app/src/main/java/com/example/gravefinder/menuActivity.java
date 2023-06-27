package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class menuActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSearch;
    private Button btnMyGraves;
    private Button btnAddGrave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        btnMyGraves = findViewById(R.id.btnMyGraves);
        btnMyGraves.setOnClickListener(this);

        btnAddGrave = findViewById(R.id.btnAddGrave);
        btnAddGrave.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == btnSearch.getId()){
            Intent i = new Intent(this, searchActivity.class);
            startActivity(i);
        }

        else if (view.getId() == btnMyGraves.getId()){
            Intent i = new Intent(this, myGravesActivity.class);
            startActivity(i);
        }

        else if (view.getId() == btnAddGrave.getId()){
            Intent i = new Intent(this, addGraveActivity.class);
            startActivity(i);
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