package com.example.cropprediction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class HistoryActivity extends AppCompatActivity {

    TextView crop,yield,district,soil,suggestion,date,expecteddate;
    RecyclerView recyclerView;
    HistoryModel historyModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appbar_color));
        }

        Intent intent=getIntent();
        historyModel= (HistoryModel) intent.getSerializableExtra("historyObject");
        System.out.println(historyModel.getCropPredicted());

        init();

        fetchCropDetails(new CropDetailsCallback() {
            @Override
            public void onDetailsReady(QuerySnapshot queryDocumentSnapshots) {
                CropDetails cropDetails=queryDocumentSnapshots.getDocuments().get(0).toObject(CropDetails.class);
                setValues(cropDetails);
                String futureDate=calculateEstimatedDate(historyModel.getDate(),cropDetails.getTime());
                String expected="Full yield can be expected from "+futureDate;

                SpannableString ss = new SpannableString(expected);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.Events.TITLE, cropDetails.getName()+" Production");
                        System.out.println("future "+futureDate);
                        Calendar cal1 = new GregorianCalendar(Integer.parseInt(futureDate.split("/")[2]),
                                Integer.parseInt(String.valueOf(futureDate.split("/")[1].charAt(0)=='0'?
                                        Integer.parseInt(String.valueOf(futureDate.split("/")[1].charAt(1))):
                                        Integer.parseInt(futureDate.split("/")[1])))-1,

                                30, 15, 20);
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal1);
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal1);
//                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//                        startDateMillis);
//                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//                        endDateMillis);
                        intent.putExtra(CalendarContract.Events.ALL_DAY, true);// periodicity
                        intent.putExtra(CalendarContract.Events.DESCRIPTION,cropDetails.getName()+" production you started "+
                                cropDetails.getTime()+" months ago must have started producing yield");
                        startActivity(intent);
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ss.setSpan(clickableSpan, 32, expected.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                expecteddate.setText(ss);
                expecteddate.setMovementMethod(LinkMovementMethod.getInstance());
                expecteddate.setHighlightColor(Color.TRANSPARENT);


            }
        });


    }

    private void setValues(CropDetails cropDetails){
        crop.setText(historyModel.getCropPredicted());
        yield.setText("Yield Estimated :"+historyModel.getYieldValue()+" Ton/H");
        district.setText("Mostly Produced in " +cropDetails.getDistrict()+" district");
        soil.setText(cropDetails.getSoil_type()+"is the most Suitable soil");
        date.setText(makeDateStandard(historyModel.getDate()));

        CropSuggestionRecyclerView cropSuggestionRecyclerView=new CropSuggestionRecyclerView(historyModel.getSuggestedCrops().split(","));
        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        recyclerView.setAdapter(cropSuggestionRecyclerView);


    }

    private void fetchCropDetails(CropDetailsCallback cropDetailsCallback){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        CollectionReference collectionReference=firebaseFirestore.collection("Crop_Details");
        collectionReference.whereEqualTo("Name",historyModel.getCropPredicted()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    cropDetailsCallback.onDetailsReady(queryDocumentSnapshots);

            }
        });

    }
    private void init(){
        crop=findViewById(R.id.cropName);
        yield=findViewById(R.id.yieldEstimated);
        district=findViewById(R.id.district);
        soil=findViewById(R.id.soilType);
        suggestion=findViewById(R.id.suggestedCrops);
        date=findViewById(R.id.date);
        expecteddate=findViewById(R.id.expected);
        recyclerView=findViewById(R.id.suggestedCropslist);

    }

    private String calculateEstimatedDate(String currentdate,int months){
        String[] dateStandardArray=currentdate.split("T")[0].split("-");
        String correctedMonth=dateStandardArray[1].charAt(0)=='0'?dateStandardArray[1].substring(1,2):dateStandardArray[1];
        String dateStandard=dateStandardArray[2]+"/"+correctedMonth+"/"+dateStandardArray[0];
        int month=Integer.parseInt(dateStandard.split("/")[1]);
        if(month+months<12){
            return dateStandardArray[2]+"/"+(month+months)+"/"+dateStandardArray[0];
        }
        else{
            return dateStandardArray[2]+"/"+(month+months)%12+"/"+dateStandardArray[0]+(month+months)/12;
        }

    }

    private String makeDateStandard(String currentdate){
        String[] dateStandardArray=currentdate.split("T")[0].split("-");
        String correctedMonth=dateStandardArray[1].charAt(0)=='0'?dateStandardArray[1].substring(1,2):dateStandardArray[1];
        String dateStandard=dateStandardArray[2]+"/"+correctedMonth+"/"+dateStandardArray[0];
        return dateStandard;

    }
    public interface CropDetailsCallback{
        void onDetailsReady(QuerySnapshot queryDocumentSnapshots);
    }
}