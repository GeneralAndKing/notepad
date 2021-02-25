
package com.np.notepad.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.np.notepad.NoteApplication;
import com.np.notepad.model.enums.ThemeSkinEnum;

import java.util.HashSet;
import java.util.Set;

public class PreferenceManager {
    private static SharedPreferences sPreferences;
    private static PreferenceManager sPreferenceManager = null;

    // 主题选择code
    private static final String APP_SKIN_INDEX = "app_skin_index";
    // 通知的ids
    private static final String APP_NOTICE_IDS = "app_notice_ids";
    // 锁屏通知
    private static final String APP_LOCK_NOTICE = "app_lock_notice";

    private PreferenceManager(Context context) {
        sPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public static PreferenceManager getInstance() {
        if (sPreferenceManager == null) {
            sPreferenceManager = new PreferenceManager(NoteApplication.context);
        }
        return sPreferenceManager;
    }

    // --------------------------------------主题-----------------------------------------
    public void setSkinIndex(int index) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(APP_SKIN_INDEX, index);
        editor.apply();
    }

    public int getSkinIndex() {
        return sPreferences.getInt(APP_SKIN_INDEX, ThemeSkinEnum.SKIN_BLUE.getCode());
    }

    // --------------------------------------通知列表-----------------------------------------
    public void setNoticeIds(Set<String> values) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putStringSet(APP_NOTICE_IDS, values);
        editor.apply();
    }

    public Set<String> getNoticeIds() {
        return sPreferences.getStringSet(APP_NOTICE_IDS, new HashSet<>());
    }
    // --------------------------------------显示锁屏通知-----------------------------------------
    public boolean getLockScreenNotice() {
        return sPreferences.getBoolean(APP_LOCK_NOTICE, true);
    }

    public void setLockScreenNotice(boolean show) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(APP_LOCK_NOTICE, show);
        editor.apply();
    }
}
