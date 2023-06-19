package com.example.vriksha;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.vriksha.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String API_KEY = "bb96fd2328451035a773a6d4ac5eb914";


    private TextView dateTextView;
    private TextView statusTextView;
    private TextView dayTextView;
    private TextView timeTextView;
    private TextView cityTextView;
    private TextView tempTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private TextView humidityTextView;

    TextInputLayout cityInputLayout;
    TextInputEditText cityEditText;
    ImageButton search;
    WeatherTask weatherTask;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dateTextView = view.findViewById(R.id.date);
        dayTextView = view.findViewById(R.id.day);
        timeTextView = view.findViewById(R.id.time);
        cityTextView = view.findViewById(R.id.city);
        tempTextView = view.findViewById(R.id.temp);
        windTextView = view.findViewById(R.id.wind);
        pressureTextView = view.findViewById(R.id.pressure);
        humidityTextView = view.findViewById(R.id.humidity);
        statusTextView = view.findViewById(R.id.status);

        cityInputLayout = (TextInputLayout) view.findViewById(R.id.location);


        cityEditText = (TextInputEditText) cityInputLayout.getEditText();


        search= (ImageButton) view.findViewById(R.id.citySearch);
        weatherTask = new WeatherTask();
        weatherTask.execute("mangaluru");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String currentDate = dateFormat.format(calendar.getTime());
        String currentDay = dayFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        // Update the dateTextView, dayTextView, and timeTextView with the current values
        dateTextView.setText(currentDate);
        dayTextView.setText(currentDay);
        timeTextView.setText(currentTime);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityname= String.valueOf(cityEditText.getText());
                weatherTask = new WeatherTask();

                    weatherTask.execute(cityname);



            }
        });
        // Request location permission and fetch the user's location
        return view;
    }





    private class WeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String city = params[0];


                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city +"&appid=" + API_KEY;

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the weather data and update the UI
            try {
                JSONObject jsonObject = new JSONObject(result);
                String city=jsonObject.getString("name");

                JSONObject mainObject = jsonObject.getJSONObject("main");
                double temperature = mainObject.getDouble("temp");
                double pressure = mainObject.getDouble("pressure");
                int humidity = mainObject.getInt("humidity");

                // Extract weather description from the "weather" array
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String weatherDescription = weatherObject.getString("description");

                // Extract wind details from the "wind" object
                JSONObject windObject = jsonObject.getJSONObject("wind");
                double windSpeed = windObject.getDouble("speed");
                int windDegree = windObject.getInt("deg");
                cityTextView.setText(city);
                double temperatureCelsius = temperature - 273.15;
                String temperatureFormatted = String.format(Locale.getDefault(), "%.2f", temperatureCelsius);
                tempTextView.setText(temperatureFormatted+"\u00B0C");

                String windDirection = getWindDirection(windDegree);
                windTextView.setText("Wind : "+windSpeed+"m/s ,"+windDirection);
                pressureTextView.setText("Pressure : "+pressure+"hPa");
                humidityTextView.setText("Humidity : "+humidity+"%");
                statusTextView.setText(weatherDescription);
                System.out.println(result);


            } catch (JSONException e) {
                e.printStackTrace();
                cityTextView.setText("City Not found");
                tempTextView.setText("--\u00B0C");
                windTextView.setText("Wind : "+"--"+"m/s ");
                pressureTextView.setText("Pressure : "+"--"+"hPa");
                statusTextView.setText("--");
                humidityTextView.setText("Humidity : "+"--"+"%");
            }
        }
        private String getWindDirection(double degrees) {
            String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
            int index = (int) Math.round((degrees % 360) / 45);
            index = (index + 8) % 8; // Ensure index wraps around within the array bounds
            return directions[index];
        }

    }
}
