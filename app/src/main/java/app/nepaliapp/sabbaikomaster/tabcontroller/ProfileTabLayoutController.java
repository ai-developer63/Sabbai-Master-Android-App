package app.nepaliapp.sabbaikomaster.tabcontroller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import app.nepaliapp.sabbaikomaster.fragments.profileTab.ContactUs;
import app.nepaliapp.sabbaikomaster.fragments.profileTab.LearningAchivementsFragments;
import app.nepaliapp.sabbaikomaster.tabLayoutFragments.sample1Fragment;

public class ProfileTabLayoutController extends FragmentStateAdapter {


    public ProfileTabLayoutController(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LearningAchivementsFragments();
        } else if (position == 1) {
            return new ContactUs();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
