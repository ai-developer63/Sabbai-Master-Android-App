package app.nepaliapp.sabbaikomaster.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import app.nepaliapp.sabbaikomaster.R;


public class SubscriptionDialog {


    public static void show(Context context, String subjectId, String subjectName, String price) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_purchase_protocol, null);
        builder.setView(layout_dialog);
        AppCompatButton requestBtn = layout_dialog.findViewById(R.id.btnPurchase);
        AppCompatEditText userText = layout_dialog.findViewById(R.id.etBatchNumber);
        AppCompatTextView subjectNameText = layout_dialog.findViewById(R.id.tvSubjectName);
        AppCompatTextView priceText = layout_dialog.findViewById(R.id.tvPrice);
        AppCompatButton closeIcon = layout_dialog.findViewById(R.id.btnCancel);
        subjectNameText.setText(subjectName);
        priceText.setText(context.getString(R.string.ruppes) + price);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateServerRequest(context, subjectId, Objects.requireNonNull(userText.getText()).toString());
                    dialog.cancel();
                    hideKeyboard(context, userText);
                    ToastUtils.showToast(context, "requested");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(context, userText);
                dialog.cancel();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.card_background);
    }

    // Method to hide the keyboard
    private static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private static JSONObject objectMaker(String subjectId, String message) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("subjectId", subjectId);
        object.put("message", message);
        return object;
    }

    private static void updateServerRequest(Context context, String subjectId, String message) throws JSONException {
        RequestQueue requestQueue = MySingleton.getInstance(context).getRequestQueue();
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Url url = new Url();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url.getRequestPurchase(), objectMaker(subjectId, message), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String message2 = response.optString("message");
                if (message2.equalsIgnoreCase("update")) {
                    ToastUtils.showToast(context, "Requested, You will receive Call shortly");

                } else if (message2.equalsIgnoreCase("Something went wrong")) {
                    ToastUtils.showToast(context, "Something went wrong");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(context, "Something went wrong");
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


}



