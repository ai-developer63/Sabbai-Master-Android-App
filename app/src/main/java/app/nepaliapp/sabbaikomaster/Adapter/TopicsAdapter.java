package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import app.nepaliapp.sabbaikomaster.R;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    FragmentManager fragmentManager;

    public TopicsAdapter(Context context, JSONArray array, FragmentManager fragmentManager) {
        this.context = context;
        this.array = array;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject object = array.getJSONObject(position);
            holder.relativeLayout.setVisibility(View.GONE);
            if (Objects.requireNonNull(object.optJSONArray("chapterModel")).length() == 0) {
                holder.recyclerViewBtn.setVisibility(View.GONE);
                holder.NoChapterTxt.setVisibility(View.VISIBLE);
            } else {
                holder.recyclerViewBtn.setVisibility(View.VISIBLE);
                holder.NoChapterTxt.setVisibility(View.GONE);
            }
            holder.txtView.setText(object.optString("name"));
            holder.recyclerViewBtn.setLayoutManager(new LinearLayoutManager(context));
            ChapterCardBtnAdapter adapter = new ChapterCardBtnAdapter(context, object.optJSONArray("chapterModel"),fragmentManager);
            holder.recyclerViewBtn.setAdapter(adapter);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Toggle visibility and rotate arrow icon
        View.OnClickListener toggleVisibility = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.relativeLayout.getVisibility() == View.GONE) {
                    holder.relativeLayout.setVisibility(View.VISIBLE);
                    holder.imageButton.animate().rotation(180).setDuration(200).start(); // Rotate to up
                } else {
                    holder.relativeLayout.setVisibility(View.GONE);
                    holder.imageButton.animate().rotation(0).setDuration(200).start(); // Rotate to down
                }
            }
        };

        // Apply the toggle logic to both card and expand button
        holder.cardView.setOnClickListener(toggleVisibility);
        holder.imageButton.setOnClickListener(toggleVisibility);

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout relativeLayout;
        ImageView imageButton;
        RecyclerView recyclerViewBtn;
        TextView txtView, NoChapterTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTopic);
            relativeLayout = itemView.findViewById(R.id.subchaptersContainer);
            imageButton = itemView.findViewById(R.id.arrowIndicator);
            recyclerViewBtn = itemView.findViewById(R.id.recyclerSubjectButton);
            txtView = itemView.findViewById(R.id.topicTitle);
            NoChapterTxt = itemView.findViewById(R.id.noChapterTxt);
        }


    }

}
