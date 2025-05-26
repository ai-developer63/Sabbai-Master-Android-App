package app.nepaliapp.sabbaikomaster.common;

public class Url {
    String url= "https://raw.githubusercontent.com/ai-developer63/just/gh-pages";
//    String serverUrl="https://sabbaikomaster.centralindia.cloudapp.azure.com";
    String serverUrl ="https://gc5tdmq1-8080.inc1.devtunnels.ms";
    String app_checkup="/appcheck";
    String login="/api/auth/login";
    String register ="/api/auth/register";
    String deviceIdFetch= "/getdeviceID";
    String home="/api/home";
    String Profile="/api/profile";
    String subjects = "/api/subject";
    String SubjectDetail ="/api/viewSubject/";

    String VideoLink = "/api/range/";

    String verifyPurchase = "/api/subjectPurchased/";

    String userPurchasedSubject = "/api/purchasedSubjects";

    String updateClass = "/api/profileupdate/selectedClass/";

    String classes = "/api/getAllClasses";

    String requestPurchase = "/api/requestPurchase";

    String profilePicUpdate="/api/auth/updateProfileImage";

    public String getProfilePicUpdate() {
        return serverUrl+profilePicUpdate;
    }

    public String getRequestPurchase() {
        return serverUrl+requestPurchase;
    }

    public String getClasses() {
        return serverUrl+classes;
    }

    public String getUpdateClass(String classes) {
        return serverUrl+updateClass+classes;
    }

    public String getUserPurchasedSubject() {
        return serverUrl+userPurchasedSubject;
    }

    public String getVerifyPurchase(String id) {
        return serverUrl+verifyPurchase+id;
    }

    public String getVideoLink() {
        return serverUrl+VideoLink;
    }

    public String getSubjectDetail(String subjectId) {
        return serverUrl+SubjectDetail+subjectId;
    }

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
