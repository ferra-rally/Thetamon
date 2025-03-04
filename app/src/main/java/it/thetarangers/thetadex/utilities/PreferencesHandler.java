package it.thetarangers.thetadex.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import it.thetarangers.thetadex.R;

// Contains static methods to deal with SharedPreferences, guarantees consistence with SettingsFragment
public class PreferencesHandler {

    private PreferencesHandler() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preferences_name),
                Context.MODE_PRIVATE);
    }

    public static synchronized Boolean isFirstUse(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.first_use_pref), true);
    }

    public static synchronized void setIsFirstUse(Context context, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(context.getString(R.string.first_use_pref), value);
        editor.apply();
    }

    public static synchronized String isNightMode(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.night_mode_pref), context.getString(R.string.system_enum));
    }

    public static synchronized Boolean isVolumeOn(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.volume_pref), true);
    }

}
