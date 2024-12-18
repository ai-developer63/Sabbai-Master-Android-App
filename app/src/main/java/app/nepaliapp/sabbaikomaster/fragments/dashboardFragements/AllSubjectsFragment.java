package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.SubjectAdapter;
import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;

public class AllSubjectsFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    RequestQueue requestQueue;
    PreferencesManager preferencesManager;
    TextView textView;
    public AllSubjectsFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = new MySingleton(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_subjects, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSubject);
        textView= view.findViewById(R.id.textView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RequestServer();
        textView.setVisibility(View.GONE);



        // Inflate the layout for this fragment
        return view;
    }

    private void RequestServer() {
        Url url = new Url();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getSubjects(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                SubjectAdapter adapter = new SubjectAdapter(context, response,getParentFragmentManager());
                recyclerView.setAdapter(adapter);
                delayAction(new Runnable() {
                    @Override
                    public void run() {
                        textView.setVisibility(View.VISIBLE);
                        AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.add_more_effect);
                        animatorSet.setTarget(textView);
                        animatorSet.start();
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String message = "Unknown Error";
                if (error instanceof AuthFailureError) {
                    message = "Login Expired....";
                    preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                    startActivity(new Intent(getContext(), MainActivity.class));
                } else if (error instanceof NetworkError) {
                    message = "Network Error";
                } else if (error instanceof ServerError) {
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            // Convert the error data to a string
                            String errorData = new String(error.networkResponse.data, StandardCharsets.UTF_8);

                            // Parse the error JSON
                            JSONObject jsonError = new JSONObject(errorData);

                            message = jsonError.optString("error", "Unknown error occurred");
                            if (message.equals("User not found")) {
                                preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                                startActivity(new Intent(getContext(), MainActivity.class));
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(arrayRequest);

    }
    private void delayAction(Runnable action) {
        new Handler(Looper.getMainLooper()).postDelayed(action, 3000);  // 2 seconds delay
    }
}