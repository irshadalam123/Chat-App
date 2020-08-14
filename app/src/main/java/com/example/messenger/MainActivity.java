package com.example.messenger;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference chatRef, userRef;
    private String currentUserId;
    private FirebaseUser currentUser;


    private FloatingActionButton friends_contact;
    private Toolbar toolbar;
    private RecyclerView chatList;
    private Dialog dialog;


    String Friends_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = mAuth.getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat List");

        dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.image_dialog);

        chatList = findViewById(R.id.chat_list);


//        friends_contact = findViewById(R.id.add_friends);
//        friends_contact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, FriendsContacts.class));
//            }
//        });

    }


    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(chatRef, new SnapshotParser<Contacts>() {
            @NonNull
            @Override
            public Contacts parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Contacts();
            }
        }).build();


        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {

                final String userId = getRef(position).getKey();
//                final String[] profile_image = {"default_image"};

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        final String userName  = dataSnapshot.child("Name").getValue().toString();
                        holder.user_name.setText(userName);

                        if(dataSnapshot.hasChild("User_State")){
                            String userState = dataSnapshot.child("User_State").child("state").getValue().toString();
                            if(userState.equals("online")){
                                holder.onlineShowButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.onlineShowButton.setVisibility(View.INVISIBLE);
                            }
                        }

                        if(dataSnapshot.hasChild("Profile_Image")){
                            String profile_image = dataSnapshot.child("Profile_Image").getValue().toString();
//                            Glide.with(getApplicationContext()).load(profile_image[0]).into(holder.user_profile_img);
                            Picasso.get().load(profile_image).placeholder(R.drawable.profile).into(holder.user_profile_img);
                        }

                        holder.user_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", userId);
                                startActivity(chatIntent);
                            }
                        });

                        holder.user_profile_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final ImageView profileImage = dialog.findViewById(R.id.profile_image_dialog);
                                if(dataSnapshot.hasChild("Profile_Image")){
                                    Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(profileImage);
                                }
                                else {
                                    Picasso.get().load("afdgldfkg").placeholder(R.drawable.profile).into(profileImage);
                                }
                                dialog.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View myView = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                return new ChatViewHolder(myView);
            }
        };

        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        chatList.setItemAnimator(new DefaultItemAnimator());
        adapter.startListening();


    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CircleImageView user_profile_img;
        ImageView onlineShowButton;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.friends_name);
            user_profile_img = itemView.findViewById(R.id.users_profile_image);
            onlineShowButton = itemView.findViewById(R.id.request_show_button);

        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
//            case R.id.search:
//                Toast.makeText(getApplicationContext(),"Hello",Toast.LENGTH_LONG).show();
//                return true;
            case R.id.setting:
                Intent intent_Sett = new Intent(getApplicationContext(), SettingActivity.class);
                intent_Sett.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_Sett.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_Sett);
                return true;
            case R.id.find_friends:
                Intent intent_frnds = new Intent(getApplicationContext(), FindFriendsActivity.class);
                intent_frnds.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_frnds.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_frnds);
                return true;
            case R.id.request_of_friends:
                Intent intent_req = new Intent(getApplicationContext(), RequestFriends.class);
                intent_req.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_req.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_req);
                return true;
            case R.id.logout:
                mAuth.signOut();
                Intent intent_logout = new Intent(getBaseContext(), Login.class);
                intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent_logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_logout);
                return true;
            default:
                return true;
        }

    }
}
