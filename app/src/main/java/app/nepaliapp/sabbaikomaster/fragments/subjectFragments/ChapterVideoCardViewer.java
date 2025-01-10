package app.nepaliapp.sabbaikomaster.fragments.subjectFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import app.nepaliapp.sabbaikomaster.Adapter.VideoAdapterForVideoViewCard;
import app.nepaliapp.sabbaikomaster.R;

public class ChapterVideoCardViewer extends Fragment {

    RecyclerView recyclerView;
    TextView chapterName;
    ImageButton backBtn;
    String subjectId;

    public ChapterVideoCardViewer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chapter_videos, container, false);
        recyclerView = view.findViewById(R.id.chapterVideoCardRecycler);
        chapterName = view.findViewById(R.id.chapterName);
        backBtn = view.findViewById(R.id.backBtn);
        Bundle bundle = getArguments();
        try {
            assert bundle != null;
            String subTopics = bundle.getString("subTopics");

            String name = bundle.getString("name");
            chapterName.setText(name);
            JSONArray array = new JSONArray(subTopics);
            subjectId = array.getJSONObject(0).optString("subjectId");
            VideoAdapterForVideoViewCard videoAdapterForVideoViewCard = new VideoAdapterForVideoViewCard(requireContext(), array);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            recyclerView.setAdapter(videoAdapterForVideoViewCard);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectViewFragment subjectViewFragment = new SubjectViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("subjectId", subjectId);
                bundle.putString("subjectBackHint","Topics");
                subjectViewFragment.setArguments(bundle);
                replaceFragment(subjectViewFragment);
            }
        });


        return view;
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