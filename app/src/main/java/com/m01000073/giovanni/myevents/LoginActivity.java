package com.m01000073.giovanni.myevents;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button btnLogin;

    private Button btnRegistration;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Typeface myCustomFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        mEmail = (EditText) findViewById(R.id.emailText);
        mPassword = (EditText)findViewById(R.id.pwdText);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(myCustomFont);
        btnRegistration = (Button)findViewById(R.id.btnRegistration);
        btnRegistration.setTypeface(myCustomFont);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
                finish();
            }
        });
    }

    private void checkLogin() {

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        checkUserExist();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Login non riuscito", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (!TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Inserire password", Toast.LENGTH_SHORT).show();
        }else if(!TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Inserire email", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this, "Email e password vuote", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkUserExist() {
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.login_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            finish();
            System.exit(0);

        }

        return super.onOptionsItemSelected(item);
    }



}
