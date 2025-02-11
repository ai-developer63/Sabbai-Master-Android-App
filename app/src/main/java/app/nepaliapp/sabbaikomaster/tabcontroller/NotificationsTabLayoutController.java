package app.nepaliapp.sabbaikomaster.tabcontroller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import app.nepaliapp.sabbaikomaster.fragments.notificationFragments.PersonalNotificationFragments;
import app.nepaliapp.sabbaikomaster.fragments.notificationFragments.YoutubeVideoFragments;
import app.nepaliapp.sabbaikomaster.fragments.profileTab.ContactUs;
import app.nepaliapp.sabbaikomaster.fragments.profileTab.LearningAchivementsFragments;

public class NotificationsTabLayoutController extends FragmentStateAdapter {

    public NotificationsTabLayoutController(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PersonalNotificationFragments();
        } else if (position == 1) {
            return new YoutubeVideoFragments();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
