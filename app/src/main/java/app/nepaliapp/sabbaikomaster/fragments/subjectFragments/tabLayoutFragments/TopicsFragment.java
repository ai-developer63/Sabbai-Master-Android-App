package app.nepaliapp.sabbaikomaster.fragments.subjectFragments.tabLayoutFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.Adapter.TopicsAdapter;
import app.nepaliapp.sabbaikomaster.R;

public class TopicsFragment extends Fragment {

    RecyclerView recyclerView;


    public TopicsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_topics, container, false);
 recyclerView = view.findViewById(R.id.topicsViews);
 recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Bundle args = getArguments();
        if (args != null) {
            String topicsDataString = args.getString("topicsData");
            try {
                JSONArray topicsJSONArray = new JSONArray(topicsDataString);
                Log.d("topicsJSONArray1", topicsJSONArray.toString());
                updateTopics(topicsJSONArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Inflate the layout for this fragment
        return view;
    }
    public void updateTopics(JSONArray topicsJSONArray) {
        // Handle the received JSONArray
        if (topicsJSONArray != null) {
            try {
                Log.d("TAG", "updateTopics: came here and Data is "+topicsJSONArray);
                // Parse and use the topics JSONArray (example)
                TopicsAdapter topicsAdapter = new TopicsAdapter(requireContext(), topicsJSONArray,requireActivity().getSupportFragmentManager());
                recyclerView.setAdapter(topicsAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}