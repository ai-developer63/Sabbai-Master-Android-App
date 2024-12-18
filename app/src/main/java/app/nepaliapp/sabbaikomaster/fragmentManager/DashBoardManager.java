package app.nepaliapp.sabbaikomaster.fragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        // Get NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frameLayoutInMain);

        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment is null. Check your layout or IDs.");
        }

        // Get NavController
        NavController navController = navHostFragment.getNavController();
        Log.d("NavController", "Current Destination: " + navController.getCurrentDestination());


        // Setup BottomNavigationView with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Custom system to fallback to manual fragment transactions
        bottomNavigationView.setOnItemSelectedListener(item -> {
            boolean handled = false;
            try {
                handled = NavigationUI.onNavDestinationSelected(item, navController);
            } catch (IllegalArgumentException e) {
                Log.e("NavController", "Unknown Fragment. Fallback to manual loading.", e);
                handleUnknownFragment(item.getItemId()); // Call fallback method
                return true; // Return true to indicate manual handling
            }
            return handled;
        });


        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    // If backstack is empty, show exit dialog
                    showExitAlertDialog();
                }
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

    private void handleUnknownFragment(int itemId) {
        if (itemId == R.id.home) {
            replaceFragment(new HomeFragment(), "HomeFragment");
        } else if (itemId == R.id.AllSubjectsFragment) {
            replaceFragment(new AllSubjectsFragment(), "AllSubjectsFragment");
        } else if (itemId == R.id.UserCoursesFragment) {
            replaceFragment(new UserCoursesFragment(), "UserCoursesFragment");
        } else if (itemId == R.id.NotificationsFragment) {
            replaceFragment(new NotificationsFragment(), "NotificationsFragment");
        } else if (itemId == R.id.ProfileFragment) {
            replaceFragment(new ProfileFragment(), "ProfileFragment");
        } else {
            Log.e("UnknownFragment", "No fallback fragment available for item ID: " + itemId);
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutInMain, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

}
