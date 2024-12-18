package app.nepaliapp.sabbaikomaster.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class Commonutils {

    public static String getDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_device_info", Context.MODE_PRIVATE);
        String deviceId = prefs.getString("device_id", null);

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("device_id", deviceId);
            editor.apply();
        }

        return deviceId;
    }

}
