package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.Activity.VideoPlayingActivity;
import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.ViewHolder> {

    Context context;
    JSONArray array;
    PreferencesManager preferencesManager;

    public VideoCardAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.preferencesManager = new PreferencesManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject object =  array.optJSONObject(position);
        holder.title.setText(object.optString("title"));
        holder.subTitle.setText("Full topics Detail is here");
        holder.author.setText("Sabbai ko Master");
        HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
        Picasso.get().load(object.optString("thumnailUrl")).into(holder.thumnailView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayingActivity.class);
                intent.putExtra("videoUrl", object.optString("videoUrl"));
                intent.putExtra("title", object.optString("title"));
                intent.putExtra("jsonArray",array.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
            thumnailView =  itemView.findViewById(R.id.videoThumbnail);
        cardView = itemView.findViewById(R.id.videoCardID);
        }
    }
}
