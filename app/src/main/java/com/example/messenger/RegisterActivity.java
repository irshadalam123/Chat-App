package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText email, userName, mobileNo, password;
    Button register_button;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    DatabaseReference db_ref;
    ProgressDialog loaderBar;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        userName = findViewById(R.id.user_name);
        email = findViewById(R.id.email);
        mobileNo = findViewById(R.id.mobile_number);
        password = findViewById(R.id.password);
        register_button = findViewById(R.id.register);
        loaderBar = new ProgressDialog(this);
        back_btn = findViewById(R.id.arrow_back_btn_register);

        database = FirebaseDatabase.getInstance();
        db_ref = database.getReference();
        fAuth = FirebaseAuth.getInstance();


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,Login.class));
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_email = email.getText().toString().trim();
                String user_pass = password.getText().toString().trim();
                final String mobile_no = mobileNo.getText().toString().trim();
                final String user_name = userName.getText().toString().trim();

                if(TextUtils.isEmpty(user_name)){
                    userName.setError("Please Enter Your Name");
                    return;
                }
                else if(TextUtils.isEmpty(mobile_no)){
                    mobileNo.setError("Please Enter Your Id");
                    return;
                }
                else if(TextUtils.isEmpty(user_email)){
                    email.setError("Please Enter Your Email");
                    return;
                }
                else if(TextUtils.isEmpty(user_pass)){
                    password.setError("Please Enter Your Password");
                    return;
                }
                else if(user_pass.length()<8){
                    password.setError("Password must be minimum 8 character");
                }
                else {
                    loaderBar.setTitle("Loading...");
                    loaderBar.show();

                    fAuth.createUserWithEmailAndPassword(user_email, user_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserId = fAuth.getCurrentUser().getUid();
                                db_ref.child("Users").child(currentUserId).child("Name").setValue(user_name);
                                db_ref.child("Users").child(currentUserId).child("Mobile_No").setValue(mobile_no);
                                db_ref.child("Users").child(currentUserId).child("Email").setValue(user_email);

                                loaderBar.dismiss();

                                Toast.makeText(getApplicationContext(), "Account created successfully..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login.class));

                            } else {

                                loaderBar.dismiss();

                                Toast.makeText(getApplicationContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
