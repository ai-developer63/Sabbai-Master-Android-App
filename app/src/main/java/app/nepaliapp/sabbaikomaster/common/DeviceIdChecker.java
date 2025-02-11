package app.nepaliapp.sabbaikomaster.common;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.MainActivity;

public class DeviceIdChecker implements DefaultLifecycleObserver {

    PreferencesManager preferencesManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private RequestQueue requestQueue;
    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            ReceieveDeviceId();
            // Schedule again after 10 minutes
            handler.postDelayed(this, 60 * 1000); // 10 minutes
        }
    };

    public DeviceIdChecker(Context context) {
        this.context = context.getApplicationContext();
        this.requestQueue = MySingleton.getInstance(this.context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        handler.post(periodicTask);
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        handler.removeCallbacks(periodicTask);
    }


    private void ReceieveDeviceId() {
        Url url = new Url();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url.getDeviceIdFetch(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String deviceId = response.optString("deviceId");
                Log.d("DeviceIdChecker", "Device ID: " + deviceId);
                Log.d("DeviceIdChecker", "Device ID: " + preferencesManager.getDeviceUniqueID());
                if (!deviceId.equals(preferencesManager.getDeviceUniqueID())) {
                    preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DeviceIdChecker", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());
                Log.d("TAG", headers.toString());
                return headers;
            }

        };
        requestQueue.add(objectRequest);
    }
}
