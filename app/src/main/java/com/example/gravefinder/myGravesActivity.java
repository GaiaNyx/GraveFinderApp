package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class myGravesActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvGraves;
    private Button btnMenu;
    private ArrayList<Grave> graves;
    private View.OnClickListener onItemClickListener;
    private DatabaseReference database;
    private Grave currentGrave;
    private String graveID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_graves);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        onItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getPosition();
                Grave graveItem = graves.get(position);
                Intent i = new Intent(myGravesActivity.this, gravePageActivity.class);
                i.putExtra("id", graveItem.getGraveId());
                startActivity(i);
            }
        };

        rvGraves = findViewById(R.id.rvGraves);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvGraves.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference currentUserData = database.child("users").child(currentUser.getUid());

        currentUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("myGraves")) {
                    database.child("users").child(currentUser.getUid()).child("myGraves")
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            graves = new ArrayList<Grave>();
                            for(DataSnapshot graveSnapshot: snapshot.getChildren()){
                                graves.add(graveSnapshot.getValue(Grave.class));
                            }
                            GraveAdapter graveAdapter = new GraveAdapter(graves);
                            rvGraves.setAdapter(graveAdapter);
                            graveAdapter.setOnItemClickListener(onItemClickListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    Toast.makeText(myGravesActivity.this, "No graves have been chosen", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()){
            Intent i = new Intent(this, menuActivity.class);
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