package app.nepaliapp.sabbaikomaster.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    Context context;

    public PreferencesManager(Context context) {
        this.context = context;
    }

    private static final String PREFERENCES_JWT="Jwt_Token";
    private static final String PREFERENCES_DEVICETOKEN="device_Token";
    private static final String PREFERENCES_User_OpeningSubjects="userService";


    public void updateUserViewedSubject(String subjectId,String subjectName,String subjectPrice){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_User_OpeningSubjects,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("subjectId",subjectId);
        editor.putString("subjectName",subjectName);
        editor.putString("subjectPrice",subjectPrice);
        editor.apply();
    }

    public String getUserClickedSubject(String id_name_price){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_User_OpeningSubjects,Context.MODE_PRIVATE);
        if(id_name_price.equalsIgnoreCase("id")) {
            return sharedPreferences.getString("subjectId", "5");
        } else if (id_name_price.equalsIgnoreCase("name")) {
            return sharedPreferences.getString("subjectName", "unknown");
        }else {
            return sharedPreferences.getString("subjectPrice", "unknown");
        }

    }

    public void UpdateJwtToken(String JwtToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_JWT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("jwtToken",JwtToken);
        editor.apply();
    }

    public String getJwtToken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_JWT,Context.MODE_PRIVATE);
        return sharedPreferences.getString("jwtToken","Jwt_kali_xa");
    }

    public void UpdateDeviceUniqueID(String deviceToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_DEVICETOKEN,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DeviceToken",deviceToken);
        editor.apply();
    }
    public String getDeviceUniqueID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_DEVICETOKEN,Context.MODE_PRIVATE);
        return sharedPreferences.getString("DeviceToken","DeviceToken_kali_xa");
    }

    public void ClearingUserDataForLogout(){
        UpdateJwtToken("Jwt_kali_xa");
        UpdateDeviceUniqueID("DeviceToken_kali_xa");

    }

}
