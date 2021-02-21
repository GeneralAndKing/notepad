
package com.np.notepad.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.np.notepad.model.enums.ThemeSkinEnum;

public class PreferenceManager {
    private static SharedPreferences sPreferences;
    private static PreferenceManager sPreferenceManager = null;

    private static final String APP_SKIN_INDEX = "app_skin_index";

    private PreferenceManager(Context context) {
        sPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static PreferenceManager getInstance(Context context) {
        if (sPreferenceManager == null) {
            sPreferenceManager = new PreferenceManager(context);
        }
        return sPreferenceManager;
    }

    public void setSkinIndex(int index) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(APP_SKIN_INDEX, index);
        editor.apply();
    }

    public int getSkinIndex() {
        return sPreferences.getInt(APP_SKIN_INDEX, ThemeSkinEnum.SKIN_BLUE.getCode());
    }
}
