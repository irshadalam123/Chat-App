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


public class RequestSentFrag extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference requestSent_ref, requestReceive_ref, userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View Myview =  inflater.inflate(R.layout.fragment_request_sent, container, false);


        recyclerView = Myview.findViewById(R.id.request_sent_recycler);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        requestSent_ref = FirebaseDatabase.getInstance().getReference().child("Request_Sent");
        requestReceive_ref = FirebaseDatabase.getInstance().getReference().child("Request_Receive");



        return Myview;
    }


    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(requestSent_ref.child(currentUserId), new SnapshotParser<Contacts>() {
            @NonNull
            @Override
            public Contacts parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Contacts();
            }
        }).build();


        FirebaseRecyclerAdapter<Contacts, RequestSentFrag.RequestSentViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestSentFrag.RequestSentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestSentFrag.RequestSentViewHolder holder, int position, @NonNull Contacts model) {

                final String req_user_id = getRef(position).getKey();


                userRef.child(req_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("Profile_Image")){
//                            Glide.with(RequestSentFrag.this).load(dataSnapshot.child("Profile_Image").getValue().toString()).into(holder.req_pro_image);
                            Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(holder.req_pro_image);
                        }
                        holder.req_name.setText(dataSnapshot.child("Name").getValue().toString());


                        holder.cancel_btn.setVisibility(View.INVISIBLE);
                        holder.cancel_btn.setEnabled(false);

                        holder.accept_btn.setText("Cancel");
                        holder.accept_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestSent_ref.child(currentUserId).child(req_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            requestReceive_ref.child(req_user_id).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(getContext(), "Request Cancel", Toast.LENGTH_SHORT).show();
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
            public RequestSentFrag.RequestSentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_friends_sample, parent, false);
                return new RequestSentFrag.RequestSentViewHolder(view);
            }
        };



        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.startListening();

//        if(adapter.getItemCount() == 0){
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            TextView msg = new TextView(getContext());
//            msg.setTextSize(22);
//            msg.setGravity(1);
//            msg.setText("You have no any sent request!");
//            builder.setView(msg);
//            builder.show();
//        }

    }

    public static class RequestSentViewHolder extends RecyclerView.ViewHolder{

        Button accept_btn, cancel_btn;
        TextView req_name;
        CircleImageView req_pro_image;

        public RequestSentViewHolder(@NonNull View itemView) {
            super(itemView);

            accept_btn = itemView.findViewById(R.id.accept_request);
            cancel_btn = itemView.findViewById(R.id.cancel_request);
            req_name = itemView.findViewById(R.id.request_friends_name);
            req_pro_image = itemView.findViewById(R.id.request_users_profile_image);

        }
    }

}
