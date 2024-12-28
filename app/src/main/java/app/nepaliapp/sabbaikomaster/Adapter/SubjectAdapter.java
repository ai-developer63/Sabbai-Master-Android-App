package app.nepaliapp.sabbaikomaster.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import app.nepaliapp.sabbaikomaster.R;
import app.nepaliapp.sabbaikomaster.common.HeaderPicasso;
import app.nepaliapp.sabbaikomaster.common.PreferencesManager;
import app.nepaliapp.sabbaikomaster.fragments.subjectFragments.SubjectViewFragment;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    Context context;
    JSONArray array;
    PreferencesManager preferencesManager;
    FragmentManager fragmentManager;

    public SubjectAdapter(Context context, JSONArray array, FragmentManager fragmentManager) {
        this.context = context;
        this.array = array;
        this.preferencesManager = new PreferencesManager(context);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_model_dash, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject jsonObject = array.optJSONObject(position);
        String name = jsonObject.optString("name");
        String logoUrl = jsonObject.optString("logo");
        Log.d("Logo url", logoUrl);
        Boolean islive = jsonObject.optBoolean("isLive");
        Boolean isAnimated = jsonObject.optBoolean("isAnimated");
        String shortDescription = jsonObject.optString("shortDescription");
        holder.name.setText(name);
        holder.shortDescription.setText(shortDescription);
        holder.islive.setVisibility(islive ? View.VISIBLE : View.GONE);
        holder.isAnimated.setVisibility(isAnimated ? View.VISIBLE : View.GONE);
        holder.classTag.setText(jsonObject.optString("whichClass"));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("subjectId", jsonObject.optString("id"));
                SubjectViewFragment subjectViewFragment = new SubjectViewFragment();
                subjectViewFragment.setArguments(bundle);
                replaceFragments(subjectViewFragment);
            }
        });


        HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
        Picasso.get().load(logoUrl).into(holder.logoImage);


    }

    @Override
    public int getItemCount() {

        if (array == null) {
            return 0;
        } else {
            return array.length();
        }
    }

    private void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutInMain, fragment);
        fragmentTransaction.commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, islive, isAnimated, shortDescription, description, classTag;
        ImageView logoImage;
        CardView button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.titleText);
            logoImage = itemView.findViewById(R.id.logoImage);
            islive = itemView.findViewById(R.id.tagLive);
            isAnimated = itemView.findViewById(R.id.tagAnimated);
            shortDescription = itemView.findViewById(R.id.shortDescription);
            classTag = itemView.findViewById(R.id.tagBsc);
            button = itemView.findViewById(R.id.subjectCard);

        }
    }
}
