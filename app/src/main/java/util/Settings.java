package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class Settings {

    private static final String SP_KEY_USER_LOGIN = "SP_KEY_USER_LOGIN";
    private static final String SP_KEY_USER_PASSWORD = "SP_KEY_USER_PASSWORD";

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoginSuccessfull(Context context) {
        return !TextUtils.isEmpty(Settings.getUserLogin(context));
    }

    public static void putUserLogin(Context context, String login) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SP_KEY_USER_LOGIN, login);
        editor.commit();
    }

    public static String getUserLogin(Context context) {
        SharedPreferences settings = getSharedPreferences(context);
        return settings.getString(SP_KEY_USER_LOGIN, "");
    }

    public static void putUserPassword(Context context, String password) {
        SharedPreferences settings = getSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SP_KEY_USER_PASSWORD, password);
        editor.commit();
    }

    public static String getUserPassword(Context context) {
        SharedPreferences settings = getSharedPreferences(context);
        return settings.getString(SP_KEY_USER_PASSWORD, "");
    }
}
