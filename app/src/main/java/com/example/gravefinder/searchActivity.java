package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class searchActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnMenu;
    private DatabaseReference database;
    private RecyclerView rvGraves;
    private SearchView searchView;
    private ArrayList<Grave> graves;
    private ArrayList<Grave> filteredList;
    private View.OnClickListener onItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        btnMenu = (Button) findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                Grave graveItem = filteredList.get(position);
                Intent i = new Intent(searchActivity.this, gravePageActivity.class);
                i.putExtra("id", graveItem.getGraveId());
                startActivity(i);
            }
        };


        rvGraves = findViewById(R.id.rvGraves);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvGraves.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance().getReference();
        database.child("graves").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                graves = new ArrayList<Grave>();
                filteredList = new ArrayList<Grave>();
                for(DataSnapshot graveSnapshot: snapshot.getChildren()){
                    graves.add(graveSnapshot.getValue(Grave.class));
                    filteredList.add(graveSnapshot.getValue(Grave.class));
                }
                GraveAdapter filteredGraveAdapter = new GraveAdapter(filteredList);
                rvGraves.setAdapter(filteredGraveAdapter);
                filteredGraveAdapter.setOnItemClickListener(onItemClickListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList = new ArrayList<Grave>();
                for(Grave searchedGrave : graves){
                    if(searchedGrave.getGraveName().toLowerCase().contains(newText.toLowerCase())){
                        filteredList.add(searchedGrave);
                    }
                }
                GraveAdapter filteredGraveAdapter = new GraveAdapter(filteredList);
                rvGraves.setAdapter(filteredGraveAdapter);
                filteredGraveAdapter.setOnItemClickListener(onItemClickListener);
                return false;
            }

        });

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()){
            Intent i = new Intent(searchActivity.this, menuActivity.class);
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