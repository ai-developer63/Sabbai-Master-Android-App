package app.nepaliapp.sabbaikomaster.fragments.subjectFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.tabcontroller.SubjectTabLayoutController;

public class SubjectViewFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    public SubjectViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_view, container, false);
        tabLayout = view.findViewById(R.id.tabLayoutSubject);
        viewPager2 = view.findViewById(R.id.viewpager);
        SubjectTabLayoutController adapter = new SubjectTabLayoutController(requireActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Description");
            } else if (position == 1) {
                tab.setText("Topics");
            }
        }).attach();

        // Inflate the layout for this fragment
        return view;
    }



}