package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.CircleTransform;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.tabcontroller.ProfileTabLayoutController;


public class ProfileFragment extends Fragment {
Context context;
    TextView UserName, UserEmail, UserPhone, SelectedClass;
    ImageView profileImage,logoutImageBtn;
    ViewPager2 viewPager2;
    TabLayout tab;
    Url url;
    RequestQueue requestQueue;
    PreferencesManager preferencesManager;
    private ShimmerFrameLayout shimmerFrameLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Initialization(view);
        getUserData();
        ProfileTabLayoutController adapter = new ProfileTabLayoutController(requireActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tab, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Routine");
            } else if (position == 1) {
                tab.setText("Contact Us");
            }
        }).attach();

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        logoutImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.ClearingUserDataForLogout();
                startActivity(new Intent(context.getApplicationContext(), MainActivity.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void Initialization(View view) {
        UserEmail = view.findViewById(R.id.userEmail);
        UserName = view.findViewById(R.id.userName);
        UserPhone = view.findViewById(R.id.userPhone);
        SelectedClass = view.findViewById(R.id.preparingClass);
        profileImage = view.findViewById(R.id.profileImage);
        logoutImageBtn = view.findViewById(R.id.logout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        viewPager2 = view.findViewById(R.id.viewpager);
        tab = view.findViewById(R.id.tabLayout);
        url = new Url();
    }

    private void getUserData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getProfile(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                UserName.setText(response.optString("name"));
                UserEmail.setText(response.optString("email"));
                UserPhone.setText(response.optString("phoneNumber"));
                if (response.optString("selectedClass").isEmpty()) {
                    SelectedClass.setText("Choose Class");
                } else {
                    SelectedClass.setText(response.optString("selectedClass"));
                }

                HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
                Picasso.get().load(response.optString("profileImage")).transform(new CircleTransform()).into(profileImage);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());

                return headers;
            }
        };
        requestQueue.add(request);
    }


}