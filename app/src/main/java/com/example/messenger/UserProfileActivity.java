package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String receive_userId, current_state, senderUserId;

    private TextView user_name;
    private CircleImageView profile_image;
    private Button req_btn, decline_req_btn;
    private ProgressDialog loaderBar;
    private Toolbar toolbar;

    private DatabaseReference userRef, chatReqSendRef, chatReqReceiveRef, contact_ref;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatReqSendRef = FirebaseDatabase.getInstance().getReference().child("Request_Sent");
        chatReqReceiveRef = FirebaseDatabase.getInstance().getReference().child("Request_Receive");

        contact_ref = FirebaseDatabase.getInstance().getReference().child("Contact");
        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();

        user_name = findViewById(R.id.new_User_name);
        req_btn = findViewById(R.id.req_msg_btn);
        decline_req_btn = findViewById(R.id.decline_msg_btn);
        profile_image = findViewById(R.id.user_profile);

        toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loaderBar = new ProgressDialog(this);
        loaderBar.setCanceledOnTouchOutside(false);
        loaderBar.setTitle("Loading...");

        current_state = "new";
        senderUserId = mAuth.getCurrentUser().getUid();

        receive_userId = getIntent().getExtras().get("visit_user_id").toString();

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {



        userRef.child(receive_userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("Name").getValue().toString();
                    if(dataSnapshot.hasChild("Profile_Image")) {
                        String profileImage = dataSnapshot.child("Profile_Image").getValue().toString();
//                        Glide.with(getApplicationContext()).load(profileImage).placeholder(R.drawable.profile).into(profile_image);
                        Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(profile_image);
                    }
                    user_name.setText(name);

                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public  void manageChatRequest(){

        chatReqReceiveRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receive_userId)){
                    current_state = "request_received";
                    req_btn.setText("Accept Chat Request");

                    decline_req_btn.setVisibility(View.VISIBLE);
                    decline_req_btn.setEnabled(true);

                    decline_req_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelChatRequest();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        contact_ref.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receive_userId)){
                    current_state = "friends";
                    req_btn.setText("Remove Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatReqSendRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receive_userId)){
                    current_state = "request_sent";
                    req_btn.setText("Cancel Chat Request");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!senderUserId.equals(receive_userId)){
            req_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    req_btn.setEnabled(false);
                    if(current_state.equals("new")){
                        sendChatRequest();
                    }
                    if(current_state.equals("request_sent")){
                        cancelChatRequest();
                    }
                    if(current_state.equals("request_received")){
                        acceptChatRequest();
                    }
                    if(current_state.equals("friends")){
                        removeFriend();
                    }
                }
            });
        }
        else{
            req_btn.setVisibility(View.INVISIBLE);
//            startActivity(new Intent(UserProfileActivity.this, SettingActivity.class));
        }

    }

    public void sendChatRequest(){

        loaderBar.show();

        chatReqSendRef.child(senderUserId).child(receive_userId).setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatReqReceiveRef.child(receive_userId).child(senderUserId).setValue("Receive").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loaderBar.dismiss();

                                req_btn.setEnabled(true);
                                current_state = "request_sent";
                                req_btn.setText("Cancel Chat Request");
                            }
                        }
                    });
                }
            }
        });

    }

    public void cancelChatRequest(){

        loaderBar.show();

        chatReqSendRef.child(senderUserId).child(receive_userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            chatReqReceiveRef.child(receive_userId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                loaderBar.dismiss();

                                                req_btn.setEnabled(true);
                                                current_state = "new";
                                                req_btn.setText("Send Message Request");
                                                decline_req_btn.setVisibility(View.INVISIBLE);
                                                decline_req_btn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    public void acceptChatRequest(){

        contact_ref.child(senderUserId).child(receive_userId).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            contact_ref.child(receive_userId).child(senderUserId).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                chatReqReceiveRef.child(senderUserId).child(receive_userId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    chatReqSendRef.child(receive_userId).child(senderUserId).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    req_btn.setEnabled(true);
                                                                                    current_state = "friends";
                                                                                    req_btn.setText("Remove Friends");
                                                                                    decline_req_btn.setVisibility(View.INVISIBLE);
                                                                                }
                                                                            });

                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    public void removeFriend(){

        contact_ref.child(senderUserId).child(receive_userId).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contact_ref.child(receive_userId).child(senderUserId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                loaderBar.dismiss();

                                                req_btn.setEnabled(true);
                                                current_state = "new";
                                                req_btn.setText("Send Message Request");
                                                decline_req_btn.setVisibility(View.INVISIBLE);
                                                decline_req_btn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

}
