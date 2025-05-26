package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.ToastUtils;
import app.nepaliapp.sabbaikomaster.tabcontroller.NotificationsTabLayoutController;
import app.nepaliapp.sabbaikomaster.tabcontroller.ProfileTabLayoutController;

public class NotificationsFragment extends Fragment {

    ViewPager2 viewPager2;
    TabLayout tab;
    CardView cardView;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_notifications, container, false);
        viewPager2 = view.findViewById(R.id.viewpager);
        tab = view.findViewById(R.id.tabLayout);
        cardView = view.findViewById(R.id.scheduleCard);
        NotificationsTabLayoutController adapter = new NotificationsTabLayoutController(requireActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tab, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Personal Notification");
            } else if (position == 1) {
                tab.setText("Youtube Videos");
            }
        }).attach();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showToast(requireContext(),"Update are on the way");
            }
        });


        return view;



    }
}