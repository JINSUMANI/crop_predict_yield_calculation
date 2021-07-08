package com.example.cropprediction;

public class ServerResponse {

    String CropName,CropYield,Humidity,Rainfall,Suggestedcrops,Temperature,Windspeed;

    public ServerResponse() {
    }

    public ServerResponse(String cropName, String cropYield, String humidity, String rainfall, String suggestedcrops, String temperature, String windspeed) {
        CropName = cropName;
        CropYield = cropYield;
        Humidity = humidity;
        Rainfall = rainfall;
        Suggestedcrops = suggestedcrops;
        Temperature = temperature;
        Windspeed = windspeed;
    }

    public String getCropName() {
        return CropName;
    }

    public void setCropName(String cropName) {
        CropName = cropName;
    }

    public String getCropYield() {
        return CropYield;
    }

    public void setCropYield(String cropYield) {
        CropYield = cropYield;
    }

    public String getHumidity() {
        return Humidity;
    }

    public void setHumidity(String humidity) {
        Humidity = humidity;
    }

    public String getRainfall() {
        return Rainfall;
    }

    public void setRainfall(String rainfall) {
        Rainfall = rainfall;
    }

    public String getSuggestedcrops() {
        return Suggestedcrops;
    }

    public void setSuggestedcrops(String suggestedcrops) {
        Suggestedcrops = suggestedcrops;
    }

    public String getTemperature() {
        return Temperature;
    }

    public void setTemperature(String temperature) {
        Temperature = temperature;
    }

    public String getWindspeed() {
        return Windspeed;
    }

    public void setWindspeed(String windspeed) {
        Windspeed = windspeed;
    }
}
