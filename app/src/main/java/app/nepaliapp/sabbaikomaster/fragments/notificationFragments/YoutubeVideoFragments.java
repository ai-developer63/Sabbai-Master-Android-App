package app.nepaliapp.sabbaikomaster.fragments.notificationFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nepaliapp.sabbaikomaster.R;

public class YoutubeVideoFragments extends Fragment {

RecyclerView recyclerView;
    public YoutubeVideoFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube_video_fragments, container, false);
        recyclerView = view.findViewById(R.id.youtubeVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        // Inflate the layout for this fragment
        return view;
    }
}