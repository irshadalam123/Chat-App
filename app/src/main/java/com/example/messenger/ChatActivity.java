package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {


//    private String msg_receiver_name;
//    private String receiver_profile_image;
    private TextView user_name, lastSeen;
    private CircleImageView user_profile_image, sendMessageButton;
    private ImageView sendFileButton;
    private ImageView back_btn, AudioCallBtn, VideoCallBtn;
    private EditText inputMessage;
    private RecyclerView userMessagesList;
    private String checker = "", myUrl = "";
    private Uri fileUri;
    private StorageTask uploadTask;

    private ProgressDialog loadingBar;


    private DatabaseReference receive_user_ref, rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String messageSenderId, messageReceiverId;
    private String saveCurrentTime, saveCurrentDate;
    private String calledBy = "";


    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReceiverId = getIntent().getExtras().get("visit_user_id").toString();

        receive_user_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(messageReceiverId);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        messageSenderId = mAuth.getCurrentUser().getUid();

        user_name = findViewById(R.id.chat_user_name);
        lastSeen = findViewById(R.id.last_seen);
        user_profile_image = findViewById(R.id.chat_user_profile_image);
        back_btn = findViewById(R.id.chat_back_btn);
        AudioCallBtn = findViewById(R.id.audio_call_btn);
        VideoCallBtn = findViewById(R.id.video_call_btn);
        inputMessage = findViewById(R.id.input_message);
        sendMessageButton = findViewById(R.id.send_message_btn);
        sendFileButton = findViewById(R.id.send_image_file_btn);
        userMessagesList = findViewById(R.id.chat_recycler);
        linearLayoutManager = new LinearLayoutManager(this);

        loadingBar = new ProgressDialog(this);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList.setAdapter(messagesAdapter);
        userMessagesList.setLayoutManager(linearLayoutManager);



        receive_user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Profile_Image")){
                    Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(user_profile_image);
                }
                user_name.setText(dataSnapshot.child("Name").getValue().toString());

                if(dataSnapshot.hasChild("User_State")){

                    String user_state = dataSnapshot.child("User_State").child("state").getValue().toString();
                    String user_state_date = dataSnapshot.child("User_State").child("date").getValue().toString();
                    String user_state_time = dataSnapshot.child("User_State").child("time").getValue().toString();


                    if(user_state.equals("offline")){
                        lastSeen.setText("last seen "+user_state_time+" "+user_state_date);

                    }
                    else{
                        lastSeen.setText("online");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] options = new CharSequence[]{
                    "Image",
                    "Camera",
                    "PDF File",
                    "Docs File"
                };

                AlertDialog.Builder fileBuilder = new AlertDialog.Builder(ChatActivity.this);
                fileBuilder.setTitle("Select File");
                fileBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, 438);
                        }
                        if(which == 1){
                            checker = "camera";
                        }
                        if(which == 2){
                            checker = "pdf";
                        }
                        if(which == 3){
                            checker = "docx";
                        }
                    }
                });

                fileBuilder.show();

            }
        });



//        VideoCallBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent videoCall = new Intent(ChatActivity.this, VideoCall.class);
//                videoCall.putExtra("chat_user_id", messageReceiverId);
//                startActivity(videoCall);
//            }
//        });
//


    }

    @Override
    protected void onStart() {
        super.onStart();

        checkForReceivingCall();



        if(currentUser != null){
            updateUserState("online");
        }


        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);

                messagesAdapter.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();

        if(currentUser != null){
            updateUserState("offline");
        }
    }

    private void updateUserState(String state){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMMM, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();

        onlineState.put("time", saveCurrentTime);
        onlineState.put("date", saveCurrentDate);
        onlineState.put("state", state);

        rootRef.child("Users").child(messageSenderId).child("User_State").updateChildren(onlineState);
    }


    private void sendMessage(){

        String messageText = inputMessage.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            inputMessage.setError("Please Write Message");
        }
        else {
            final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
            final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

            DatabaseReference userMsgKeyRef = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

            final String messagePushId = userMsgKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "Text");
            messageTextBody.put("from", messageSenderId);
            messageTextBody.put("to", messageReceiverId);
            messageTextBody.put("messageId", messagePushId);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);



            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }

                    inputMessage.setText("");

                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null){

            loadingBar.setTitle("Sending...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();

            if(!checker.equals("image")){

            }
            else if (checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderId + "/" + messageReceiverId;
                final String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSenderId;

                DatabaseReference userMsgKeyRef = rootRef.child("Messages").child(messageSenderId).child(messageReceiverId).push();

                final String messagePushId = userMsgKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushId + ".jpg");

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = task.getResult();

                            myUrl = downloadUri.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", "Image");
                            messageTextBody.put("from", messageSenderId);
                            messageTextBody.put("to", messageReceiverId);
                            messageTextBody.put("messageId", messagePushId);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);



                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushId, messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushId, messageTextBody);

                            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(),"message sent",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                    }

                                    inputMessage.setText("");

                                }
                            });

                        }
                    }
                });

            }
            else {
                loadingBar.dismiss();
                Toast.makeText(getApplicationContext(), "Image not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void checkForReceivingCall(){

        rootRef.child("Users").child(messageSenderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Ringing")){
                    calledBy = dataSnapshot.child("Ringing").child("ringing").getValue().toString();

                    Intent calling = new Intent(ChatActivity.this, VideoCall.class);
                    calling.putExtra("chat_user_id", calledBy);
                    startActivity(calling);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(),"error"+databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}