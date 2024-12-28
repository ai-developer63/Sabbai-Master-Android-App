package app.nepaliapp.sabbaikomaster.fragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.DeviceIdChecker;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.AllSubjectsFragment;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.HomeFragment;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.NotificationsFragment;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.ProfileFragment;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.UserCoursesFragment;
import app.nepaliapp.sabbaikomaster.fragments.subjectFragments.SubjectViewFragment;

public class DashBoardManager extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board_manager);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DeviceIdChecker(getApplicationContext()));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.logocolor));

        // Find views
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        replaceFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    replaceFragment(new HomeFragment());
                } else if (itemId == R.id.AllSubjectsFragment) {
                    replaceFragment(new AllSubjectsFragment());
                } else if (itemId == R.id.UserCoursesFragment) {
                    replaceFragment(new UserCoursesFragment());
                } else if (itemId == R.id.NotificationsFragment) {
                    replaceFragment(new NotificationsFragment());
                } else if (itemId == R.id.ProfileFragment) {
                    replaceFragment(new ProfileFragment());
                } else {
                    replaceFragment(new HomeFragment());
                }

                return true;
            }
        });



        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                    showExitAlertDialog();

            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardManager.this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> close());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void close() {
        finishAffinity();
        System.exit(0);
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void setBottomNavigationSelected(int position) {
        int menuItemId = getMenuItemIdForPosition(position); // Convert position to menu item ID
        bottomNavigationView.setSelectedItemId(menuItemId); // Update the selected item
    }

    private void handleUnknownFragment(int itemId) {
        if (itemId == R.id.home) {
            replaceFragment(new HomeFragment());
        } else if (itemId == R.id.AllSubjectsFragment) {
            replaceFragment(new AllSubjectsFragment());
        } else if (itemId == R.id.UserCoursesFragment) {
            replaceFragment(new UserCoursesFragment());
        } else if (itemId == R.id.NotificationsFragment) {
            replaceFragment(new NotificationsFragment());
        } else if (itemId == R.id.ProfileFragment) {
            replaceFragment(new ProfileFragment());
        } else {
            Log.e("UnknownFragment", "No fallback fragment available for item ID: " + itemId);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutInMain, fragment)
                .addToBackStack(null)
                .commit();
    }
    private int getMenuItemIdForPosition(int position) {
        switch (position) {
            case 1: return R.id.home;
            case 2: return R.id.AllSubjectsFragment;
            case 3: return R.id.UserCoursesFragment;
            case 4: return R.id.NotificationsFragment;
            case 5: return R.id.ProfileFragment;
            default: return R.id.home; // Default to Home if position is invalid
        }
    }


}
