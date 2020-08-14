package com.example.messenger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;



    public MessagesAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText, receiverMessageTime, senderMessageTime, receiverImageReceiveTime, senderImageSendTime;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.receiver_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            receiverMessageTime = itemView.findViewById(R.id.receiver_message_time);
            senderMessageTime = itemView.findViewById(R.id.sender_message_time);
            receiverImageReceiveTime = itemView.findViewById(R.id.receiver_image_receive_time);
            senderImageSendTime = itemView.findViewById(R.id.sender_image_send_time);

        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);

        mAuth = FirebaseAuth.getInstance();



        return new MessageViewHolder(view);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String messageSenderId = mAuth.getCurrentUser().getUid();

        final Messages messages = userMessagesList.get(position);

        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("Profile_Image")) {
                    String receiverProfileImg = dataSnapshot.child("Profile_Image").getValue().toString();
                    Picasso.get().load(receiverProfileImg).placeholder(R.drawable.profile).into(holder.receiverProfileImage);
                }
                else {
                    Picasso.get().load("dskfjgnsld").placeholder(R.drawable.profile).into(holder.receiverProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.senderMessageTime.setVisibility(View.GONE);
        holder.receiverMessageTime.setVisibility(View.GONE);
        holder.senderImageSendTime.setVisibility(View.GONE);
        holder.receiverImageReceiveTime.setVisibility(View.GONE);


        if(fromMessageType.equals("Text")){

            if(fromUserId.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderMessageTime.setVisibility(View.VISIBLE);
                holder.senderMessageTime.setText(messages.getTime());
            }
            else {

                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.receiverMessageTime.setVisibility(View.VISIBLE);
                holder.receiverMessageTime.setText(messages.getTime());
            }
        }
        else if (fromMessageType.equals("Image")){

            if (fromUserId.equals(messageSenderId)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                holder.senderImageSendTime.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
                holder.senderImageSendTime.setText(messages.getTime());
            }
            else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
                holder.receiverImageReceiveTime.setVisibility(View.VISIBLE);
                holder.receiverImageReceiveTime.setText(messages.getTime());
            }
        }


        holder.senderMessageText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Action...");
                menu.add("Delete for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                        msgRef.child(messages.getFrom()).child(messages.getTo()).child(messages.getMessageId()).removeValue();

                        return true;
                    }
                });
                menu.add("Delete for everyone").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                        msgRef.child(messages.getFrom()).child(messages.getTo()).child(messages.getMessageId()).removeValue();

                        msgRef.child(messages.getTo()).child(messages.getFrom()).child(messages.getMessageId()).removeValue();

                        return true;
                    }
                });
                menu.add("Clear all messages for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                        msgRef.child(messages.getFrom()).child(messages.getTo()).removeValue();

                        return true;
                    }
                });

            }
        });

        holder.messageSenderPicture.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Action...");
                menu.add("Delete for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });

                menu.add("Delete for everyone").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });
            }
        });


//        holder.messageSenderPicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog = new Dialog(context, R.style.FullScreenDialog);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//                dialog.setContentView(R.layout.image_dialog);
//
//                final ImageView profileImage = dialog.findViewById(R.id.profile_image_dialog);
//                Picasso.get().load(messages.getMessage()).into(profileImage);
//                dialog.show();
//            }
//        });


        holder.receiverMessageText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Action...");
                menu.add("Delete for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                        msgRef.child(messages.getTo()).child(messages.getFrom()).child(messages.getMessageId()).removeValue();

                        return true;
                    }
                });

                menu.add("Clear all messages for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        DatabaseReference msgRef = FirebaseDatabase.getInstance().getReference().child("Messages");
                        msgRef.child(messages.getTo()).child(messages.getFrom()).removeValue();

                        return true;
                    }
                });

            }
        });

        holder.messageReceiverPicture.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Action...");
                menu.add("Delete for me").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                });

            }
        });





    }



    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
