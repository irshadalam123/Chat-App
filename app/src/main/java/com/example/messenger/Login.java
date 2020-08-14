package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    TextView userEmail, userPass;
    Button login, signUp;
    ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.signup);
        userEmail = findViewById(R.id.login_userId);
        userPass = findViewById(R.id.Login_password);
        login = findViewById(R.id.login_button);
        loader = new ProgressDialog(this);

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), RegisterActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String password = userPass.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Enter Email");
                }else if (TextUtils.isEmpty(password)){
                    userPass.setError("Enter your password");
                }else {

                    loader.setTitle("Loading...");
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loader.dismiss();
                                Toast.makeText(getApplicationContext(), "You have loged In successfully", Toast.LENGTH_SHORT).show();

                                Intent gotomain = new Intent(getApplicationContext(), MainActivity.class);
                                gotomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                gotomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(gotomain);
                            } else {
                                loader.dismiss();
                                Toast.makeText(getApplicationContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
