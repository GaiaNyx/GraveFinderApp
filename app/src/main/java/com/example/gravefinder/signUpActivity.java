package com.example.gravefinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class signUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etEmail;
    private EditText etPhone;
    private EditText etName;
    private EditText etPassword;
    private Button btnSignUp;
    private TextView messageTitle;

    private DatabaseReference database;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        TextView messageTitle = new TextView(this);
        messageTitle.setText("הרשמה?");
        messageTitle.setGravity(Gravity.RIGHT);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == btnSignUp.getId()){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setCustomTitle(messageTitle)
                    .setMessage("Please check your personal information before registering")
                    .setPositiveButton("check", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("register", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            mAuth.createUserWithEmailAndPassword(
                                    etEmail.getText().toString(), etPassword.getText().toString())
                                    .addOnCompleteListener(signUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Register", "Success");
                                                ArrayList<Grave> myGraves = new ArrayList<>();
                                                String id = etEmail.getText().toString();
                                                User user = new User(mAuth.getCurrentUser().getUid(), etEmail.getText().toString(),
                                                        etPhone.getText().toString(), etName.getText().toString(),
                                                        etPassword.getText().toString(), myGraves);
                                                database.child("users").child(user.getUserId()).setValue(user);
                                                startActivity(new Intent(signUpActivity.this, menuActivity.class));
                                            }
                                            else {
                                                Toast.makeText(signUpActivity.this, "הרשמה נכשלה", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .show();
        }


    }
}