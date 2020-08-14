package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



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

public class FriendsContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference contact_ref, users_ref;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_contacts);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        contact_ref = FirebaseDatabase.getInstance().getReference().child("Contact").child(currentUserId);
        users_ref = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.contact_recyclerView);

        loader = new ProgressDialog(this);
        loader.setTitle("Loading...");
        loader.setCanceledOnTouchOutside(false);

        toolbar = findViewById(R.id.contact_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contact List");

    }


    @Override
    protected void onStart() {
        super.onStart();

        loader.show();
//        final Query query = contact_ref;
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(contact_ref, new SnapshotParser<Contacts>() {
            @NonNull
            @Override
            public Contacts parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Contacts();
            }
        }).build();


        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                        String friend_user_id = getRef(position).getKey();

                        users_ref.child(friend_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("Profile_Image")){
                                    String F_profile_image = dataSnapshot.child("Profile_Image").getValue().toString();
                                    String F_name = dataSnapshot.child("Name").getValue().toString();

                                    holder.Friend_user_name.setText(F_name);

//                                    Glide.with(FriendsContacts.this).load(F_profile_image).placeholder(R.drawable.profile).into(holder.Friend_user_profile_img);
                                    Picasso.get().load(F_profile_image).placeholder(R.drawable.profile).into(holder.Friend_user_profile_img);

                                    loader.dismiss();
                                }
                                else {
                                    String F_name = dataSnapshot.child("Name").getValue().toString();

                                    holder.Friend_user_name.setText(F_name);

                                    loader.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                        return viewHolder;

                    }
                };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendsContacts.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.startListening();


    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView Friend_user_name;
        CircleImageView Friend_user_profile_img;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            Friend_user_name = itemView.findViewById(R.id.friends_name);
            Friend_user_profile_img = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
