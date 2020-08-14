package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {


    private EditText searchFriend;
    private Button searchButton;
    private TextView searchFriendName;
    private CircleImageView searchFriendProImg;
    private String user_id;
    private LinearLayout friendLayout;

    private Toolbar toolbar;
    private String current_user_id;
    private RecyclerView recyclerView;
    private DatabaseReference userRef;
    private FirebaseAuth mauth;
    private ProgressDialog loaderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        searchFriend = findViewById(R.id.search_friend_text_view);
        searchButton = findViewById(R.id.search_friend_button);
        searchFriendName = findViewById(R.id.search_friend_name);
        searchFriendProImg = findViewById(R.id.search_friend_profile_image);
        friendLayout = findViewById(R.id.search_friend_layout);

        mauth = FirebaseAuth.getInstance();
        current_user_id = mauth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

//        recyclerView = findViewById(R.id.friends_recycler_view);

        toolbar = findViewById(R.id.friends_toolbar);

        loaderBar = new ProgressDialog(this);
        loaderBar.setTitle("Loading...");
        loaderBar.setCanceledOnTouchOutside(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = searchFriend.getText().toString();

                loaderBar.show();

                userRef.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            loaderBar.dismiss();
                            friendLayout.setVisibility(View.VISIBLE);
                            if (dataSnapshot.hasChild("Profile_Image")){

                                Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).into(searchFriendProImg);
                            }
                            searchFriendName.setText(dataSnapshot.child("Name").getValue().toString());
                        }
                        else {
                            loaderBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Friend Not Found!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        searchFriendName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user_id = searchFriend.getText().toString();

                Intent Profile_intent = new Intent(FindFriendsActivity.this, UserProfileActivity.class);
                Profile_intent.putExtra("visit_user_id", user_id);
                startActivity(Profile_intent);

            }
        });





    }



//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        loaderBar.show();
//
////        Query query = userRef;
//
//        FirebaseRecyclerOptions<Contacts> options =
//                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userRef, new SnapshotParser<Contacts>() {
//                    @NonNull
//                    @Override
//                    public Contacts parseSnapshot(@NonNull DataSnapshot snapshot) {
//
//                        Contacts contacts =  new Contacts();
//                        return contacts;
//                    }
//                }).build();
//
//        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
//
//                    @Override
//                    protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int position, @NonNull Contacts model) {
//
//                        loaderBar.dismiss();
//
//                        final String frnds_Id = getRef(position).getKey();
//
//
//                        userRef.child(frnds_Id).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                holder.user_name.setText(dataSnapshot.child("Name").getValue().toString());
//
//                                if(dataSnapshot.hasChild("Profile_Image")){
//                                    Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(holder.user_profile_img);
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//
//
//                        holder.user_name.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                final String visit_user_id = getRef(position).getKey();
//                                Intent chat_intent = new Intent(FindFriendsActivity.this, UserProfileActivity.class);
//                                chat_intent.putExtra("visit_user_id", visit_user_id);
//                                startActivity(chat_intent);
//                            }
//                        });
//                    }
//
//
//                    @NonNull
//                    @Override
//                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                        loaderBar.dismiss();
//
//                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
//                        FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
//                        return viewHolder;
//                    }
//
//
//                };
//
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(FindFriendsActivity.this));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        adapter.startListening();
//
//    }
//
//    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
//
//        TextView user_name;
//        CircleImageView user_profile_img;
//
//
//
//        public FindFriendViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            user_name = itemView.findViewById(R.id.friends_name);
//            user_profile_img = itemView.findViewById(R.id.users_profile_image);
//
//
//        }
//
//    }
}
