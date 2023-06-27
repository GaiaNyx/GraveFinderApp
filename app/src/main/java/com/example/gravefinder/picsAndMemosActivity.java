package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class picsAndMemosActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMenu;
    private RecyclerView rvGraveImgs;
    private RecyclerView rvGraveMemos;
    private Button btnAddPicsAndMemos;
    private MemoryAdapter memoryAdapter;
    private ImageAdapter imageAdapter;

    private DatabaseReference currentGrave;
    private FirebaseAuth mAuth;
    private String userID;

    private String id;
    private ArrayList<ImageMemo> images;
    private DatabaseReference database;
    private View.OnClickListener onImgClickListener;
    private ArrayList<PDFmemo> memories;
    private View.OnClickListener onMemoryClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_and_memos);

        rvGraveImgs = findViewById(R.id.rvGraveImgs);
        rvGraveMemos = findViewById(R.id.rvGraveMemos);
        rvGraveMemos.setHasFixedSize(true);
        rvGraveImgs.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvGraveImgs.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        rvGraveMemos.setLayoutManager(layoutManager2);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnAddPicsAndMemos = findViewById(R.id.btnAddPicsAndMemos);
        btnAddPicsAndMemos.setOnClickListener(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentUser.getUid();

        onImgClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                Uri uri = Uri.parse(images.get(position).getStorageFilePath());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(uri);
                startActivity(browserIntent);
            }
        };

        onMemoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                String path = memories.get(position).getStorageFilePath();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(path.toString()), "application/image");
                startActivity(browserIntent);
            }
        };

        Intent i = getIntent();
        Bundle b = i.getExtras();
        database = FirebaseDatabase.getInstance().getReference();
        id = (String) b.get("id");

        database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference currentGrave = database.child("graves").child(id);

        currentGrave.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("images")) {
                    database.child("graves").child(id).child("images")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    images = new ArrayList<ImageMemo>();
                                    for (DataSnapshot graveSnapshot : snapshot.getChildren()) {
                                        images.add(graveSnapshot.getValue(ImageMemo.class));
                                    }
                                    ImageAdapter imageAdapter = new ImageAdapter(images);
                                    rvGraveImgs.setAdapter(imageAdapter);
                                    imageAdapter.setOnItemClickListener(onImgClickListener);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                } else {
                    Toast.makeText(picsAndMemosActivity.this, "No photos have been added", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        currentGrave.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("memories")) {
                    database.child("graves").child(id).child("memories")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    memories = new ArrayList<PDFmemo>();
                                    for (DataSnapshot graveSnapshot : snapshot.getChildren()) {
                                        memories.add(graveSnapshot.getValue(PDFmemo.class));
                                    }
                                    MemoryAdapter memoryAdapter = new MemoryAdapter(memories);
                                    rvGraveMemos.setAdapter(memoryAdapter);
                                    memoryAdapter.setOnItemClickListener(onMemoryClickListener);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                } else {
                    Toast.makeText(picsAndMemosActivity.this, "No files have been added", Toast.LENGTH_SHORT).show();
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
        else if (view.getId() == btnAddPicsAndMemos.getId()){
            Intent i = new Intent(this, addPicsAndMemosActivity.class);
            i.putExtra("id", id);
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