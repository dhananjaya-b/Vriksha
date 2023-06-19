package com.example.vriksha;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiClient {
    private static final String API_KEY = "bb96fd2328451035a773a6d4ac5eb914";

    public static void main(String[] args) {
        double latitude = 37.7749;  // Replace with the desired latitude
        double longitude = -122.4194;  // Replace with the desired longitude

        String weatherData = getWeatherData(latitude, longitude);
        System.out.println(weatherData);
    }

    private static String getWeatherData(double latitude, double longitude) {
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude +
                    "&lon=" + longitude + "&appid=" + API_KEY;

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
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

