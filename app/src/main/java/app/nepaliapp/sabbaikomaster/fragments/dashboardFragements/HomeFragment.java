package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.CategoryAdapter;
import app.nepaliapp.sabbaikomaster.Adapter.SubjectAdapter;
import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.CircleTransform;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;

public class HomeFragment extends Fragment {
    Context context;
    RequestQueue requestQueue;
    TextView welcomeTxt, classesRecyclerTxt, CoursesTxt, NewsTxt;
    ImageView logoImage;
    PreferencesManager preferencesManager;
    RecyclerView categoryRecyclerView, subjectRecyclerView;
    private ShimmerFrameLayout shimmerFrameLayout;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        welcomeTxt = view.findViewById(R.id.welcomeTxt);
        logoImage = view.findViewById(R.id.logoImage);
        categoryRecyclerView = view.findViewById(R.id.categoryRecycle);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        classesRecyclerTxt = view.findViewById(R.id.txtClasses);
        CoursesTxt = view.findViewById(R.id.txtCourses);
        NewsTxt = view.findViewById(R.id.latestNews);
        NewsTxt.setMaxLines(1);
        NewsTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        NewsTxt.setSingleLine(true);
        NewsTxt.setMarqueeRepeatLimit(10000);
        subjectRecyclerView = view.findViewById(R.id.subjectRecycle);
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        classesRecyclerTxt.setVisibility(View.GONE);
        CoursesTxt.setVisibility(View.GONE);
        requestHomeData();
        return view;
    }

    private void requestHomeData() {
        Url url = new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getHome(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                shimmerFrameLayout.setVisibility(View.VISIBLE); // Show shimmer
                categoryRecyclerView.setVisibility(View.GONE);
                subjectRecyclerView.setVisibility(View.GONE);
                String name = response.optString("name");
                String profileUrl = response.optString("profileImg");
                welcomeTxt.setText(getGreetingMessage() + "\n" + "               " + name);
                HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
                Picasso.get().load(profileUrl).transform(new CircleTransform()).into(logoImage);
                CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), response.optJSONArray("classes"), getParentFragmentManager());
                categoryRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
                categoryRecyclerView.setAdapter(categoryAdapter);
                SubjectAdapter adapter = new SubjectAdapter(context, response.optJSONArray("subjects"),getParentFragmentManager());
                subjectRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                subjectRecyclerView.setAdapter(adapter);
                NewsTxt.setText(response.optString("news"));
                NewsTxt.setSelected(true);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                classesRecyclerTxt.setVisibility(View.VISIBLE);
                categoryRecyclerView.setVisibility(View.VISIBLE);
                subjectRecyclerView.setVisibility(View.VISIBLE);
                NewsTxt.setVisibility(View.VISIBLE);
                CoursesTxt.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
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
                            String errorData = new String(error.networkResponse.data, "UTF-8");

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

        requestQueue.add(request);
    }


    private String getGreetingMessage() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour <= 11) {
            return "Good Morning,";
        } else if (hour >= 12 && hour <= 16) {
            return "Good Afternoon,";
        } else if (hour >= 17 && hour <= 20) {
            return "Good Evening,";
        } else {
            return "Good Luck,";
        }
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] nameParts = fullName.trim().split(" ");
        return nameParts.length > 0 ? nameParts[0] : "";
    }

    private void delayAction(Runnable action) {
        new Handler(Looper.getMainLooper()).postDelayed(action, 5000);  // 2 seconds delay
    }
}