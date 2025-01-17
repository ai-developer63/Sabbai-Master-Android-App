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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import com.android.volley.toolbox.JsonArrayRequest;

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
    TextView textView;
    ImageView  SearchClickAbleIcon;
    EditText searchEditTxt;
    Boolean edittextVisible = false;
    JSONArray jsonArrayData;
    DashBoardManager dashBoardManager;
    public AllSubjectsFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashBoardManager) {
            dashBoardManager = (DashBoardManager) context;
        } else {
            throw new ClassCastException(context + "must be DashBoardManager");
        }

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
        SearchClickAbleIcon = view.findViewById(R.id.searchIcon);
        Drawable drawable = SearchClickAbleIcon.getDrawable();
        Drawable tintedDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(tintedDrawable, ResourcesCompat.getColor(context.getResources(), R.color.colorOnprimaryDemo, context.getTheme()));
        SearchClickAbleIcon.setImageDrawable(tintedDrawable);
        searchEditTxt = view.findViewById(R.id.searchEditText);
        searchEditTxt.setVisibility(View.GONE);
        setupTextWatcher();
        SearchClickAbleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittextVisible = !edittextVisible;
                if (edittextVisible ) {
                    searchEditTxt.setVisibility(View.VISIBLE);
                } else {
                    searchEditTxt.setVisibility(View.GONE);
                }
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                replaceFragment(new HomeFragment());
                dashBoardManager.setBottomNavigationSelected(1);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void RequestServer() {
        Url url = new Url();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getSubjects(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonArrayData = response;
                if (isAdded()) {
                    SubjectAdapter adapter = new SubjectAdapter(context, response, getParentFragmentManager());
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

    private void setupTextWatcher() {
        searchEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (jsonArrayData == null) return; // Ensure data is available

                String searchText = s.toString().trim();

                if (!TextUtils.isEmpty(searchText)) {
                    try {
                        JSONArray matchingArray = findMatchingObjects(searchText, jsonArrayData);
                        if (isAdded()) { // Check if fragment is still added
                            SubjectAdapter adapter = new SubjectAdapter(context, matchingArray, getParentFragmentManager());
                            recyclerView.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (isAdded()) { // Check if fragment is still added
                        SubjectAdapter adapter = new SubjectAdapter(context, jsonArrayData, getParentFragmentManager());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private JSONArray findMatchingObjects(String searchText, JSONArray originalJsonArray) throws JSONException {
        JSONArray matchingArray = new JSONArray();

        for (int i = 0; i < originalJsonArray.length(); i++) {
            JSONObject jsonObject = originalJsonArray.getJSONObject(i);
            String name = jsonObject.optString("shortDescription");
            if (containsPartialMatch(name, searchText)) {
                matchingArray.put(jsonObject);
            }
        }

        return matchingArray;
    }

    private boolean containsPartialMatch(String name, String searchText) {
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
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutInMain, fragment)
                .addToBackStack(null)
                .commit();
    }
}