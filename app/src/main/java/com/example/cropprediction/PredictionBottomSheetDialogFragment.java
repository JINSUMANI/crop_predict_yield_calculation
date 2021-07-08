package com.example.cropprediction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PredictionBottomSheetDialogFragment extends BottomSheetDialogFragment {
    ServerResponse serverResponse;
    int price;

    PredictionBottomSheetDialogFragment(ServerResponse serverResponse,int price){
        this.serverResponse=serverResponse;
        this.price=price;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.prediction_dialog_fragment,container,false);

        TextView dialogcrop=view.findViewById(R.id.cropnameserver);
        TextView dialogYield=view.findViewById(R.id.cropyieldserver);
        TextView earnings=view.findViewById(R.id.earnings);
        String[] suggestions=serverResponse.getSuggestedcrops().split(",");
        RecyclerView recyclerView=view.findViewById(R.id.suggestionList);
        dialogcrop.setText(serverResponse.getCropName());
        dialogYield.setText(serverResponse.getCropYield().substring(0,5)+" T/Hect");
        float value=Float.valueOf(serverResponse.getCropYield());
        System.out.println("value is "+value);
        System.out.println(price);
        earnings.setText(String.valueOf(value*1000*price)+" ₹/Hectare");
        CropSuggestionRecyclerView cropSuggestionRecyclerView=new CropSuggestionRecyclerView(suggestions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cropSuggestionRecyclerView);

        return  view;

    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
