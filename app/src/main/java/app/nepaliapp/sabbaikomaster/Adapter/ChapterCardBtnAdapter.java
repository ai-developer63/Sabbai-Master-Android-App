package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.fragments.subjectFragments.ChapterVideoCardViewer;

public class ChapterCardBtnAdapter extends RecyclerView.Adapter<ChapterCardBtnAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    FragmentManager fragmentManager;

    public ChapterCardBtnAdapter(Context context, JSONArray array,FragmentManager fragmentManager) {
        this.context = context;
        this.array = array;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_button_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {

            JSONObject jsonObject = array.getJSONObject(position);
            String name = jsonObject.optString("name");
            holder.txtView.setText(name);
            JSONArray subTopicsList = jsonObject.optJSONArray("subtopicsList");
            holder.cardBtnHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(subTopicsList.length()==0)){
                        Bundle bundle = new Bundle();
                        ChapterVideoCardViewer chapterVideoCardViewer = new ChapterVideoCardViewer();
                        bundle.putString("name", name);
                        bundle.putString("subTopics", subTopicsList.toString());
                        chapterVideoCardViewer.setArguments(bundle);
                        replaceFragments(chapterVideoCardViewer);
                    }else {
                        Toast.makeText(context, "No Item Present", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cardBtnHolder, cardTextViewHolder;
        TextView txtView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView = itemView.findViewById(R.id.cardText);
            cardBtnHolder = itemView.findViewById(R.id.ChapterBtn);
        }
    }
    private void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutInMain, fragment);
        fragmentTransaction.commit();
    }
}
