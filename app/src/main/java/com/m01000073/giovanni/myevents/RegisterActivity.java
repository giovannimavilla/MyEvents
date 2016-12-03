package com.m01000073.giovanni.myevents;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegisterBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference rootRef;

    private DatabaseReference current_user_db;

    private ProgressDialog mProgress;

    private int result;
    private String name;
    private String email;
    private String pwd;
    private boolean control = false;

    private Typeface myCustomFont;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myCustomFont = Typeface.createFromAsset(getAssets(), "font/Insignia.ttf");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        rootRef = FirebaseDatabase.getInstance().getReference().child("Usernames");

        mProgress = new ProgressDialog(this);

        mName = (EditText) findViewById(R.id.nameField);
        mEmail = (EditText) findViewById(R.id.emailField);
        mPassword = (EditText) findViewById(R.id.pwdField);
        mRegisterBtn = (Button)findViewById(R.id.registerBtn);
        mRegisterBtn.setTypeface(myCustomFont);


    }

    private void startControlName() {
        name = mName.getText().toString();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(name)){
                    result = 1;
                    Log.d("startControl", "return 1");
                }else{
                    result = 0;
                    Log.d("startControl", "return 0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("startControl", "return ERRROR");
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();



        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = mName.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                pwd = mPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)){

                    mProgress.setMessage("Registrazione....");
                    mProgress.show();

                    startControlName();
                    startDialogPost();
                    mProgress.dismiss();

                }else {
                    Toast.makeText(RegisterActivity.this, "Campi vuoti! ", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private void startDialogPost() {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.getWindow();
        dialog.setTitle("Registrazione");
        dialog.setContentView(R.layout.register_dialog);

        TextView n = (TextView)dialog.findViewById(R.id.textName);
        n.setText(name);
        TextView e = (TextView)dialog.findViewById(R.id.textEmail);
        e.setText(email);

        dialog.show();

        ImageView imgclose = (ImageView)dialog.findViewById(R.id.CloseDialogImageView);
        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Button btnOK = (Button)dialog.findViewById(R.id.btnOK);
        btnOK.setTypeface(myCustomFont);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startControlName();
                dialog.dismiss();
                if(result==0) {
                    (mAuth.createUserWithEmailAndPassword(email, pwd)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();

                                current_user_db = mDatabase.child(user_id);

                                current_user_db.child("name").setValue(name);
                                current_user_db.child("età").setValue("");
                                current_user_db.child("telefono").setValue("");
                                current_user_db.child("image").setValue("default");

                                current_user_db.child("notification").child("new message").setValue(0);

                                rootRef.child(name).setValue(name);


                                mProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    Toast.makeText(RegisterActivity.this, "Email già in uso", Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this, "Username non disponibile", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.back_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
