package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.tabcontroller.NotificationsTabLayoutController;
import app.nepaliapp.sabbaikomaster.tabcontroller.ProfileTabLayoutController;

public class NotificationsFragment extends Fragment {

    ViewPager2 viewPager2;
    TabLayout tab;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);
        viewPager2 = view.findViewById(R.id.viewpager);
        tab = view.findViewById(R.id.tabLayout);
        NotificationsTabLayoutController adapter = new NotificationsTabLayoutController(requireActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tab, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Personal Notification");
            } else if (position == 1) {
                tab.setText("Youtube Videos");
            }
        }).attach();



        return view;



    }
}