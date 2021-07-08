package com.example.cropprediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements HistoryRecyclerAdapter.OnItemClick{

    CircleImageView profilePic;
    TextView username,mailID,phoneNumber;
    RecyclerView recyclerView;
    String usernameValue,phoneNumberValue,emailValue;
    ArrayList<HistoryModel> historyModels=new ArrayList<>();
    ImageView signout;
    public static final int PICK_IMAGE = 1;

    byte[] imageToUpload;

    Bitmap bitmap;

    ImageView addProfilepicture;


    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appbar_color));
        }
        init();
        setProfilePicInActivity();
        getUserDetails(new UserDetails() {
            @Override
            public void onUserDetailsReady(UserModel user) {
                usernameValue=user.getUsername();
                phoneNumberValue=user.getPhone();
                emailValue=user.getEmail();

                username.setText(usernameValue);
                mailID.setText(emailValue);
                phoneNumber.setText("+91 "+phoneNumberValue);

            }
        });
        getHistory(new HistoryDetails() {
            @Override
            public void onHistoryReady() {
                HistoryRecyclerAdapter historyRecyclerAdapter=new HistoryRecyclerAdapter(historyModels,ProfileActivity.this::onHistoryClicked);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                recyclerView.setAdapter(historyRecyclerAdapter);

            }
        });

        addProfilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "logged out succesfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
            }
        });


    }
    private void init(){
        profilePic=findViewById(R.id.profilePicActivity);
        username=findViewById(R.id.userName);
        mailID=findViewById(R.id.mailID);
        phoneNumber=findViewById(R.id.phoneNumber);
        recyclerView=findViewById(R.id.historyRecycler);
        addProfilepicture=findViewById(R.id.addProfilePicture);
        signout=findViewById(R.id.signout);
    }

    private void setProfilePicInActivity(){
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("profile_pictures/"+firebaseAuth.getCurrentUser().getUid()+".jpeg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadLink=uri.toString();
                System.out.println("download link for the image is "+downloadLink);
                Picasso.get().load(downloadLink).into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Couldn't download image", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void getUserDetails(UserDetails userDetails){
        DocumentReference documentReference = FirebaseFirestore.getInstance().document("Users/" + firebaseAuth.getCurrentUser().getUid());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                userDetails.onUserDetailsReady(userModel);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }
    private void getHistory(HistoryDetails historyDetails){

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        CollectionReference collectionReference=FirebaseFirestore.getInstance().collection("History").
                document(firebaseAuth.getCurrentUser().getUid()).collection("userHistory");
        collectionReference.orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "No History", Toast.LENGTH_SHORT).show();
                }
                else{
                    List<DocumentSnapshot> history=queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot documentSnapshot:history){
                        HistoryModel historyModel=documentSnapshot.toObject(HistoryModel.class);
                        historyModels.add(historyModel);

                    }
                    historyDetails.onHistoryReady();

                }

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            Uri filePath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(),
                        filePath);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                imageToUpload = baos.toByteArray();
                uploadImage();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "Image picked", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImage() {
        if (imageToUpload != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("profile_pictures/"+firebaseAuth.getCurrentUser().getUid()+".jpeg");
            // adding listeners on upload
            // or failure of image
            ref.putBytes(imageToUpload)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    @Override
    public void onHistoryClicked(int position) {
        Intent intent=new Intent(ProfileActivity.this,HistoryActivity.class);
        intent.putExtra("historyObject",historyModels.get(position));
        startActivity(intent);
    }

    public interface UserDetails{
        void onUserDetailsReady(UserModel user);

    }
    public interface HistoryDetails{
            void onHistoryReady();
    }

}