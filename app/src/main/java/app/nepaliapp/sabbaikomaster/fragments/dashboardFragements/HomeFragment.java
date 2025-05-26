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
import androidx.core.widget.NestedScrollView; // NECESSARY CHANGE: Import NestedScrollView
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.ClassAdapter;
import app.nepaliapp.sabbaikomaster.Adapter.SubjectAdapter;
import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.CircleTransform;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.common.UserClassSelector;
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;

public class HomeFragment extends Fragment {
    Context context;
    RequestQueue requestQueue;
    TextView welcomeTxt, classesRecyclerTxt, CoursesTxt, NewsTxt;
    ImageView logoImage;
    PreferencesManager preferencesManager;
    RecyclerView categoryRecyclerView, subjectRecyclerView;
    DashBoardManager dashBoardManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Runnable delayedActionRunnable;
    private boolean isPopupShown = false;

    // NECESSARY CHANGE: Declare NestedScrollView for your main content
    private NestedScrollView mainContentScrollView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof DashBoardManager) {
            dashBoardManager = (DashBoardManager) context;
        } else {
            throw new ClassCastException(context + "must be DashBoardManager");
        }
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

        // NECESSARY CHANGE: Initialize mainContentScrollView
        mainContentScrollView = view.findViewById(R.id.main_content_scrollview);

        NewsTxt.setMaxLines(1);
        NewsTxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        NewsTxt.setSingleLine(true);
        NewsTxt.setMarqueeRepeatLimit(10000);
        subjectRecyclerView = view.findViewById(R.id.subjectRecycle);

        // Initial UI State
        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        // mainContentScrollView is GONE by default from XML (android:visibility="gone")
        // Individual elements inside mainContentScrollView can be GONE initially too,
        // but their parent (mainContentScrollView) is the main controller for visibility here.
        classesRecyclerTxt.setVisibility(View.GONE); // These are children of mainContentScrollView
        CoursesTxt.setVisibility(View.GONE);         // So they will only show if mainContentScrollView is VISIBLE

        requestHomeData();
        logoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashBoardManager.setBottomNavigationSelected(5);
                replaceFragment(new ProfileFragment());
            }
        });
        return view;
    }

    private void requestHomeData() {
        Url url = new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getHome(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // NECESSARY CHECK: Ensure the fragment's view is still available
                if (getView() == null || !isAdded()) {
                    return; // Fragment's view is destroyed, do nothing
                }

                // The rest of your onResponse code
                // The line below was likely causing the crash if shimmerFrameLayout was null
                // shimmerFrameLayout.setVisibility(View.VISIBLE); // You can remove this line as shimmer is already visible from onCreateView

                // It's also safer to remove these if their parent (mainContentScrollView) is already GONE
                // categoryRecyclerView.setVisibility(View.GONE);
                // subjectRecyclerView.setVisibility(View.GONE);

                String name = response.optString("name");
                String profileUrl = response.optString("profileImg");
                // It's also good to check if views are null before using them,
                // though the check above should mostly cover it.
                if (welcomeTxt != null) {
                    welcomeTxt.setText(getGreetingMessage() + "\n" + "               " + name);
                }
                HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
                if (logoImage != null) {
                    Picasso.get().load(profileUrl).transform(new CircleTransform()).into(logoImage);
                }

                if (getContext() != null && categoryRecyclerView != null && subjectRecyclerView != null) {
                    ClassAdapter classAdapter = new ClassAdapter(getContext(), response.optJSONArray("classes"), getParentFragmentManager());
                    GridLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
                    categoryRecyclerView.setLayoutManager(layoutManager);
                    categoryRecyclerView.setAdapter(classAdapter);

                    SubjectAdapter adapter = new SubjectAdapter(context, response.optJSONArray("subjects"), getParentFragmentManager());
                    subjectRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    subjectRecyclerView.setAdapter(adapter);
                }

                if (NewsTxt != null) {
                    NewsTxt.setText(response.optString("news"));
                    NewsTxt.setSelected(true);
                }


                // UI updates after successful data load
                if (shimmerFrameLayout != null) { // Always good to check
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }

                if (mainContentScrollView != null) {
                    mainContentScrollView.setAlpha(0f);
                    mainContentScrollView.setVisibility(View.VISIBLE);
                    mainContentScrollView.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }

                // Now set its children to visible
                if (classesRecyclerTxt != null) classesRecyclerTxt.setVisibility(View.VISIBLE);
                if (categoryRecyclerView != null) categoryRecyclerView.setVisibility(View.VISIBLE);
                if (subjectRecyclerView != null) subjectRecyclerView.setVisibility(View.VISIBLE);
                if (NewsTxt != null) NewsTxt.setVisibility(View.VISIBLE);
                if (CoursesTxt != null) CoursesTxt.setVisibility(View.VISIBLE);

                if (response.optString("news").equalsIgnoreCase("No message")) {
                    JSONArray classes = response.optJSONArray("classes");
                    if (classes != null) { // Added null check for classes
                        String[] classesName = new String[classes.length() + 1];
                        for (int i = 0; i < classes.length(); i++) {
                            JSONObject object = classes.optJSONObject(i);
                            classesName[i] = object.optString("name", "unknown");
                            if (i == classes.length() - 1) {
                                classesName[i + 1] = "None of Above";
                            }
                        }
                        delayAction(() -> {
                            if (isAdded() && context != null) {
                                UserClassSelector.show(requireContext(), classesName);
                            }
                        });
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // NECESSARY CHECK: Ensure the fragment's view is still available
                if (getView() == null || !isAdded()) {
                    return; // Fragment's view is destroyed, do nothing
                }

                if (shimmerFrameLayout != null) { // Always good to check
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                if (mainContentScrollView != null) {
                    mainContentScrollView.setVisibility(View.GONE);
                }

                String message = "Unknown Error";
                // ... (rest of your onErrorResponse logic) ...
                // Ensure context checks or isAdded() checks before Toasts or starting activities
                if (isAdded() && context != null) {
                    if (error instanceof AuthFailureError) {
                        message = "Login Expired....";
                        preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                        startActivity(new Intent(getContext(), MainActivity.class));
                        // No need for Toast here as you are navigating away
                        return; // Exit after starting activity
                    } else if (error instanceof NetworkError) {
                        message = "Network Error";
                    } else if (error instanceof ServerError) {
                        try {
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String errorData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject jsonError = new JSONObject(errorData);
                                message = jsonError.optString("error", "Unknown error occurred");
                                if (message.equals("User not found")) {
                                    preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    return; // Exit after starting activity
                                }
                            } else {
                                message = "No response from server";
                            }
                        } catch (Exception e) {
                            // message variable will hold the default or parsed message
                        }
                    } else if (error instanceof TimeoutError) {
                        message = "Timeout Error";
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
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

    // getFirstName method was unused, so I've removed it for brevity.
    // If you need it, you can add it back.
    // private String getFirstName(String fullName) { ... }


    private void delayAction(Runnable action) {
        // Check if fragment is still added to an activity and context is not null
        if (!isAdded() || context == null) {
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        if (!isPopupShown) {
            isPopupShown = true;
            delayedActionRunnable = action; // Storing the runnable if you need to cancel it
            handler.postDelayed(() -> {
                if (isAdded() && context != null) { // Double check before running
                    action.run();
                }
                isPopupShown = false; // Reset after showing or attempting to show
            }, 5000); // 5 seconds delay
        }
    }

    private void replaceFragment(Fragment fragment) {
        if (isAdded() && getActivity() != null) { // Ensure fragment is added and activity is available
            requireActivity().
                    getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutInMain, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up to prevent memory leaks, e.g., cancel network requests or remove handlers
        if (requestQueue != null) {
            requestQueue.cancelAll(this::equals); // Example: Cancel requests tagged with this fragment
        }
        Handler handler = new Handler(Looper.getMainLooper());
        if (delayedActionRunnable != null) {
            handler.removeCallbacks(delayedActionRunnable); // Remove pending callbacks
        }
        // Nullify views to help GC
        welcomeTxt = null;
        logoImage = null;
        categoryRecyclerView = null;
        subjectRecyclerView = null;
        shimmerFrameLayout = null;
        mainContentScrollView = null;
        classesRecyclerTxt = null;
        CoursesTxt = null;
        NewsTxt = null;
    }
}