package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;

public class VideoAdapterForVideoViewCard extends RecyclerView.Adapter<VideoAdapterForVideoViewCard.VideoViewHolder> {
    Context context;
    JSONArray array;
    PreferencesManager preferenceManager;

    public VideoAdapterForVideoViewCard(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        this.preferenceManager = new PreferencesManager(context);
    }


    @NonNull
    @Override
    public VideoAdapterForVideoViewCard.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chapter_heading_with_video_holder, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapterForVideoViewCard.VideoViewHolder holder, int position) {
        String playingName = array.optJSONObject(position).optString("name");
        holder.subTopicsName.setText(playingName);
        JSONArray videoArray = array.optJSONObject(position).optJSONArray("videos");
        JSONArray updatedVideoArray = new JSONArray();
        if (videoArray != null) {
            for (int i = 0; i < videoArray.length(); i++) {
                try {
                    JSONObject videoObject = videoArray.getJSONObject(i);
                    if (!videoObject.optString("name").equals(playingName)) {
                        updatedVideoArray.put(videoObject);
                    }
                } catch (JSONException e) {
                }
            }
        }
        Log.d("Video Array at playing part", videoArray.toString());

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        VideoCardAdapter videoCardAdapter = new VideoCardAdapter(context, updatedVideoArray);
        holder.recyclerView.setAdapter(videoCardAdapter);
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView subTopicsName;
        RecyclerView recyclerView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            subTopicsName = itemView.findViewById(R.id.subChapterName);
            recyclerView = itemView.findViewById(R.id.videoCardRecycler);
        }
    }
}