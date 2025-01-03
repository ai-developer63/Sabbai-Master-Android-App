package app.nepaliapp.sabbaikomaster.fragments.loginFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.Commonutils;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.Url;
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;
import app.nepaliapp.sabbaikomaster.fragmentManager.SigningManager;

public class RegisterFragment extends Fragment {
    ImageView backBtn;
    RequestQueue requestQueue;
    Context context;
    PreferencesManager preferencesManager;

    EditText name, EmailId, PhoneNumber, password;
    Button createAccount;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
        this.preferencesManager = new PreferencesManager(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        name = view.findViewById(R.id.FullName);
        EmailId = view.findViewById(R.id.EmailEdittext);
        PhoneNumber = view.findViewById(R.id.PhoneEdittext);
        password = view.findViewById(R.id.PasswordEdittextEdittext);
        createAccount = view.findViewById(R.id.signUpButton);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString().trim())) {
                    name.setError("Full Name is required");
                } else if (TextUtils.isEmpty(EmailId.getText().toString().trim())) {
                    EmailId.setError("Email is required");
                } else if (!isEmail(EmailId.getText().toString().trim())) {
                    EmailId.setError("Enter a valid Email");
                } else if (TextUtils.isEmpty(PhoneNumber.getText().toString().trim())) {
                    PhoneNumber.setError("Phone Number is required");
                } else if (!isWholeNumber(PhoneNumber.getText().toString().trim())) {
                    PhoneNumber.setError("Phone Number must be numeric");
                } else if (countDigits(PhoneNumber.getText().toString().trim()) <= 9) {
                    PhoneNumber.setError("Phone Number must be exactly 10 digits");
                } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
                    password.setError("Password is required");
                } else if (password.getText().toString().trim().length() < 6) {
                    password.setError("Password must be at least 6 characters");
                } else {
                    registerRequest();
                }

            }
        });

        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentChanger(new LoginFragment());
            }
        });


        return view;
    }

    private void registerRequest() {
        Url url = new Url();
        JsonObjectRequest resiteredObject = new JsonObjectRequest(Request.Method.POST, url.getRegisteringUrl(), objectMaker(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String token = response.optString("token");
                Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT).show();
                preferencesManager.UpdateJwtToken(token);
                startActivity(new Intent(getActivity(), DashBoardManager.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "Unknown Error";
                if (error instanceof AuthFailureError) {
                    message = "Email or Password is wrong";
                } else if (error instanceof NetworkError) {
                    message = "Network Error";
                } else if (error instanceof ServerError) {
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            // Convert the error data to a string
                            String errorData = new String(error.networkResponse.data, "UTF-8");

                            // Parse the error JSON
                            JSONObject jsonError = new JSONObject(errorData);

                             message = jsonError.optString("error", "Unknown error occurred");

                        } else {
                            message = "No response from server";
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } else if (error instanceof TimeoutError) {
                    message = "Timeout Error";
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(resiteredObject);

    }

    private JSONObject objectMaker() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", name.getText().toString());
            object.put("phoneNumber", PhoneNumber.getText().toString());
            object.put("emailId", EmailId.getText().toString());
            object.put("password", password.getText().toString());
            object.put("deviceID", Commonutils.getDeviceId(context));
        } catch (JSONException e) {
            Toast.makeText(context, "During Object Creation Error", Toast.LENGTH_SHORT).show();
        }
        return object;
    }

    private void FragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof SigningManager) {
            ((SigningManager) getActivity()).replaceFragments(fragment);
        }

    }
    // Count if Number
    public int countDigits(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                count++;  // Increment count for each digit
            }
        }
        return count;
    }

    //is given Text or Number
    public boolean isWholeNumber(String str) {
        Log.d("whole number checker", "isEmail: " + str + "returned value" + str.matches("\\d+"));
        return str.matches("\\d+");  // Matches one or more digitsd
    }

    private boolean isEmail(String username) {
        Log.d("Email Passed", "isEmail: " + username);
        return username != null && username.contains("@") && username.contains(".");
    }
}