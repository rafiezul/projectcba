package com.abc.projectcba.ui;



import android.content.Context;
import android.content.SharedPreferences;

public class UserInfoSP {
    private SharedPreferences sharedPref;
    private static  UserInfoSP inst;

    public static UserInfoSP getInstance(Context context){
        if (inst == null)
        {
            inst = new UserInfoSP(context);
        }
        return inst;
    }

    private UserInfoSP(Context context){
        sharedPref = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    }

    private void saveData (String key, String value){
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    private  String getData (String key){
        if(sharedPref!=null){
            return sharedPref.getString(key, "");
        }
        return "";
    }

    public String getUserID(){
        return getData("UserID");
    }

    public String getUserPwd(){
        return  getData("UserPwd");
    }

    public Boolean getLoginSuccess(){
        String ss = getData("LoginSuccess");
        return  ss.equalsIgnoreCase("TRUE");
    }

    public void  setLoginSuccess(String xuserid, String userPwd){

        saveData("UserID", xuserid);
        saveData("UserPwd", userPwd);
        saveData("LoginSuccess", "TRUE");
    }

    public  void resetAll(){
        saveData("UserID", "");
        saveData("UserPwd", "");
        saveData("LoginSuccess", "FALSE");
    }

    public  void  signOut(){
        saveData("UserPwd", "");
        saveData("LoginSuccess", "FALSE");
    }


}