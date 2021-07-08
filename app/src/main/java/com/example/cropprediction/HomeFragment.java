package com.example.cropprediction;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    TextInputLayout district, area;
    CardView predictButton;
    TextView cardText;
    ProgressBar progressBar;
    String cropName;
    String cropYield;
    String humidity;
    String rainfall;
    String suggestedCrops;
    String temperature;
    String windSpeed;
    TextView result;
    ServerResponse serverResponse;

    LocalDateTime date;
    CropDetails cropDetails;

    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    CircleImageView profilePic;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        district = view.findViewById(R.id.district);
        area = view.findViewById(R.id.area);

        predictButton = view.findViewById(R.id.cardPrediction);

        result = view.findViewById(R.id.result);

        cardText = view.findViewById(R.id.cardText);
        progressBar = view.findViewById(R.id.progressBar);
        profilePic = view.findViewById(R.id.profilePic);

        profilePic.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_pin_24));

        setProfilePicture();

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                cardText.setVisibility(View.GONE);
                getDataFromServer();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        //https://www.xyz.com/values?city=kottayam&araea=212


    }

    private void getDataFromServer() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("cropyieldpredictionapp.herokuapp.com")
                .appendPath("values")
                .appendQueryParameter("city", district.getEditText().getText().toString())
                .appendQueryParameter("area", area.getEditText().getText().toString());
        String myUrl = builder.build().toString();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(myUrl).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        cropName = jsonObject.getString("CropName");
                        cropYield = jsonObject.getString("CropYield");
                        humidity = jsonObject.getString("Humidity");
                        rainfall = jsonObject.getString("Rainfall");
                        suggestedCrops = jsonObject.getString("Suggestedcrops");
                        temperature = jsonObject.getString("Temperature");
                        windSpeed = jsonObject.getString("Windspeed");
                        serverResponse=new ServerResponse(cropName,cropYield,humidity,rainfall,suggestedCrops,temperature,windSpeed);

//                        fetchCropDetails(cropName,new ServerDetailsCallback() {
//                            @Override
//                            public void onServerDataRecieved(QuerySnapshot queryDocumentSnapshots) {
//                                cropDetails=queryDocumentSnapshots.getDocuments().get(0).toObject(CropDetails.class);
//                                System.out.println("insidde fragment"+cropDetails.getPrice());
//                                System.out.println("insidde fragment"+cropDetails.getTime());
//                                System.out.println("insidde fragment"+cropDetails.getDistrict());
//                                System.out.println("insidde fragment"+cropDetails.getName());
//                                PredictionBottomSheetDialogFragment predictionBottomSheetDialogFragment=new PredictionBottomSheetDialogFragment(serverResponse,cropDetails.getPrice());
//                                predictionBottomSheetDialogFragment.show(getParentFragmentManager(),"Dialog");
//
//                            }
//                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
//                            result.setText("The best crop according to the cuurent climate is " + cropName);
                            fetchCropDetails(cropName,new ServerDetailsCallback() {
                                @Override
                                public void onServerDataRecieved(QuerySnapshot queryDocumentSnapshots) {
                                    cropDetails=queryDocumentSnapshots.getDocuments().get(0).toObject(CropDetails.class);
                                    System.out.println("insidde fragment"+cropDetails.getPrice());
                                    System.out.println("insidde fragment"+cropDetails.getTime());
                                    System.out.println("insidde fragment"+cropDetails.getDistrict());
                                    System.out.println("insidde fragment"+cropDetails.getName());
                                    PredictionBottomSheetDialogFragment predictionBottomSheetDialogFragment=new PredictionBottomSheetDialogFragment(serverResponse,cropDetails.getPrice());
                                    predictionBottomSheetDialogFragment.show(getParentFragmentManager(),"Dialog");

                                }
                            });



                            //adding to the database

                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            date = LocalDateTime.now();
                            System.out.println("////" + dtf.format(date));

                            System.out.println(temperature + "[[[[[[[[[[[");
                            System.out.println(humidity + "[[[[[[[[[[[");
                            System.out.println(rainfall + "[[[[[[[[[[[");
                            System.out.println(temperature + "[[[[[[[[[[[");
                            System.out.println(windSpeed + "[[[[[");

                            HistoryModel historyModel = new HistoryModel(String.valueOf(date), cropName, cropYield, temperature, windSpeed, suggestedCrops, rainfall);


                            Task<DocumentReference> documentReference = firebaseFirestore.collection("History").document(firebaseAuth.getCurrentUser().getUid())
                                    .collection("userHistory").add(historyModel);
                            documentReference.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                            cardText.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            System.out.println(cropName);
                        }
                    });
                }
            }
        });
    }

    private void setProfilePicture() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("profile_pictures/" + firebaseAuth.getCurrentUser().getUid() + ".jpeg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri!=null){
                String downloadLink = uri.toString();
                System.out.println("download link for the image is " + downloadLink);
                Picasso.get().load(downloadLink).into(profilePic);}
                else{
                    Toast.makeText(getContext(), "Looks like there is no profile picture", Toast.LENGTH_SHORT).show();
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Couldn't download image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCropDetails(String cropName,ServerDetailsCallback cropDetailsCallback){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        CollectionReference collectionReference=firebaseFirestore.collection("Crop_Details");
        collectionReference.whereEqualTo("Name",cropName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cropDetailsCallback.onServerDataRecieved(queryDocumentSnapshots);

            }
        });

    }

    public interface ServerDetailsCallback{
        void onServerDataRecieved(QuerySnapshot queryDocumentSnapshots);
    }


    public interface HttpCallbackService {
        void onDataReady();
    }


}

