package com.example.messenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestReceiveFrag extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference requestReceive_ref, requestSent_ref, contact_Ref, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_request_receive, container, false);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        requestReceive_ref = FirebaseDatabase.getInstance().getReference().child("Request_Receive").child(currentUserId);
        requestSent_ref = FirebaseDatabase.getInstance().getReference().child("Request_Sent");
        contact_Ref = FirebaseDatabase.getInstance().getReference().child("Contact");

        recyclerView = myView.findViewById(R.id.request_recycler);

        return myView;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(requestReceive_ref, new SnapshotParser<Contacts>() {
            @NonNull
            @Override
            public Contacts parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Contacts();
            }
        }).build();


        FirebaseRecyclerAdapter<Contacts, RequestReceiveFrag.RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestReceiveFrag.RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestReceiveFrag.RequestViewHolder holder, int position, @NonNull Contacts model) {

                final String req_user_id = getRef(position).getKey();


                userRef.child(req_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Profile_Image")){
//                            Glide.with(RequestReceiveFrag.this).load(dataSnapshot.child("Profile_Image").getValue().toString()).into(holder.req_pro_image);
                            Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(holder.req_pro_image);
                        }
                        holder.req_name.setText(dataSnapshot.child("Name").getValue().toString());


                        holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                contact_Ref.child(currentUserId).child(req_user_id).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            contact_Ref.child(req_user_id).child(currentUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        requestSent_ref.child(req_user_id).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    requestReceive_ref.child(req_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                Toast.makeText(getContext(),"Request Accept", Toast.LENGTH_SHORT).show();
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
                                    }
                                });
                            }
                        });


                        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSent_ref.child(req_user_id).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            requestReceive_ref.child(req_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getContext(),"Request Cancel", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
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
            public RequestReceiveFrag.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_friends_sample, parent, false);
                return new RequestReceiveFrag.RequestViewHolder(view);
            }
        };


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.startListening();


    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        Button accept_btn, cancel_btn;
        TextView req_name;
        CircleImageView req_pro_image;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            accept_btn = itemView.findViewById(R.id.accept_request);
            cancel_btn = itemView.findViewById(R.id.cancel_request);
            req_name = itemView.findViewById(R.id.request_friends_name);
            req_pro_image = itemView.findViewById(R.id.request_users_profile_image);

        }
    }
}
