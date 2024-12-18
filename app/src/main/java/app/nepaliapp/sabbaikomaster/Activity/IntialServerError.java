package app.nepaliapp.sabbaikomaster.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;

public class IntialServerError extends AppCompatActivity {
    private TextView tvCountdownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intial_server_error);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvCountdownTimer = findViewById(R.id.tvCountdownTimer);
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("jsonobject");
        try {
            JSONObject object = new JSONObject(jsonString);
            String estimatedTime = object.getString("totalTime");
            tvCountdownTimer.setText(estimatedTime);
        } catch (JSONException e) {
            Toast.makeText(this, "Data conversion Error", Toast.LENGTH_SHORT).show();
        }
    }
//Use if you found a way to get correct time with evaluating value of date & time
    private void setTvCountdownTimer(){

        long remainingTimeInMillis = 2 * 60 * 60 * 1000; // Example: 2 hours

        new CountDownTimer(remainingTimeInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                tvCountdownTimer.setText(time);
            }
            @Override
            public void onFinish() {
                tvCountdownTimer.setText("00:00:00");
            }
        }.start();
    }
}