package app.nepaliapp.sabbaikomaster.fragments.subjectFragments.tabLayoutFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import app.nepaliapp.sabbaikomaster.Adapter.TopicsAdapter;
import app.nepaliapp.sabbaikomaster.R;


public class TopicsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TopicsAdapter topicsAdapter;

    public TopicsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topics, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.topicsViews);
        progressBar = view.findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load data from arguments
        loadDataFromArguments();

        return view;
    }

    private void loadDataFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            String topicsDataString = args.getString("topicsData");

            if (topicsDataString != null && !topicsDataString.isEmpty()) {
                // Data is available, parse and display it
                try {
                    JSONArray topicsJSONArray = new JSONArray(topicsDataString);
                    updateTopics(topicsJSONArray);
                } catch (JSONException e) {
                    Log.e("TopicsFragment", "Error parsing topics data", e);
                }
            } else {
                // Data is not available, show loading indicator
                progressBar.setVisibility(View.VISIBLE);
                Log.d("TopicsFragment", "Topics data not ready yet.");
            }
        }
    }

    public void updateTopics(JSONArray topicsJSONArray) {
        if (topicsJSONArray != null && topicsJSONArray.length() > 0) {
            progressBar.setVisibility(View.GONE);

            // Update RecyclerView with new data
            topicsAdapter = new TopicsAdapter(requireContext(), topicsJSONArray, requireActivity().getSupportFragmentManager());
            recyclerView.setAdapter(topicsAdapter);

            Log.d("TopicsFragment", "Topics updated: " + topicsJSONArray.toString());
        } else {
            Log.e("TopicsFragment", "No topics to display.");
        }
    }
}
