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
import app.nepaliapp.sabbaikomaster.fragmentManager.DashBoardManager;
import app.nepaliapp.sabbaikomaster.fragments.dashboardFragements.AllSubjectsFragment;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    Context context;
    JSONArray array;
    FragmentManager fragmentManager;
    PreferencesManager preferencesManager;
    DashBoardManager dashBoardManager;

    public CategoryAdapter(Context context, JSONArray array, FragmentManager fragmentManager) {
        this.context = context;
        this.array = array;
        this.fragmentManager = fragmentManager;
        this.preferencesManager = new PreferencesManager(context);
        if (context instanceof DashBoardManager) {
            dashBoardManager = (DashBoardManager) context;
        } else {
            throw new ClassCastException(context + "must be DashBoardManager");
        }
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
try {
    JSONObject jsonObject = array.getJSONObject(position);
    HeaderPicasso.initializePicassoWithHeaders(context, "Authorization", "Bearer " + preferencesManager.getJwtToken());
    Picasso.get().load(jsonObject.optString("logoName")).into(holder.icons);
    holder.txtView.setText(jsonObject.optString("name"));
    holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString("searchtag",jsonObject.optString("name"));
            AllSubjectsFragment allSubjectsFragment = new AllSubjectsFragment();
            allSubjectsFragment.setArguments(bundle);
            dashBoardManager.setBottomNavigationSelected(2);
            replaceFragments(allSubjectsFragment);
        }
    });
}catch (Exception e){

}


    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icons;
        TextView txtView;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icons = itemView.findViewById(R.id.imageView5);
            txtView = itemView.findViewById(R.id.txtIcon);
            cardView= itemView.findViewById(R.id.categoriesCards);
        }
    }
    private void replaceFragments(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutInMain, fragment);
        fragmentTransaction.commit();
    }
}
