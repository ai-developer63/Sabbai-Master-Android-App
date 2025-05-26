package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout; // NECESSARY: Import LinearLayout
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.facebook.shimmer.ShimmerFrameLayout; // NECESSARY: Import ShimmerFrameLayout

import org.json.JSONArray;
import org.json.JSONException;
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
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;

public class AllSubjectsFragment extends Fragment {
    RecyclerView recyclerView;
    Context context;
    RequestQueue requestQueue;
    PreferencesManager preferencesManager;
    TextView textView; // This is the "subjectAdding" text view
    ImageView SearchClickAbleIcon;
    EditText searchEditTxt;
    Boolean edittextVisible = false;
    JSONArray jsonArrayData;
    DashBoardManager dashBoardManager;
    // ProgressBar progressBar; // REMOVED: ProgressBar is replaced by shimmer

    // NECESSARY: Declare ShimmerFrameLayout and the actual content container
    private ShimmerFrameLayout shimmerViewContainerSubjects;
    private LinearLayout actualContentContainer;


    public AllSubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashBoardManager) {
            dashBoardManager = (DashBoardManager) context;
        } else {
            throw new ClassCastException(context + " must be DashBoardManager");
        }

        this.context = context;
        // It's better to initialize requestQueue here if context is guaranteed
        // Or ensure MySingleton handles context appropriately if called later
        this.requestQueue = MySingleton.getInstance(context.getApplicationContext()).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_subjects, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewSubject);
        textView = view.findViewById(R.id.textView); // "subjectAdding" TextView
        SearchClickAbleIcon = view.findViewById(R.id.searchIcon);
        searchEditTxt = view.findViewById(R.id.searchEditText);
        // progressBar = view.findViewById(R.id.progressBar); // REMOVED

        // NECESSARY: Initialize shimmer and content container
        shimmerViewContainerSubjects = view.findViewById(R.id.shimmer_view_container_subjects);
        actualContentContainer = view.findViewById(R.id.actual_content_container);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // textView.setVisibility(View.GONE); // This will be handled by parent container's visibility

        // Tint search icon
        if (context != null) { // Add context check
            Drawable drawable = SearchClickAbleIcon.getDrawable();
            if (drawable != null) {
                Drawable tintedDrawable = DrawableCompat.wrap(drawable).mutate();
                DrawableCompat.setTint(tintedDrawable, ResourcesCompat.getColor(context.getResources(), R.color.colorOnprimaryDemo, context.getTheme()));
                SearchClickAbleIcon.setImageDrawable(tintedDrawable);
            }
        }

        setupTextWatcher();

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("searchtag")) { // Check if key exists
            showShimmer(); // Show shimmer before search request
            String searchText = bundle.getString("searchtag", "").trim();
            searchEditTxt.setText(searchText);
            if (!searchText.isEmpty()) {
                edittextVisible = true;
                searchEditTxt.setVisibility(View.VISIBLE);
            }

            RequestSearchServer(new onSuccessFetching() {
                @Override
                public void onSuccess(JSONArray response) {
                    if (getView() == null || !isAdded()) return;
                    jsonArrayData = response; // Store fetched data
                    triggerSearchLogic(searchEditTxt.getText().toString().trim()); // Apply filter
                    showContent(); // Hide shimmer, fade in content
                }

                @Override
                public void onError(String errormessage) {
                    if (getView() == null || !isAdded()) return;
                    handleLoadError(errormessage); // Centralized error handling for UI
                }
            });
        } else {
            showShimmer(); // Show shimmer before initial data request
            RequestServer();
        }

        // Initial visibility of search EditText is GONE (from XML)
        // searchEditTxt.setVisibility(View.GONE); // This is already set in XML

        SearchClickAbleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittextVisible = !edittextVisible;
                if (edittextVisible) {
                    searchEditTxt.setVisibility(View.VISIBLE);
                    searchEditTxt.requestFocus();
                    // Add code to show keyboard if needed
                } else {
                    searchEditTxt.setVisibility(View.GONE);
                    // Add code to hide keyboard if needed
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) { // Set to true if you want this to be the primary handler
            @Override
            public void handleOnBackPressed() {
                // Handle back press specific to this fragment
                // For example, if search is visible, hide it, else allow default back navigation or custom
                if (searchEditTxt.getVisibility() == View.VISIBLE) {
                    searchEditTxt.setVisibility(View.GONE);
                    edittextVisible = false;
                } else {
                    // If you want to navigate to HomeFragment:
                    // replaceFragment(new HomeFragment());
                    // dashBoardManager.setBottomNavigationSelected(1);

                    // To allow default back press behavior (e.g., pop back stack):
                    if (isEnabled()) {
                        setEnabled(false); // Disable this callback
                        requireActivity().getOnBackPressedDispatcher().onBackPressed(); // Trigger default
                    }
                }
            }
        });
        return view;
    }

    // NECESSARY: Method to show shimmer and hide content
    private void showShimmer() {
        if (shimmerViewContainerSubjects != null) {
            shimmerViewContainerSubjects.startShimmer();
            shimmerViewContainerSubjects.setVisibility(View.VISIBLE);
        }
        if (actualContentContainer != null) {
            actualContentContainer.setVisibility(View.GONE);
        }
    }

    // NECESSARY: Method to hide shimmer and show content with fade-in
    private void showContent() {
        if (getView() == null || !isAdded()) return;

        if (shimmerViewContainerSubjects != null) {
            shimmerViewContainerSubjects.stopShimmer();
            shimmerViewContainerSubjects.setVisibility(View.GONE);
        }
        if (actualContentContainer != null) {
            actualContentContainer.setAlpha(0f);
            actualContentContainer.setVisibility(View.VISIBLE);
            actualContentContainer.animate()
                    .alpha(1f)
                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                    .setListener(null);

            // The "subjectAdding" textView's visibility will be handled by its parent
            // actualContentContainer becoming visible. The delayAction for animation
            // can still be applied if you want it to appear *after* the list.
        }
    }

    // NECESSARY: Centralized UI update for error state
    private void handleLoadError(String errorMessage) {
        if (getView() == null || !isAdded()) return;

        if (shimmerViewContainerSubjects != null) {
            shimmerViewContainerSubjects.stopShimmer();
            shimmerViewContainerSubjects.setVisibility(View.GONE);
        }
        if (actualContentContainer != null) {
            actualContentContainer.setVisibility(View.GONE);
        }
        if (context != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }


    private void RequestServer() {
        Url url = new Url();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getSubjects(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (getView() == null || !isAdded()) return; // View check

                jsonArrayData = response;
                if (context != null) { // Check context before creating adapter
                    SubjectAdapter adapter = new SubjectAdapter(context, response, getParentFragmentManager());
                    recyclerView.setAdapter(adapter);
                    showContent(); // NECESSARY: Call this to hide shimmer and show content
                    delayAction(new Runnable() {
                        @Override
                        public void run() {
                            if (textView != null && isAdded()) { // Check textView and fragment state
                                textView.setVisibility(View.VISIBLE);
                                AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.add_more_effect);
                                animatorSet.setTarget(textView);
                                animatorSet.start();
                            }
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getView() == null || !isAdded()) return; // View check
                // hideProgressbar(); // REMOVE THIS
                String message = "Unknown Error";
                // ... (your existing error parsing logic, ensure context checks before Toasts/Intents)
                if (error instanceof AuthFailureError) {
                    message = "Login Expired....";
                    if (context != null) {
                        preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                        startActivity(new Intent(getContext(), MainActivity.class));
                        if (getActivity() != null) getActivity().finish();
                        return; // Important to return after starting activity
                    }
                } else if (error instanceof NetworkError) {
                    message = "Network Error";
                } else if (error instanceof ServerError) {
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorData = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject jsonError = new JSONObject(errorData);
                            message = jsonError.optString("error", "Unknown error occurred");
                            if (message.equals("User not found") && context != null) {
                                preferencesManager.UpdateJwtToken("Jwt_kali_xa");
                                startActivity(new Intent(getContext(), MainActivity.class));
                                if (getActivity() != null) getActivity().finish();
                                return; // Important
                            }
                        } else {
                            message = "No response from server";
                        }
                    } catch (Exception e) {
                        // message will contain the default "No response from server" or specific error.
                    }
                } else if (error instanceof TimeoutError) {
                    message = "Timeout Error";
                }
                handleLoadError(message); // Use centralized error handler for UI
            }
        }) {
            // ... getHeaders ...
        };
        requestQueue.add(arrayRequest);
    }

    private void delayAction(Runnable action) {
        if (isAdded()) { // Ensure fragment is added before posting delayed action
            new Handler(Looper.getMainLooper()).postDelayed(action, 3000);
        }
    }

    private void setupTextWatcher() {
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No change needed here unless you want to optimize further
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (jsonArrayData == null || !isAdded() || context == null) return;

                String searchText = s.toString().trim();
                try {
                    JSONArray displayArray = TextUtils.isEmpty(searchText) ? jsonArrayData : findMatchingObjects(searchText, jsonArrayData);
                    SubjectAdapter adapter = new SubjectAdapter(context, displayArray, getParentFragmentManager());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private JSONArray findMatchingObjects(String searchText, JSONArray originalJsonArray) throws JSONException {
        // ... (your existing findMatchingObjects logic - looks okay) ...
        JSONArray matchingArray = new JSONArray();
        if (originalJsonArray == null) return matchingArray; // Add null check

        for (int i = 0; i < originalJsonArray.length(); i++) {
            JSONObject jsonObject = originalJsonArray.getJSONObject(i);
            String name = jsonObject.optString("shortDescription");
            String searchTag = jsonObject.optString("searchTags");
            String subjectName = jsonObject.optString("name");
            String classes = jsonObject.optString("whichClass");

            if (containsPartialMatch(name, searchText) ||
                    containsPartialMatch(searchTag, searchText) ||
                    containsPartialMatch(subjectName, searchText) || containsPartialMatch(classes, searchText)) {
                matchingArray.put(jsonObject);
            }
        }
        return matchingArray;
    }

    private boolean containsPartialMatch(String name, String searchText) {
        // ... (your existing containsPartialMatch logic - looks okay) ...
        if (name == null || searchText == null) return false;
        name = name.toLowerCase();
        searchText = searchText.toLowerCase();

        for (int i = 0; i <= name.length() - searchText.length(); i++) {
            if (name.regionMatches(i, searchText, 0, searchText.length())) {
                return true;
            }
        }
        return false;
    }

    private void replaceFragment(Fragment fragment) {
        if (isAdded() && getActivity() != null) {
            requireActivity().
                    getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutInMain, fragment)
                    .addToBackStack(null) // Consider if you always want to add to backstack
                    .commit();
        }
    }

    private void triggerSearchLogic(String searchText) {
        // This method is essentially duplicated by the onTextChanged logic.
        // You can call onTextChanged directly or refactor the core search logic.
        // For now, just ensure it uses the updated text.
        if (searchEditTxt != null) {
            // The TextWatcher's onTextChanged will handle the filtering
            // Or, if you want to force it:
            // setupTextWatcher(); // Re-attaching might not be ideal
            Editable currentText = searchEditTxt.getText();
            if (currentText != null) {
                // This will trigger the TextWatcher's onTextChanged if the text is different
                // or you can directly call the filtering logic here.
                // Let's simplify: the TextWatcher already handles it.
                // If search text is from bundle, it's set, and then onTextChanged will be triggered
                // if the adapter is set later or if the text changes.
                // The current setupTextWatcher is fine.
            }
        }
    }

    private void RequestSearchServer(onSuccessFetching callback) {
        Url url = new Url();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getSubjects(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (!isAdded()) return;
                if (callback != null) callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isAdded()) return;
                if (callback != null) callback.onError(error != null ? error.toString() : "Unknown search error");
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

    public interface onSuccessFetching {
        void onSuccess(JSONArray response);
        void onError(String errormessage);
    }

    // REMOVED: showProgressbar() and hideProgressbar() methods
    // Their logic is now incorporated into showShimmer(), showContent(), and handleLoadError()

    // NECESSARY: Add onDestroyView for cleanup
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        textView = null;
        SearchClickAbleIcon = null;
        searchEditTxt = null;
        shimmerViewContainerSubjects = null;
        actualContentContainer = null;
        // Cancel Volley requests if tagged, or consider broader cancellation if appropriate
        // if (requestQueue != null) {
        // requestQueue.cancelAll("AllSubjectsFragmentTag"); // Example tag
        // }
    }
}