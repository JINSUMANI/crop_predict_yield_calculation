package com.example.cropprediction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherFragment extends Fragment {

    Dialog dialog;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    String zipCode, countryCode, seaLevel, groundLevel, sunrise_readable, sunset_readable;
    double temperature, pressure, humidity, windSpeed;
    long sunrise, sunset;

    TextView sunriseTextview, sunsetTextview, windTextview, humidityTextview, temperatureTextview, sealevelTextview, groundlevelTextview, pressureTextview;

    boolean isWeatherDataReady = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new Dialog(getContext(), R.style.FullHeightDialog);

        dialog.setContentView(R.layout.weather_dialog);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        dialog.show();
        init(view);
        getLastLocation(new UserLocationReady() {
            @Override
            public void onLocationReady() {
                fetchWeatherData(new WeatherDetails() {
                    @Override
                    public void onDataReceived() {
                        WeatherFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                setWidgetValues();


                            }
                        });
                    }
                });
            }


        });

//        api.openweathermap.org/data/2.5/weather?zip=686633,IN&appid=d3f5f94cb63316aec3c2e72fde6d8e9f
    }

    private void init(View view) {
        sunriseTextview = view.findViewById(R.id.sunrise);
        sunsetTextview = view.findViewById(R.id.sunset);
        temperatureTextview = view.findViewById(R.id.temperature);
        windTextview = view.findViewById(R.id.wind);
        humidityTextview = view.findViewById(R.id.humidity);
        pressureTextview = view.findViewById(R.id.pressure);
        sealevelTextview = view.findViewById(R.id.sealevel);
        groundlevelTextview = view.findViewById(R.id.grndlevel);
    }

    private void setWidgetValues() {
        sunriseTextview.setText(sunrise_readable + " AM");
        sunsetTextview.setText(sunset_readable + " PM");
        temperatureTextview.setText(String.format("%.2f", temperature) + "°C");
        windTextview.setText(String.format("%.2f", windSpeed) + " km/hr");
        humidityTextview.setText(String.format("%.2f", humidity) + " %");
        pressureTextview.setText(String.format("%.2f", pressure) + " P");
        sealevelTextview.setText(seaLevel);
        groundlevelTextview.setText(groundLevel);
    }

    private void fetchWeatherData(WeatherDetails weatherDetails) {

        String myUrl = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipCode.trim() + "," + countryCode.trim() + "&appid=" + getResources().getString(R.string.weather_Api_key);

        System.out.println("url is " + myUrl);
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
                        JSONObject mainJSON = new JSONObject(response.body().string());
                        JSONObject childJSON = mainJSON.getJSONObject("main");
                        temperature = childJSON.getDouble("temp");
                        temperature = temperature - 273.15;
                        pressure = childJSON.getDouble("pressure");
                        humidity = childJSON.getDouble("humidity");
                        seaLevel = String.valueOf(childJSON.getInt("sea_level"));
                        groundLevel = String.valueOf(childJSON.getInt("grnd_level"));
                        JSONObject wind = mainJSON.getJSONObject("wind");
                        windSpeed = wind.getDouble("speed");
                        windSpeed = windSpeed * 18 / 5;


                        JSONObject sys = mainJSON.getJSONObject("sys");
                        sunrise = sys.getLong("sunrise");
                        sunrise_readable = formatMs(sunrise);
                        sunset = sys.getLong("sunset");
                        sunset_readable = formatMs(sunset);

                        System.out.println("From weather Api \n" + temperature + "\n" + pressure);

                        weatherDetails.onDataReceived();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(UserLocationReady userLocationReady) {
        // check if permissions are given
        if (checkPermissions()) {


            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            System.out.println(location.getLongitude() + "???????????here");

                            Geocoder geocoder;
                            List<Address> addresses = null;
                            geocoder = new Geocoder(getContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            countryCode = addresses.get(0).getCountryCode();
                            zipCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();

                            System.out.println("address : " + address);
                            System.out.println("city : " + city);
                            System.out.println("state : " + state);
                            System.out.println("country : " + country);
                            System.out.println("countrycode : " + countryCode);
                            System.out.println("postalcode : " + zipCode);
                            System.out.println("knownname : " + knownName);
                            userLocationReady.onLocationReady();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            System.out.println(mLastLocation.getLongitude() + "???????????");


            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            countryCode = addresses.get(0).getCountryCode();
            zipCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            System.out.println("address : " + address);
            System.out.println("city : " + city);
            System.out.println("state : " + state);
            System.out.println("country : " + country);
            System.out.println("countrycode : " + countryCode);
            System.out.println("postalcode : " + zipCode);
            System.out.println("knownname : " + knownName);


            fetchWeatherData(new WeatherDetails() {
                @Override
                public void onDataReceived() {
                    WeatherFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            setWidgetValues();


                        }
                    });
                }
            });



        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(new UserLocationReady() {
                    @Override
                    public void onLocationReady() {
                        fetchWeatherData(new WeatherDetails() {
                            @Override
                            public void onDataReceived() {
                                WeatherFragment.this.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        setWidgetValues();


                                    }
                                });
                            }
                        });
                    }


                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation(new UserLocationReady() {
                @Override
                public void onLocationReady() {
                    fetchWeatherData(new WeatherDetails() {
                        @Override
                        public void onDataReceived() {
                            WeatherFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    setWidgetValues();


                                }
                            });
                        }
                    });
                }


            });
        }
    }

    public static String formatMs(long millis) {
        long x = millis / 1000;

        long secs = x % 60;
        x /= 60;
        long mins = x % 60;
        x /= 60;

        long hours = x % 24;
        x /= 24;
        if (hours > 12) {
            hours = hours - 12;

        }
        return String.format("%d:%d:%d",
                hours,
                mins,
                secs
        );
    }

    public interface WeatherDetails {
        void onDataReceived();
    }

    public interface UserLocationReady {
        void onLocationReady();
    }

}
