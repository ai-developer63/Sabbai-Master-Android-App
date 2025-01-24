package app.nepaliapp.sabbaikomaster.fragments.profileTab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import app.nepaliapp.sabbaikomaster.R;


public class contactUs extends Fragment {
    private static final int REQUEST_CALL = 1;
    CardView callBtn, chatBtn;

    public contactUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        callBtn = view.findViewById(R.id.callNow);
        chatBtn = view.findViewById(R.id.chatNow);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }

            private void makePhoneCall() {


                if (ContextCompat.checkSelfPermission(requireActivity(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    String dial = "tel:" + "+9779867331839";
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }


            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://m.me/101442208871937");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}