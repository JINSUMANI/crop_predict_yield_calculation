package com.example.cropprediction;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class HistoryModel implements Serializable {

    String date;
    String cropPredicted;
    String yieldValue;
    String temperature;
    String windSpeed;
    String suggestedCrops;
    String rainfall;

    public HistoryModel(String date, String cropPredicted, String yieldValue, String temperature, String windSpeed, String suggestedCrops, String rainfall) {
        this.date = date;
        this.cropPredicted = cropPredicted;
        this.yieldValue = yieldValue;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.suggestedCrops = suggestedCrops;
        this.rainfall = rainfall;
    }

    public HistoryModel() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCropPredicted() {
        return cropPredicted;
    }

    public void setCropPredicted(String cropPredicted) {
        this.cropPredicted = cropPredicted;
    }

    public String getYieldValue() {
        return yieldValue;
    }

    public void setYieldValue(String yieldValue) {
        this.yieldValue = yieldValue;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getSuggestedCrops() {
        return suggestedCrops;
    }

    public void setSuggestedCrops(String suggestedCrops) {
        this.suggestedCrops = suggestedCrops;
    }

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }
}
