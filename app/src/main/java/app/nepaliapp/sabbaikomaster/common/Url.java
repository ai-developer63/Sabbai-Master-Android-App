package app.nepaliapp.sabbaikomaster.common;

public class Url {
    String url= "https://raw.githubusercontent.com/ai-developer63/just/gh-pages";
    String serverUrl="https://gc5tdmq1-8080.inc1.devtunnels.ms";
    String app_checkup="/appcheck";
    String login="/api/auth/login";
    String register ="/api/auth/register";
    String deviceIdFetch= "/getdeviceID";
    String home="/api/home";
    String Profile="/api/profile";
    String subjects = "/api/subject";

    public String getApp_checkup() {
        return url+app_checkup;
    }

    public String getDeviceIdFetch(){
        return serverUrl+deviceIdFetch;
    }
    public String getRegisteringUrl(){
        return serverUrl+register;
    }
    public String getloginUrl(){
        return serverUrl+login;
    }
    public String getHome(){
        return serverUrl+home;
    }

    public String getProfile(){
        return serverUrl+Profile;
    }

    public String getSubjects(){
        return serverUrl+subjects;
    }

}
