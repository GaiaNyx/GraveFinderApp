package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

public class uploadPdfActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnMenu;
    private TextView tvGraveName;
    private Button btnAddMemos;
    private Button btnSave;
    private TextView messageTitle;
    private EditText etPDFname;

    private String id;
    private DatabaseReference database;
    private String storageFilePath;


    private static final int PICK_PDF_FILE = 2;


    private StorageReference storageRef;
    private UploadTask uploadTask;
    private Uri downloadUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        tvGraveName = findViewById(R.id.tvGraveName);

        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this);

        btnAddMemos = findViewById(R.id.btnAddMemos);
        btnAddMemos.setOnClickListener(this);

        etPDFname = findViewById(R.id.etPDFname);
        etPDFname.setOnClickListener(this);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        TextView messageTitle = new TextView(this);
        messageTitle.setText("לשמור?");
        messageTitle.setGravity(Gravity.RIGHT);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        database = FirebaseDatabase.getInstance().getReference();
        id = (String) b.get("id");
        database.child("graves").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvGraveName.setText(snapshot.child("graveName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageRef = FirebaseStorage.getInstance().getReference();



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
                    .setMessage("Save the file?")
                    .setPositiveButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Date date = new Date();
                            long timeMilli = date.getTime();
                            PDFmemo memory = new PDFmemo(String.valueOf(timeMilli), etPDFname.getText().toString(),
                                    storageFilePath);
                            if(etPDFname.getText() == null || etPDFname.getText().equals("")) {
                                Toast.makeText(getApplicationContext(),"Add PDF file name",Toast.LENGTH_LONG).show();
                            }
                            else{
                                database.child("graves").child(id).child("memories")
                                        .child(String.valueOf(timeMilli)).setValue(memory);
                                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .show();
        }
        else if (view.getId() == R.id.btnAddMemos){

            openFile();

        }

    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                uploadFile(uri, ".pdf" );
            }

        }
    }


    private void openFile() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, PICK_PDF_FILE);
    }

    private void uploadFile(Uri uri, String suffix)
    {

        StorageReference riversRef = null;
        if (suffix.equals(".jpg"))
            riversRef = storageRef.child("images/"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + suffix);
        else if (suffix.equals(".pdf"))
            riversRef = storageRef.child("files/"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + suffix);
        UploadTask uploadTask = riversRef.putFile(uri);
        final StorageReference finalRiversRef = riversRef;
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return finalRiversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    storageFilePath = (downloadUri.toString());
                } else {
                }
            }
        });
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