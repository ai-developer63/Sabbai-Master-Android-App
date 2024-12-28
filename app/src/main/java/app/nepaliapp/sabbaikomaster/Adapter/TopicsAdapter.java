package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    Context context;
    JSONArray array;

    public TopicsAdapter(Context context,JSONArray array) {
        this.context = context;
        this.array = array;
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
            JSONObject object = new JSONObject();
             object = array.getJSONObject(position);
            holder.txtView.setText(object.optString("name"));
            holder.recyclerViewBtn.setLayoutManager(new LinearLayoutManager(context));
            ChapterCardBtnAdapter adapter = new ChapterCardBtnAdapter(context,object.optJSONArray("chapterModel"));
            holder.recyclerViewBtn.setAdapter(adapter);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.relativeLayout.getVisibility() == View.GONE) {
                    holder.relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.relativeLayout.setVisibility(View.GONE);
                }
            }
        });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.relativeLayout.getVisibility() == View.GONE) {
                    holder.relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.relativeLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout relativeLayout;
        ImageButton imageButton;
        RecyclerView recyclerViewBtn;
        TextView txtView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewTopic);
            relativeLayout = itemView.findViewById(R.id.subchaptersContainer);
            imageButton = itemView.findViewById(R.id.btnExpand);
            recyclerViewBtn = itemView.findViewById(R.id.recyclerSubjectButton);
        txtView = itemView.findViewById(R.id.topicTitle);
        }


    }

}
