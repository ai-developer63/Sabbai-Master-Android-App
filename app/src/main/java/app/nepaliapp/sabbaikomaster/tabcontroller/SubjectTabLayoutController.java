package app.nepaliapp.sabbaikomaster.tabcontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.json.JSONArray;

import app.nepaliapp.sabbaikomaster.fragments.subjectFragments.tabLayoutFragments.DescriptionFragment;
import app.nepaliapp.sabbaikomaster.fragments.subjectFragments.tabLayoutFragments.TopicsFragment;

public class SubjectTabLayoutController extends FragmentStateAdapter {
    private String shortDescription = "";
    private String longDescription = "";
    private JSONArray topicsJSONArray;
    private final FragmentManager fragmentManager;
    public SubjectTabLayoutController(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            DescriptionFragment descriptionFragment = new DescriptionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("shortDescription", shortDescription);
            bundle.putString("longDescription", longDescription);
            descriptionFragment.setArguments(bundle);
            return descriptionFragment;
        } else if (position == 1) {
            TopicsFragment topicsFragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            if (topicsJSONArray != null) {
                bundle.putString("topicsData", topicsJSONArray.toString());
            }
            topicsFragment.setArguments(bundle);
            return topicsFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(String shortDescription, String longDescription, JSONArray topicsJSONArray) {
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.topicsJSONArray = topicsJSONArray;

        // Update DescriptionFragment if it's already created
        Fragment descriptionFragment = fragmentManager.findFragmentByTag("f" + 0); // "f0" is the tag for the first fragment
        if (descriptionFragment instanceof DescriptionFragment) {
            ((DescriptionFragment) descriptionFragment).updateDescription(shortDescription, this.longDescription);
        }

        // Update TopicsFragment if it's already created
        Fragment topicsFragment = fragmentManager.findFragmentByTag("f" + 1); // "f1" is the tag for the second fragment
        if (topicsFragment instanceof TopicsFragment) {
            ((TopicsFragment) topicsFragment).updateTopics(topicsJSONArray);
        }

        notifyDataSetChanged();
    }
}
