/*
 * Tencent is pleased to support the open source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.np.notepad.manager;

import android.content.Context;
import android.content.res.Configuration;
import com.np.notepad.NoteApplication;
import com.np.notepad.model.enums.ThemeSkinEnum;
import com.qmuiteam.qmui.skin.QMUISkinManager;
import java.util.Objects;

/**
 * app主题管理
 */
public class SkinManager {

    public static void install(Context context) {
        QMUISkinManager skinManager = QMUISkinManager.defaultInstance(context);
        skinManager.addSkin(ThemeSkinEnum.SKIN_BLUE.getCode(), ThemeSkinEnum.SKIN_BLUE.getStyleId());
        skinManager.addSkin(ThemeSkinEnum.SKIN_DARK.getCode(), ThemeSkinEnum.SKIN_DARK.getStyleId());
        skinManager.addSkin(ThemeSkinEnum.SKIN_WHITE.getCode(), ThemeSkinEnum.SKIN_WHITE.getStyleId());
        // 是否黑夜模式
        boolean isDarkMode = (context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        // 获取本地设置主题
        int storeSkinIndex = PreferenceManager.getInstance().getSkinIndex();
        // 设置主题
        if (isDarkMode && storeSkinIndex != ThemeSkinEnum.SKIN_DARK.getCode()) {
            skinManager.changeSkin(ThemeSkinEnum.SKIN_DARK.getCode());
        } else if (!isDarkMode && storeSkinIndex == ThemeSkinEnum.SKIN_DARK.getCode()) {
            skinManager.changeSkin(ThemeSkinEnum.SKIN_BLUE.getCode());
        }else{
            skinManager.changeSkin(storeSkinIndex);
        }
    }

    public static void changeSkin(int index) {
        QMUISkinManager.defaultInstance(Objects.requireNonNull(NoteApplication.Companion.getContext())).changeSkin(index);
        PreferenceManager.getInstance().setSkinIndex(index);
    }

    /**
     * 当前主题code值
     */
    public static int getCurrentSkin() {
        return QMUISkinManager.defaultInstance(Objects.requireNonNull(NoteApplication.Companion.getContext())).getCurrentSkin();
    }
}
