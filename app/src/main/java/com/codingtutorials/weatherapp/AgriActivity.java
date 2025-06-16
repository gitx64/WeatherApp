package com.codingtutorials.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AgriActivity extends AppCompatActivity {

    TextView adviceText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agri);

        adviceText = findViewById(R.id.adviceText);

        double humidity = getIntent().getDoubleExtra("humidity", 0);
        double windSpeed = getIntent().getDoubleExtra("windSpeed", 0);
        String condition = getIntent().getStringExtra("condition");

        StringBuilder advice = new StringBuilder();

        // 🌿 Advice based on humidity
        if (humidity >= 40 && humidity <= 70) {
            advice.append("🌾 Moderate Humidity: Good for Wheat, Barley, Mustard.\n\n");
        } else if (humidity > 70) {
            advice.append("🌴 High Humidity: Good for Paddy, Banana, Sugarcane.\n\n");
        } else if (humidity < 40) {
            advice.append("🥦 Low Humidity: Favorable for Cabbage, Carrot, Cauliflower.\n\n");
        }

        // 💨 Advice based on wind speed
        if (windSpeed >= 25) {
            advice.append("💨 High Wind: Avoid pesticide spraying. Protect light crops.\n\n");
        } else if (windSpeed >= 10) {
            advice.append("🌬️ Moderate Wind: Okay for general activities, but avoid light spraying.\n\n");
        } else {
            advice.append("🪴 Low Wind: Good for spraying and delicate plants.\n\n");
        }

        // 🌦️ Advice based on weather condition
        if (condition != null && !condition.isEmpty()) {
            String cond = condition.toLowerCase();

            if (cond.contains("rain")) {
                advice.append("🌧️ Raining: Good for Paddy and Sugarcane. Avoid spraying chemicals.\n\n");
            } else if (cond.contains("cloud")) {
                advice.append("☁️ Cloudy: Can slow down photosynthesis. Delay fertilizer application.\n\n");
            } else if (cond.contains("sun") || cond.contains("clear")) {
                advice.append("☀️ Sunny: Ideal for harvesting, drying grains, and applying pesticides.\n\n");
            } else if (cond.contains("storm")) {
                advice.append("🌪️ Storm Alert: Protect greenhouse crops. Delay sowing or harvesting.\n\n");
            } else {
                advice.append("📋 General Weather: ").append(condition).append("\n\n");
            }
        }

        if (advice.length() == 0) {
            advice.append("⚠️ No specific advice for the current conditions. Please consult your local agricultural officer.");
        }

        adviceText.setText(advice.toString().trim());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_agri); // Highlight agri

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_weather) {
                Intent intent = new Intent(AgriActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return item.getItemId() == R.id.nav_agri; // stay here
        });
    }
}
