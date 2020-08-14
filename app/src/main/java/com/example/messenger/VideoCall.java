//package com.example.messenger;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
////import android.media.Image;
////import android.media.MediaPlayer;
//import android.os.Bundle;
////import android.provider.ContactsContract;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.HashMap;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class VideoCall extends AppCompatActivity {
//
//
//    private ImageView userProfileImage;
//    private TextView userName;
//    private CircleImageView acceptCallButton, cancelCallButton;
//
//
//    private String receiveUserId = "", receiveUserName = "", receiveUserImage = "";
//    private String senderUserId = "", senderUserName = "", senderUserImage = "", checker = "";
//    private String callingId = "", ringingId = "";
//
////    private MediaPlayer mediaPlayer;
//
//    private DatabaseReference userRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_call);
//
//
//        receiveUserId = getIntent().getExtras().get("chat_user_id").toString();
//
//        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
//
////        mediaPlayer = MediaPlayer.create(this, R.raw.rington);
//
//
//        userProfileImage = findViewById(R.id.calling_profile_image);
//        userName = findViewById(R.id.calling_user_name);
//        acceptCallButton = findViewById(R.id.make_call_btn);
//        cancelCallButton = findViewById(R.id.cancel_call);
//
//
//        cancelCallButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mediaPlayer.stop();
//                checker = "clicked";
//                cancelCallingUser();
//            }
//        });
//
//
//        acceptCallButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                mediaPlayer.stop();
//
//                final HashMap<String, Object> callingPickupMap = new HashMap<>();
//                callingPickupMap.put("picked", "picked");
//
//                userRef.child(senderUserId).child("Ringing").updateChildren(callingPickupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                            Intent intent = new Intent(VideoCall.this, VideoChatActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//                });
//
//            }
//        });
//
//
//        getAndSetUserProfileInfo();
//
//    }
//
//
//
//    public void getAndSetUserProfileInfo() {
//
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.child(receiveUserId).hasChild("Profile_Image")){
//                    receiveUserImage = dataSnapshot.child(receiveUserId).child("Profile_Image").getValue().toString();
//                    Picasso.get().load(receiveUserImage).placeholder(R.drawable.profile).into(userProfileImage);
//                }
//
//                receiveUserName = dataSnapshot.child(receiveUserId).child("Name").getValue().toString();
//                userName.setText(receiveUserName);
//
////                if(dataSnapshot.child(senderUserId).hasChild("Profile_Image")){
////                    senderUserImage = dataSnapshot.child(senderUserId).child("Profile_Image").getValue().toString();
////                }
////                senderUserName = dataSnapshot.child(senderUserId).child("Name").getValue().toString();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
////        mediaPlayer.start();
//
//        userRef.child(receiveUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(!checker.equals("clicked") && !dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing")){
//
//
//
//                    final HashMap<String, Object> callingInfo = new HashMap<>();
//                    callingInfo.put("calling", receiveUserId);
//
//                    userRef.child(senderUserId).child("Calling").updateChildren(callingInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//
//                                final HashMap<String, Object> ringingInfo = new HashMap<>();
//                                ringingInfo.put("ringing", senderUserId);
//
//                                userRef.child(receiveUserId).child("Ringing").updateChildren(ringingInfo);
//                            }
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.child(senderUserId).hasChild("Ringing") && !dataSnapshot.child(senderUserId).hasChild("Calling")){
//
//                    acceptCallButton.setVisibility(View.VISIBLE);
//
//                }
//
//                if(dataSnapshot.child(receiveUserId).child("Ringing").hasChild("picked")){
//
////                    mediaPlayer.stop();
//                    startActivity(new Intent(VideoCall.this, VideoChatActivity.class));
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    private void cancelCallingUser(){
//
////        from sender side
//
//        userRef.child(senderUserId).child("Calling").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists() && dataSnapshot.hasChild("calling")){
//                    callingId = dataSnapshot.child("calling").getValue().toString();
//
//                    userRef.child(callingId).child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                userRef.child(senderUserId).child("Calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        startActivity(new Intent(VideoCall.this, Login.class));
//                                        finish();
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
////                else {
////                    startActivity(new Intent(VideoCall.this, Login.class));
////                    finish();
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
////        from receiver side
//
//        userRef.child(senderUserId).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists() && dataSnapshot.hasChild("ringing")){
//                    ringingId = dataSnapshot.child("ringing").getValue().toString();
//
//                    userRef.child(ringingId).child("Calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                userRef.child(senderUserId).child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        startActivity(new Intent(VideoCall.this, Login.class));
//                                        finish();
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
////                else {
////                    startActivity(new Intent(VideoCall.this, Login.class));
////                    finish();
////                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//}
