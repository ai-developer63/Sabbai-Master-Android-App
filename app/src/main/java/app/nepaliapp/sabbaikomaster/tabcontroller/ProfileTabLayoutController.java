package app.nepaliapp.sabbaikomaster.tabcontroller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import app.nepaliapp.sabbaikomaster.tabLayoutFragments.sample1Fragment;
import app.nepaliapp.sabbaikomaster.tabLayoutFragments.sample2;

public class ProfileTabLayoutController extends FragmentStateAdapter {


    public ProfileTabLayoutController(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new sample1Fragment();
        } else if (position == 1) {
            return new sample2();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
