package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.SubjecAdapterWithDay;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;


public class UserCoursesFragment extends Fragment {
    Context context;
    RelativeLayout relativeLayout;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    PreferencesManager preferencesManager;
    Button enrollBtn;
    DashBoardManager dashBoardManager;
    ProgressBar progressDialog;
    public UserCoursesFragment() {
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

        this.context = context;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_courses, container, false);
        relativeLayout = view.findViewById(R.id.noUserCourseView);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        enrollBtn = view.findViewById(R.id.enrollBtn);
        progressDialog =  view.findViewById(R.id.progressBar);
        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AllSubjectsFragment());
                dashBoardManager.setBottomNavigationSelected(2);
            }
        });
        getPurchasedSubjects();
        // Inflate the layout for this fragment


        return view;
    }

    private void getPurchasedSubjects() {
        Url url = new Url();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url.getUserPurchasedSubject(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.setVisibility(View.VISIBLE);
                if (response.length() == 0) {
                    progressDialog.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    progressDialog.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    SubjecAdapterWithDay adapter = new SubjecAdapterWithDay(requireContext(), response, getParentFragmentManager());
                    recyclerView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Fetching Subjects got Error", Toast.LENGTH_SHORT).show();
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