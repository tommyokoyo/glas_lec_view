package com.example.glas_lec;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
   SharedPreferences sharedPreferences;
   SharedPreferences.Editor editor;

   //constructor
    public SharedPrefManager(Context context){
        sharedPreferences = context.getSharedPreferences("Appkey", 0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    //set login methos
    public void setLogin(boolean login){
        editor.putBoolean("KEY_LOGIN",login);
        editor.commit();
    }

    //get login method
    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN", false);
    }

    //set lec_id methods
    public void setLecID(String lecID){
        editor.putString("KEY_LECID",lecID);
        editor.commit();
    }

    //get Lec_id
    public String getLecID(){
        return sharedPreferences.getString("KEY_LECID", "");
    }
    //set lec_id methods
    public void setPassword(String password){
        editor.putString("KEY_PASSWORD",password);
        editor.commit();
    }

    //get Lec_id
    public String setPassword(){
        return sharedPreferences.getString("KEY_USERNAME", "");
    }


}
