package com.example.cropprediction;

import android.renderscript.ScriptIntrinsicYuvToRGB;

import com.google.firebase.firestore.IgnoreExtraProperties;
@IgnoreExtraProperties
public class CropDetails {
    String Name;
    String District;
    long MaximumYield;
    String Soil_type;
    int Time;
    int Price;

    public CropDetails(){

    }

    public CropDetails(String name, String district, long maximumYield, String Soil_type, int time) {
        Name = name;
        District = district;
        MaximumYield = maximumYield;
        Soil_type = Soil_type;
        Time = time;
    }

    public CropDetails(String name, String district, long maximumYield, String Soil_type, int time, int Price) {
        Name = name;
        District = district;
        MaximumYield = maximumYield;
        Soil_type = Soil_type;
        Time = time;
        this.Price = Price;
    }

    public String getSoil_type() {
        return Soil_type;
    }

    public void setSoil_type(String Soil_type) {
        Soil_type = Soil_type;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }



    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public long getMaximumYield() {
        return MaximumYield;
    }

    public void setMaximumYield(long maximumYield) {
        MaximumYield = maximumYield;
    }


    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }
}
