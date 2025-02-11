package app.nepaliapp.sabbaikomaster.common;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast sToast;

    public static void showToast(Context context, String message) {
        // Cancel any existing Toast if necessary
        if (sToast != null) {
            sToast.cancel();
        }
        sToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
        sToast.show();
    }
}
