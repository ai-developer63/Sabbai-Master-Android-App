package app.nepaliapp.sabbaikomaster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.Activity.IntialServerError;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;
import app.nepaliapp.sabbaikomaster.fragmentManager.SigningManager;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;
    RequestQueue requestQueue;
    Url url;
    ProgressBar progressBar;
    Button restartBtn;
    TextView sloganTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        restartBtn = findViewById(R.id.retryBtn);
        sloganTxt = findViewById(R.id.slogan);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.logocolorbright));
        PreferencesManager preferencesManager = new PreferencesManager(getApplicationContext());
        requestQueue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent(); // Get the current Intent
                finish(); // Close the current activity
                overridePendingTransition(0, 0); // Disable close animation
                startActivity(intent); // Restart the activity
                overridePendingTransition(0, 0); // Disable start animation
            }
        });
        url = new Url();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url.getApp_checkup(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String serverContext = response.optString("sabaikomaster");
                Log.d("ServerContext", serverContext);
                if (serverContext.equalsIgnoreCase("running")) {
                    String jwt = preferencesManager.getJwtToken();
                    if (jwt.equals("Jwt_kali_xa")) {
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, SigningManager.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Smooth transition effect
                            finish();
                        }, SPLASH_DELAY);
                    } else {
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(MainActivity.this, DashBoardManager.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Smooth transition effect
                            finish();
                        }, SPLASH_DELAY);
                    }
                } else if (serverContext.equalsIgnoreCase("Developing")) {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(MainActivity.this, IntialServerError.class);
                        intent.putExtra("jsonobject", response.toString());
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Smooth transition effect
                        finish();
                    }, SPLASH_DELAY);
                }

                {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.INVISIBLE);
                restartBtn.setVisibility(View.VISIBLE);
                String message = "Unknown error";
                if (error instanceof AuthFailureError) {
                    message = "Email or Password is wrong";
                } else if (error instanceof NetworkError) {
                    message = "Network Error";
                } else if (error instanceof ServerError) {
                    message = "Server Error";
                } else if (error instanceof TimeoutError) {
                    message = "Timeout Error";
                }
                sloganTxt.setText(message);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }
}