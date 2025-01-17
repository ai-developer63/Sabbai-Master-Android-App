package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.nepaliapp.sabbaikomaster.Activity.VideoPlayingActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.MySingleton;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.common.SubscriptionDialog;
import app.nepaliapp.sabbaikomaster.common.Url;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.ViewHolder> {

    Context context;
    JSONArray array;
    PreferencesManager preferencesManager;
    RequestQueue requestQueue;

    public VideoCardAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.preferencesManager = new PreferencesManager(context);
        this.requestQueue = MySingleton.getInstance(context).getRequestQueue();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object = array.optJSONObject(position);
        Log.d("search subject id", object.toString());
        holder.title.setText(object.optString("title"));
        holder.subTitle.setText("Full topics Detail is here");
        holder.author.setText("Sabbai ko Master");
        HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
        Picasso.get().load(object.optString("thumnailUrl")).into(holder.thumnailView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isPurchasedServer("1", new PurchaseCallback() {
                    @Override
                    public void onSuccess(boolean isPurchased) {
                        if (isPurchased) {
                            Intent intent = new Intent(context, VideoPlayingActivity.class);
                            intent.putExtra("videoUrl", object.optString("videoUrl"));
                            intent.putExtra("title", object.optString("title"));
                            intent.putExtra("jsonArray", array.toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else {
                            SubscriptionDialog.show(context);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(context, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.length();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumnailView;
        TextView title, subTitle, author;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoTitle);
            subTitle = itemView.findViewById(R.id.videoSubtittle);
            author = itemView.findViewById(R.id.videoAuthor);
            thumnailView = itemView.findViewById(R.id.videoThumbnail);
            cardView = itemView.findViewById(R.id.videoCardID);
        }
    }
    public void isPurchasedServer(String subjectId, PurchaseCallback callback) {
        Url url = new Url();
        StringRequest request = new StringRequest(Request.Method.GET,
                url.getVerifyPurchase(subjectId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the server response to a boolean
                        try {
                            boolean isPurchased = Boolean.parseBoolean(response);
                            callback.onSuccess(isPurchased);
                        } catch (Exception e) {
                            callback.onError("Parse Error: Invalid response format.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Unknown error";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMessage = "Timeout or No Connection Error.";
                        } else if (error instanceof AuthFailureError) {
                            errorMessage = "Authentication Error.";
                        } else if (error instanceof ServerError) {
                            errorMessage = "Server Error.";
                        } else if (error instanceof NetworkError) {
                            errorMessage = "Network Error.";
                        } else if (error instanceof ParseError) {
                            errorMessage = "Parse Error.";
                        }
                        callback.onError(errorMessage);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + preferencesManager.getJwtToken());
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        // Add the request to the RequestQueue
      requestQueue.add(request);
    }

    // Define a callback interface
    public interface PurchaseCallback {
        void onSuccess(boolean isPurchased);
        void onError(String errorMessage);
    }
}
