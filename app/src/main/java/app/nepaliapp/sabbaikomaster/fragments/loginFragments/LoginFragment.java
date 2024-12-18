package app.nepaliapp.sabbaikomaster.fragments.loginFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
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


public class LoginFragment extends Fragment {
    Context context;
    Button signupBtn, signinBtn;
    EditText PhoneNumber_EditText, Password_LoginEditText;
    TextView ForgetPassword;
    ToggleButton togglePasswordVisibility;
    private RequestQueue requestQueue;
    PreferencesManager preferencesManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        signupBtn = view.findViewById(R.id.signUpButton);
        signinBtn = view.findViewById(R.id.signinButton);
        PhoneNumber_EditText = view.findViewById(R.id.PhoneNumberEditText);
        Password_LoginEditText = view.findViewById(R.id.PasswordEdittext);
        ForgetPassword = view.findViewById(R.id.Forgetpassword);
        PhoneNumber_EditText.requestFocus();

        togglePasswordVisibility = view.findViewById(R.id.togglePasswordVisibility);
         preferencesManager= new PreferencesManager(context);


         signupBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FragmentChanger(new RegisterFragment());
             }
         });

        togglePasswordVisibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    Password_LoginEditText.setTransformationMethod(null); // Show password in plain text
                } else {
                    // Hide password
                    Password_LoginEditText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(PhoneNumber_EditText.getText().toString())) {
                    PhoneNumber_EditText.setError("PhoneNumber or Email is required");
                } else if (isWholeNumber(PhoneNumber_EditText.getText().toString())) {
                    if (countDigits(PhoneNumber_EditText.getText().toString()) <= 9) {
                        PhoneNumber_EditText.setError("Phone number must be 10 digit");
                    } else if (TextUtils.isEmpty(Password_LoginEditText.getText().toString())) {
                        Password_LoginEditText.setError("Enter your Password");
                    } else if (!(Password_LoginEditText.getText().length() >= 6)) {
                        Password_LoginEditText.setError("Password must be 6 character or more");
                    } else {
                        sendLoginRequest();
                    }

                } else if (!isWholeNumber(PhoneNumber_EditText.getText().toString())) {

                    if (!isEmail(PhoneNumber_EditText.getText().toString())) {
                        PhoneNumber_EditText.setError("Email must be Valid");
                    } else if (TextUtils.isEmpty(Password_LoginEditText.getText().toString())) {
                        Password_LoginEditText.setError("Enter your Password");
                    } else if (!(Password_LoginEditText.getText().length() >= 6)) {
                        Password_LoginEditText.setError("Password must be 6 character or more");
                    } else {
                        sendLoginRequest();
                    }

                }


            }

        });


        return view;
    }

    private void sendLoginRequest() {
        Url url = new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getloginUrl(), objectMaker(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String token = response.getString("token");
                            preferencesManager.UpdateJwtToken(token);
                            Intent intent = new Intent(getActivity(), DashBoardManager.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error on Getting Server Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleErrorResponse(error);
                    }
                });

        requestQueue.add(request);
    }

    private void FragmentChanger(Fragment fragment) {

        if (getActivity() != null && getActivity() instanceof SigningManager) {
            ((SigningManager) getActivity()).replaceFragments(fragment);
        }

    }
    private void handleErrorResponse(VolleyError error) {
        String message = "Unknown error";
        if (error instanceof AuthFailureError) {
            message = "Email or Password is wrong";
        } else if (error instanceof NetworkError) {
            message = "Network Error";
        } else if (error instanceof ServerError) {
            message = "Server Error";
        } else if (error instanceof TimeoutError) {
            message = "Timeout Error";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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

    public JSONObject objectMaker() {
        JSONObject object = new JSONObject();
        try {
            String deviceID= Commonutils.getDeviceId(context);
            object.put("emailOrPhone", PhoneNumber_EditText.getText().toString());
            object.put("password", Password_LoginEditText.getText().toString());
            preferencesManager.UpdateDeviceUniqueID(deviceID);
            object.put("deviceId", deviceID);
        } catch (JSONException e) {
            Toast.makeText(context, "Data Creation went Wrong", Toast.LENGTH_SHORT).show();
        }
        Log.d("Created Object", object.toString());
        return object;
    }

}