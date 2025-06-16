package com.codingtutorials.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private TextView cityNameText, temperatureText, humidityText, descriptionText, windText;
    private ImageView weatherIcon;
    private EditText cityNameInput;
    private Button refreshButton;

    private double currentHumidity = 0;
    private double currentWindSpeed = 0;
    private double currentTemperature = 0;
    private String currentCondition = "clear";
    private String lastSearchedCity = "Mumbai";

    private static final String API_KEY = "7be4a25466b8361c2ae28097a6aa5617";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameText = findViewById(R.id.cityNameText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        descriptionText = findViewById(R.id.descriptionText);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityNameInput = findViewById(R.id.cityNameInput);
        refreshButton = findViewById(R.id.fetchWeatherButton);
        temperatureText = findViewById(R.id.temperatureText);

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_weather);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_weather) {
                return true;
            } else if (item.getItemId() == R.id.nav_agri) {
                Intent intent = new Intent(MainActivity.this, AgriActivity.class);
                intent.putExtra("humidity", currentHumidity);
                intent.putExtra("windSpeed", currentWindSpeed);
                intent.putExtra("temperature", currentTemperature);
                intent.putExtra("condition", currentCondition);
                intent.putExtra("city", lastSearchedCity);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        refreshButton.setOnClickListener(view -> {
            String cityName = cityNameInput.getText().toString().trim();
            if (!cityName.isEmpty()) {
                lastSearchedCity = cityName;
                FetchWeatherData(cityName);
            } else {
                cityNameInput.setError("Please enter a city name");
            }
        });

        // Restore last searched city if returned from AgriActivity
        Intent incomingIntent = getIntent();
        String returnedCity = incomingIntent.getStringExtra("city");
        if (returnedCity != null && !returnedCity.isEmpty()) {
            lastSearchedCity = returnedCity;
        }

        cityNameInput.setText(lastSearchedCity);
        FetchWeatherData(lastSearchedCity);
    }

    private void FetchWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                runOnUiThread(() -> updateUI(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateUI(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");

                currentHumidity = main.getDouble("humidity");
                currentTemperature = main.getDouble("temp");
                currentCondition = jsonObject.getJSONArray("weather")
                        .getJSONObject(0).getString("description");

                currentWindSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String iconCode = jsonObject.getJSONArray("weather")
                        .getJSONObject(0).getString("icon");

                String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                weatherIcon.setImageResource(resId);

                String name = jsonObject.getString("name");
                lastSearchedCity = name;
                cityNameText.setText(name);
                cityNameInput.setText(name);

                temperatureText.setText(String.format("%.0f°", currentTemperature));
                humidityText.setText(String.format("%.0f%%", currentHumidity));
                windText.setText(String.format("%.0f km/h", currentWindSpeed));
                descriptionText.setText(currentCondition);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
