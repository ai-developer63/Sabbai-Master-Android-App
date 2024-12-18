package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import app.nepaliapp.sabbaikomaster.R;


public class UserCoursesFragment extends Fragment {
    Context context;
    RelativeLayout relativeLayout;

    public UserCoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_courses, container, false);
        relativeLayout = view.findViewById(R.id.noUserCourseView);

        // Inflate the layout for this fragment
        return view;
    }
}