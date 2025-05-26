package app.nepaliapp.sabbaikomaster.fragments.dashboardFragements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout; // NECESSARY IMPORT
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import app.nepaliapp.sabbaikomaster.MainActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.CircleTransform;
import app.nepaliapp.sabbaikomaster.common.DataPart;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.common.UserClassSelector;
import app.nepaliapp.sabbaikomaster.common.VolleyMultipartRequest;
import app.nepaliapp.sabbaikomaster.tabcontroller.ProfileTabLayoutController;


public class ProfileFragment extends Fragment {
    Context context;
    TextView UserName, UserEmail, UserPhone, SelectedClass;
    ImageView profileImage, logoutImageBtn;
    ViewPager2 viewPager2;
    TabLayout tab;
    Url url;
    RequestQueue requestQueue;
    PreferencesManager preferencesManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private Uri imageUri;

    private ActivityResultLauncher<String> getContentLauncher;

    // NECESSARY CHANGE: Declare the main content layout
    private RelativeLayout mainProfileContent;


    public ProfileFragment() {
        // Required empty public constructor
    }

    private static float calculateDynamicTextSize(int length) {
        float maxSize = 18f;
        float minSize = 13f; // Minimum size for very long emails
        float lengthLimit = 70f; // Maximum length before hitting the smallest size

        // Calculate size incrementally between maxSize and minSize
        if (length <= 18) {
            return maxSize;
        } else {
            float sizeStep = (maxSize - minSize) / (lengthLimit - 18); // Decrease per character
            float textSize = maxSize - ((length - 18) * sizeStep);
            return Math.max(minSize, textSize); // Ensure it doesn't drop below minSize
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Initialization(view); // Initialization will now find mainProfileContent
        getUserData();
        ProfileTabLayoutController adapter = new ProfileTabLayoutController(requireActivity());
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tab, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Your Progress");
            } else if (position == 1) {
                tab.setText("Contact Us");
            }
        }).attach();

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        // mainProfileContent is GONE by default from XML

        logoutImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutAlert();
            }
        });

        SelectedClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClasses(new onSuccessClass() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        if (!isAdded() || context == null) return; // Guard clause
                        assert response != null;
                        String[] classesName = new String[response.length() + 1];
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.optJSONObject(i);
                            classesName[i] = object.optString("name", "unknown");
                            if (i == response.length() - 1) {
                                classesName[i + 1] = "None of Above";
                            }
                        }
                        UserClassSelector.show(context, classesName);
                    }

                    @Override
                    public void onError(String errormessage) {
                        if (!isAdded() || context == null) return; // Guard clause
                        Toast.makeText(context, errormessage, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        return view;
    }

    private void Initialization(View view) {
        UserEmail = view.findViewById(R.id.userEmail);
        UserName = view.findViewById(R.id.userName);
        UserPhone = view.findViewById(R.id.userPhone);
        SelectedClass = view.findViewById(R.id.preparingClass);
        profileImage = view.findViewById(R.id.profileImage);
        logoutImageBtn = view.findViewById(R.id.logout);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        viewPager2 = view.findViewById(R.id.viewpager);
        tab = view.findViewById(R.id.tabLayout);
        url = new Url();

        // NECESSARY CHANGE: Initialize the main content layout
        mainProfileContent = view.findViewById(R.id.main_profile_content);
    }

    private void getClasses(onSuccessClass callback) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url.getClasses(), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (!isAdded()) return; // Guard clause
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isAdded()) return; // Guard clause
                if (callback != null) {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown Error";
                    callback.onError(errorMessage); // Pass the error message to the callback
                }
            }
        });
        requestQueue.add(arrayRequest);
    }


    private void getUserData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.getProfile(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // NECESSARY CHANGE: Check if fragment view is still available
                if (getView() == null || !isAdded()) {
                    return; // Fragment's view is destroyed, do nothing
                }

                // Set data to views
                if (UserName != null) UserName.setText(response.optString("name"));
                if (UserEmail != null) {
                    String email = response.optString("email");
                    UserEmail.setText(email);
                    UserEmail.setTextSize(calculateDynamicTextSize(email.length()));
                }
                if (UserPhone != null) UserPhone.setText(response.optString("phoneNumber"));

                if (SelectedClass != null) {
                    if (response.optString("selectedClass").isEmpty()) {
                        SelectedClass.setText("Choose Class");
                    } else {
                        if (response.optString("selectedClass").equalsIgnoreCase("None of Above")) {
                            SelectedClass.setText("Choose a class");
                        } else {
                            SelectedClass.setText(response.optString("selectedClass"));
                        }
                    }
                }

                if (context != null && profileImage != null) { // Check context before Picasso related calls
                    HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
                    Picasso.get().load(response.optString("profileImage")).transform(new CircleTransform()).into(profileImage);
                }

                // UI updates for visibility
                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }

                // NECESSARY CHANGE: Make the main content container visible
                if (mainProfileContent != null) {
                    mainProfileContent.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // NECESSARY CHANGE: Check if fragment view is still available
                if (getView() == null || !isAdded()) {
                    return; // Fragment's view is destroyed, do nothing
                }

                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
                // NECESSARY CHANGE: Ensure main content remains hidden or show an error state
                if (mainProfileContent != null) {
                    mainProfileContent.setVisibility(View.GONE); // Keep it hidden on error
                    mainProfileContent.setAlpha(0f);
                    mainProfileContent.setVisibility(View.VISIBLE);
                    mainProfileContent.animate()
                            .alpha(1f)
                            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                            .setListener(null);
                }
                if (isAdded() && context != null) { // Check before showing Toast
                    Toast.makeText(context, "Error loading profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());
                return headers;
            }
        };
        requestQueue.add(request);
    }

    private void showLogoutAlert() {
        if (!isAdded() || context == null) return; // Guard clause
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to Logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            if (!isAdded() || context == null) return;
            preferencesManager.ClearingUserDataForLogout();
            startActivity(new Intent(requireContext(), MainActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public interface onSuccessClass {
        void onSuccess(JSONArray response);
        void onError(String errormessage);
    }

    private File compressImage(Uri imageUri) {
        if (!isAdded() || getActivity() == null) return null; // Guard clause
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            int maxDimension = 1024;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratio = Math.min((float) maxDimension / width, (float) maxDimension / height);
            Matrix matrix = new Matrix();
            matrix.postScale(ratio, ratio);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            if (!bitmap.isRecycled()) bitmap.recycle(); // Recycle original bitmap

            File file = File.createTempFile("compressed_", ".jpg", requireContext().getCacheDir());
            try (FileOutputStream out = new FileOutputStream(file)) {
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            }
            if (!resizedBitmap.isRecycled()) resizedBitmap.recycle(); // Recycle resized bitmap
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Toast.makeText(context, "Image too large or out of memory", Toast.LENGTH_SHORT).show();
            return null;
        }
    }



    private void uploadImage() {
        if (imageUri == null || !isAdded() || getContext() == null) return; // Guard clause
        final File imageFile = compressImage(imageUri);
        if (imageFile == null) {
            Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            return;
        }

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.PUT, url.getProfilePicUpdate(),
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (!isAdded() || getContext() == null) return;
                        Toast.makeText(getContext(), "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (!isAdded() || getContext() == null) return;
                        Toast.makeText(getContext(), "Upload failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " +preferencesManager.getJwtToken());
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    byte[] fileData = FileUtils.readFileToByteArray(imageFile);
                    params.put("img", new DataPart(imageFile.getName(), fileData, "image/jpeg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(multipartRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getContentLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null && isAdded()) { // Check isAdded
                        imageUri = uri;
                        if (profileImage != null) profileImage.setImageURI(imageUri);
                        uploadImage();
                    }
                }
        );
    }

    private void openImageChooser() {
        if (getContentLauncher != null) { // Check if launcher is initialized
            getContentLauncher.launch("image/*");
        }
    }

    // NECESSARY CHANGE: Add onDestroyView to nullify view references
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify views to help prevent memory leaks and issues with async calls
        UserEmail = null;
        UserName = null;
        UserPhone = null;
        SelectedClass = null;
        profileImage = null;
        logoutImageBtn = null;
        shimmerFrameLayout = null;
        viewPager2 = null;
        tab = null;
        mainProfileContent = null; // Nullify the new view reference

        // It's also good practice to cancel any ongoing Volley requests
        // specific to this fragment if they are no longer needed.
        if (requestQueue != null) {
            // You might want to tag your requests to cancel them more selectively
            // For example, requestQueue.cancelAll("ProfileFragmentTag");
            // For now, this is a general idea:
            // requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            // @Override
            // public boolean apply(Request<?> request) {
            // return true; // Cancels all, be careful
            // }
            // });
        }
    }
}