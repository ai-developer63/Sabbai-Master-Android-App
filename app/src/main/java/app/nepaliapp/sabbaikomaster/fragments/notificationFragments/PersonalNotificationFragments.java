package app.nepaliapp.sabbaikomaster.fragments.notificationFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nepaliapp.sabbaikomaster.R;

public class PersonalNotificationFragments extends Fragment {

    RecyclerView recyclerView;
    public PersonalNotificationFragments() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_notification_fragments, container, false);
recyclerView = view.findViewById(R.id.personalNotification);
recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));



        return view;
    }
}