package app.nepaliapp.sabbaikomaster.fragments.profileTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Adapter.SubjecAdapterWithDay;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;

public class LearningAchivementsFragments extends Fragment {
    ProgressBar progressDialog;
    MaterialCardView coursesCard;
    PreferencesManager preferencesManager;
    RequestQueue requestQueue;
    RecyclerView recyclerView;

    public LearningAchivementsFragments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learning_achivements_fragments, container, false);
        requestQueue = MySingleton.getInstance(requireContext()).getRequestQueue();
        progressDialog = view.findViewById(R.id.progressBar);
        coursesCard = view.findViewById(R.id.courselist);
        preferencesManager = new PreferencesManager(requireContext());
        recyclerView = view.findViewById(R.id.purchasedCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        getPurchasedSubjects();
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
                } else {
                    progressDialog.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    SubjecAdapterWithDay adapter = new SubjecAdapterWithDay(requireContext(), response, getParentFragmentManager());
                    recyclerView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(requireContext(), "Fetching Subjects got Error", Toast.LENGTH_SHORT).show();
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