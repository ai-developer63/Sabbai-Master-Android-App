package app.nepaliapp.sabbaikomaster.common;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import app.nepaliapp.sabbaikomaster.R;


public class SubscriptionDialog {


    public static void show(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.check_purchase_protocol, null);
        builder.setView(layout_dialog);
        AppCompatButton btnRetry = layout_dialog.findViewById(R.id.btnRetry);
        AppCompatEditText userText = layout_dialog.findViewById(R.id.Usermessage);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.card_background);
    }
}
