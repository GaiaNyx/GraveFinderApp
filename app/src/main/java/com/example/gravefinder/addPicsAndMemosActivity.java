package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addPicsAndMemosActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMenu;
    private TextView tvGraveName;
    private Button btnAddPics;
    private Button btnCameraPics;
    private Button btnAddMemos;

    private String id;
    private DatabaseReference database;

    private FirebaseUser currentUser;
    private Grave currentGrave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pics_and_memos);

        tvGraveName = findViewById(R.id.tvGraveName);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnAddPics = findViewById(R.id.btnAddPics);
        btnAddPics.setOnClickListener(this);

        btnCameraPics = findViewById(R.id.btnCameraPics);
        btnCameraPics.setOnClickListener(this);

        btnAddMemos = findViewById(R.id.btnAddMemos);
        btnAddMemos.setOnClickListener(this);


        //extracting id from intent (
        //        Intent i = getIntent();
        //        Bundle b = i.getExtras();
        //        Bundle --> a class in android which is used to pass data from one activity to
        //        another activity within an android application
        //        )
        database = FirebaseDatabase.getInstance().getReference();
        id = (String) getIntent().getExtras().get("id");
        database.child("graves").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGrave = snapshot.getValue(Grave.class);
                tvGraveName.setText(currentGrave.getGraveName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnMenu.getId()){
            Intent i = new Intent(this, menuActivity.class);
            startActivity(i);
        }

        else if (view.getId() == R.id.btnAddPics){
            Intent i = new Intent(this, uploadPhotoActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
        else if (view.getId() == R.id.btnAddMemos){
            Intent i = new Intent(this, uploadPdfActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }

        else if (view.getId() == R.id.btnCameraPics)
        {
            Intent i = new Intent(this, takePictureActivity.class);
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