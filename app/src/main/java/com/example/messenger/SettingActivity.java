package com.example.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    Button edit_profile_btn, delete_account_button;
    TextView userName, mobileNo, userEmail;
    EditText userId;
    FloatingActionButton Add_profile_image;
    ProgressDialog loaderBar;
    CircleImageView user_profile_Image;

    private Dialog dialog;

    private ImageView back_btn, profileImage;


    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private StorageReference userProfileImgRef;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edit_profile_btn = findViewById(R.id.update_button);
        delete_account_button = findViewById(R.id.delete_account);
        userName = findViewById(R.id.userName);
        mobileNo = findViewById(R.id.mobile_number);
        userEmail = findViewById(R.id.userEmail);
        userId = findViewById(R.id.userId);
        Add_profile_image = findViewById(R.id.add_profile_image);
        back_btn = findViewById(R.id.arrow_back_btn_setting);
        user_profile_Image = findViewById(R.id.profile_image);

        loaderBar = new ProgressDialog(this);
        loaderBar.setTitle("Loading...");
        loaderBar.setCanceledOnTouchOutside(false);

        dialog = new Dialog(this, R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.image_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        profileImage = dialog.findViewById(R.id.profile_image_dialog);


        mref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("Profile_Images");


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



//        user profile image

        user_profile_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final Dialog image_dialog = new Dialog(getApplicationContext());
//                image_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                image_dialog.setCanceledOnTouchOutside(false);
//                image_dialog.setContentView(R.layout.image_dialog);

//                final ImageView profileImage = image_dialog.findViewById(R.id.profile_image_dialog);


                mref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("Profile_Image")){
                            Picasso.get().load(dataSnapshot.child("Profile_Image").getValue().toString()).placeholder(R.drawable.profile).into(profileImage);
                        }
                        else {
                            Picasso.get().load("adlfkflkg").placeholder(R.drawable.profile).into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.show();

            }
        });



//      get the user information from database

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                currentUserId = mAuth.getCurrentUser().getUid();

                String name_Data = dataSnapshot.child("Users").child(currentUserId).child("Name").getValue(String.class);
                String userMobile_No_Data = dataSnapshot.child("Users").child(currentUserId).child("Mobile_No").getValue(String.class);
                String user_email_Data = dataSnapshot.child("Users").child(currentUserId).child("Email").getValue(String.class);
                String profile_Image = dataSnapshot.child("Users").child(currentUserId).child("Profile_Image").getValue(String.class);


                userName.setText(name_Data);
                mobileNo.setText(userMobile_No_Data);
                userEmail.setText(user_email_Data);
                userId.setText(currentUserId);

                Picasso.get().load(profile_Image).placeholder(R.drawable.profile).into(user_profile_Image);
//                user_profile_Image.setImageURI(Uri.parse(profileImage));
//                Glide.with(SettingActivity.this).load(profileImage).placeholder(R.drawable.profile).into(user_profile_Image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//      edit the user information

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Edit Profile");
                final LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText Name = new EditText(getApplicationContext());
                Name.setHint("User Name");
                layout.addView(Name);
                final EditText mobNo = new EditText(getApplicationContext());
                mobNo.setHint("Mobile No.");
                layout.addView(mobNo);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        currentUserId = mAuth.getCurrentUser().getUid();

                        String new_name = Name.getText().toString();
                        String new_mob = mobNo.getText().toString();
                        if (!TextUtils.isEmpty(new_name)){
                            mref.child("Users").child(currentUserId).child("Name").setValue(new_name);
                        }
                        if (!TextUtils.isEmpty(new_mob)){
                            mref.child("Users").child(currentUserId).child("Mobile_No").setValue(new_mob);
                        }

                    }
                });

                builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


//        Delete account............

        delete_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder delete_dialog = new AlertDialog.Builder(SettingActivity.this);
                delete_dialog.setTitle("You want to delete your account?");
                delete_dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Account Deleted Successfully",Toast.LENGTH_SHORT).show();

//                        mref.child("Users").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//
//                                }
//                            }
//                        });

                    }
                });
                delete_dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                delete_dialog.show();
            }
        });

//        change profile image

        Add_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent image_intent = new Intent();
                image_intent.setAction(Intent.ACTION_GET_CONTENT);
                image_intent.setType("image/*");
                startActivityForResult(image_intent, 1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
//            CropImage.activity(ImageUri)
//                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){


                loaderBar.show();

                Uri resultUri = result.getUri();

                user_profile_Image.setImageURI(resultUri);

                final StorageReference filepath = userProfileImgRef.child(currentUserId+".jpg");


                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();

                                mref.child("Users").child(currentUserId).child("Profile_Image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Update Success",Toast.LENGTH_SHORT).show();

                                                loaderBar.dismiss();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Error! "+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                                loaderBar.dismiss();
                                            }
                                        }
                                    });
                            }
                        });
                    }
                });

            }
        }
    }

}
