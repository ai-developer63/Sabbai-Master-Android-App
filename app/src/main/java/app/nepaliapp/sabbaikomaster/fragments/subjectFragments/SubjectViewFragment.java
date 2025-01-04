package app.nepaliapp.sabbaikomaster.fragments.subjectFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.AllSubjectsFragment;
import app.nepaliapp.sabbaikomaster.tabcontroller.SubjectTabLayoutController;

public class SubjectViewFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    RequestQueue requestQueue;
    PreferencesManager preferencesManager;
    ImageButton backBtn;
    TextView subjectTitle, price;
    ImageView subjectLogoImage;
    DashBoardManager dashBoardManager;

    public SubjectViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DashBoardManager) {
            dashBoardManager = (DashBoardManager) context;
        } else {
            throw new ClassCastException(context + "must be DashBoardManager");
        }

        requestQueue = MySingleton.getInstance(context).getRequestQueue();
        preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_view, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutSubject);
        viewPager = view.findViewById(R.id.viewpager);
        backBtn = view.findViewById(R.id.backBtn);
        subjectTitle = view.findViewById(R.id.headingTopic);
        price = view.findViewById(R.id.price);
        subjectLogoImage = view.findViewById(R.id.subjectImage);
        Bundle bundle = getArguments();
        assert bundle != null;
        String subjectId = bundle.getString("subjectId");
        String fromwehere = bundle.getString("subjectBackHint","Description");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AllSubjectsFragment());
                dashBoardManager.setBottomNavigationSelected(2);
            }
        });

        handleBackPress();

        getSubjectData(subjectId);


setupViewPagerAndTabs(fromwehere);


        // Inflate the layout for this fragment
        return view;
    }

    private void setupViewPagerAndTabs(String positioner) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null) {
            SubjectTabLayoutController adapter = new SubjectTabLayoutController(requireActivity());
            viewPager.setAdapter(adapter);
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText("Description");
                } else if (position == 1) {
                    tab.setText("Topics");
                }
            }).attach();
            int position = getTabPosition(positioner);
            Log.d("TAG", "setupViewPagerAndTabs: "+position);
            viewPager.setCurrentItem(position, true);

        }
    }

    private int getTabPosition(String positioner){
        switch (positioner) {
            case "Description":
                return 0;
            case "Topics":
                return 1;
            default: return 0;
        }
    }


    private void handleBackPress() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                replaceFragment(new AllSubjectsFragment());
                dashBoardManager.setBottomNavigationSelected(2);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }


    private void getSubjectData(String subjectId) {
        Url url = new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getSubjectDetail(subjectId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                subjectTitle.setText(response.optString("name"));
                price.setText(response.optString("price"));
                HeaderPicasso.initializePicassoWithHeaders(requireContext(), "Authorization", "Bearer " + preferencesManager.getJwtToken());
                Picasso.get().load(response.optString("url")).into(subjectLogoImage);

                // Send data to fragments via adapter
                String description = response.optString("shortDescription", "No description available.");
                String topics = response.optString("longDescription", "No topics available.");
                SubjectTabLayoutController adapter = (SubjectTabLayoutController) viewPager.getAdapter();
                if (adapter != null) {
                    adapter.updateData(description, topics,response.optJSONArray("topics"));
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
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else if (error instanceof TimeoutError) {
                    message = "Timeout Error";
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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

    private void replaceFragment(Fragment fragment) {
        requireActivity().
                getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutInMain, fragment)
                .addToBackStack(null)
                .commit();
    }
}