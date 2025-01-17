package app.nepaliapp.sabbaikomaster.common;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.R;

public class UserClassSelector {
    public static void show(Context context,String[] classes) {
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        final String[] messagetext = new String[1];
        // Create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
         String normalText= "Add Classes in order to get Exam Routines and Notification Regarding it.";
        // Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_subscription, null);


        // Get references to UI elements
        Spinner spinnerSubscription = dialogView.findViewById(R.id.spinner_subscription);
        Button saveClass = dialogView.findViewById(R.id.button_subscribe);
        ImageView closeBtn = dialogView.findViewById(R.id.closeicon);
        TextView messageToSelectClass = dialogView.findViewById(R.id.packexplainer);
        messageToSelectClass.setText(normalText);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, classes);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSubscription.setAdapter(adapter);

        // Set an item selected listener on the spinner
        spinnerSubscription.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPackage = classes[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set the dialog view
        builder.setView(dialogView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
Url url = new Url();
PreferencesManager preferencesManager = new PreferencesManager(context);
        // Set click listener for the subscribe button
        saveClass.setOnClickListener(v -> {
            String selectedClass = spinnerSubscription.getSelectedItem().toString();
            url.getUpdateClass(encode(selectedClass));
            StringRequest request = new StringRequest(Request.Method.GET, url.getUpdateClass(encode(selectedClass)), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  if (response.equalsIgnoreCase("done")){

                      Toast.makeText(context, "Class Updated", Toast.LENGTH_SHORT).show();
                      dialog.dismiss();
                  }else {
                      Toast.makeText(context, "Sorry Class Didn't update Try Again", Toast.LENGTH_SHORT).show();
                  }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("something", url.getUpdateClass(encode(selectedClass)));
                    String message = "Unknown Error";
                    if (error instanceof AuthFailureError) {
                        message = "Login Expired....";
                        preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                    } else if (error instanceof NetworkError) {
                        message = "Network Error";
                    } else if (error instanceof ServerError) {
                        try {
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                // Convert the error data to a string
                                String errorData = new String(error.networkResponse.data, "UTF-8");
                                // Parse the error JSON
                                JSONObject jsonError = new JSONObject(errorData);

                                message = jsonError.optString("error", "Unknown error occurred");
                                if (message.equals("User not found")) {
                                    preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                                }

                            } else {
                                message = "No response from server";
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } else if (error instanceof TimeoutError) {
                        message = "Timeout Error";
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            })

                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());
                    return headers;
                }

            };
            requestQueue.add(request);
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public static String encode(String value) {
        try {
            // Encode the value with UTF-8 encoding
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return value; // Return the original value in case of error
        }
    }
}

