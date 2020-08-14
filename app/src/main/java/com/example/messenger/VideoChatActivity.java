//package com.example.messenger;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.opengl.GLSurfaceView;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.FrameLayout;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.opentok.android.OpentokError;
//import com.opentok.android.PublisherKit;
//import com.opentok.android.Session;
//import com.opentok.android.Session.Builder;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.opentok.android.Publisher;
//import com.opentok.android.Stream;
//import com.opentok.android.Subscriber;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import pub.devrel.easypermissions.AfterPermissionGranted;
//import pub.devrel.easypermissions.EasyPermissions;
//
//public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {
//
//
//    private static String API_Key = "46731902";
//    private static String SESSION_ID = "1_MX40NjczMTkwMn5-MTU4OTIzNzg5MzEyNn5CUEx2NDM2SzZSSVlaUExSZlpmalNpTFJ-fg";
//    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjczMTkwMiZzaWc9ZmJjYmQ2YmVkNTlkMTlkNDE5OGFjNDJmYTE2MmI5N2ZhMGIxMTllZDpzZXNzaW9uX2lkPTFfTVg0ME5qY3pNVGt3TW41LU1UVTRPVEl6TnpnNU16RXlObjVDVUV4Mk5ETTJTelpTU1ZsYVVFeFNabHBtYWxOcFRGSi1mZyZjcmVhdGVfdGltZT0xNTg5MjM3OTg2Jm5vbmNlPTAuNzg5MjQyNjMzOTIwNjI4OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTkxODI5OTg0JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
//    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
//    private static final int RC_VIDEO_APP_PERM = 124;
//
//    private FrameLayout mPublisherViewController;
//    private FrameLayout mSubscriberViewController;
//    private Session mSession;
//    private Publisher mPublisher;
//    private Subscriber mSubscriber;
//
//    private CircleImageView closeVideoChatBtn;
//
//    private DatabaseReference userRef;
//    private String userId;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_chat);
//
//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
//
//        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
//
//        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(userId).hasChild("Ringing")){
//
//                            final String callerId = dataSnapshot.child(userId).child("Ringing").child("ringing").getValue().toString();
//
//                            userRef.child(userId).child("Ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        userRef.child(callerId).child("Calling").removeValue();
//                                    }
//                                }
//                            });
//                            if (mPublisher != null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber != null){
//                                mSubscriber.destroy();
//                            }
//
//                            startActivity(new Intent(VideoChatActivity.this, Login.class));
//                            finish();
//                        }
//
//                        else if (dataSnapshot.child(userId).hasChild("Calling")){
//
//                            final String callReceiverId = dataSnapshot.child(userId).child("Calling").child("calling").getValue().toString();
//
//                            userRef.child(userId).child("Calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        userRef.child(callReceiverId).child("Ringing").removeValue();
//                                    }
//                                }
//                            });
//
//                            if (mPublisher != null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber != null){
//                                mSubscriber.destroy();
//                            }
//
//                            startActivity(new Intent(VideoChatActivity.this, Login.class));
//                            finish();
//                        }
//
//                        else {
//
//                            if (mPublisher != null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber != null){
//                                mSubscriber.destroy();
//                            }
//
//                            startActivity(new Intent(VideoChatActivity.this, Login.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//
//
//        requestPermissions();
//    }
//
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        userRef.child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild("Ringing") | dataSnapshot.hasChild("Calling")){
//
//                }
//                else {
//                    if (mPublisher != null){
//                        mPublisher.destroy();
//                    }
//                    if (mSubscriber != null){
//                        mSubscriber.destroy();
//                    }
//                    startActivity(new Intent(VideoChatActivity.this, Login.class));
//                    finish();
//                }
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
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);
//    }
//
//
//    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
//    private void requestPermissions(){
//        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
//
//        if(EasyPermissions.hasPermissions(this, perms)){
//            mPublisherViewController = findViewById(R.id.publisher_container);
//            mSubscriberViewController = findViewById(R.id.subscriber_container);
//
//
////            1. Initialize and connect to the session
//
//            mSession = new Session.Builder(this, API_Key, SESSION_ID).build();
//            mSession.setSessionListener(VideoChatActivity.this);
//            mSession.connect(TOKEN);
//        }
//        else {
//            EasyPermissions.requestPermissions(this, "You need to allow Camera and Mic permission. Pleas allow.", RC_VIDEO_APP_PERM);
//        }
//    }
//
//    @Override
//    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
//
//    }
//
//    @Override
//    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
//
//    }
//
//    @Override
//    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
//
//    }
//
////    2. publishing stream to the session
//    @Override
//    public void onConnected(Session session) {
//        Log.i(LOG_TAG, "Session Connected");
//
//        mPublisher = new Publisher.Builder(this).build();
//        mPublisher.setPublisherListener(VideoChatActivity.this);
//
//        mPublisherViewController.addView(mPublisher.getView());
//
//        if(mPublisher.getView() instanceof GLSurfaceView){
//            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
//        }
//
//        mSession.publish(mPublisher);
//    }
//
//    @Override
//    public void onDisconnected(Session session) {
//        Log.i(LOG_TAG, "Stream Disconnected");
//    }
//
////    3. subscribing to the stream
//
//    @Override
//    public void onStreamReceived(Session session, Stream stream) {
//
//        Log.i(LOG_TAG,"Stream received");
//
//        if(mSubscriber == null){
//            mSubscriber = new Subscriber.Builder(this, stream).build();
//            mSession.subscribe(mSubscriber);
//            mSubscriberViewController.addView(mSubscriber.getView());
//        }
//
//    }
//
//    @Override
//    public void onStreamDropped(Session session, Stream stream) {
//
//        Log.i(LOG_TAG, "Stream Dropped");
//
//        if (mSubscriber != null){
//            mSubscriber = null;
//            mSubscriberViewController.removeAllViews();
//        }
//
//    }
//
//    @Override
//    public void onError(Session session, OpentokError opentokError) {
//        Log.i(LOG_TAG, "Stream Error");
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
//}
