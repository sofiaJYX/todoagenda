package org.andstatus.todoagenda.prefs.colors;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import org.andstatus.todoagenda.R;

/**
 * @author yvolk@yurivolkov.com
*/
public enum Shading {

    // For historical reasons we store theme names for text shadings i.e. "BLACK" theme for WHITE text shading
    BLACK("WHITE", Color.WHITE, R.style.Theme_ToDoAgenda_White, R.string.appearance_theme_white),
    DARK("LIGHT", 0xFFCCCCCC, R.style.Theme_ToDoAgenda_Light, R.string.appearance_theme_light),
    LIGHT("DARK", 0xFF777777, R.style.Theme_ToDoAgenda_Dark, R.string.appearance_theme_dark),
    WHITE("BLACK", Color.BLACK, R.style.Theme_ToDoAgenda_Black, R.string.appearance_theme_black);

    public final String themeName;
    @ColorInt
    public final int titleColor;
    @StyleRes
    public final int themeResId;
    @StringRes
    public final int titleResId;

    Shading(String themeName, int titleColor, int themeResId, int titleResId) {
        this.themeName = themeName;
        this.titleColor = titleColor;
        this.themeResId = themeResId;
        this.titleResId = titleResId;
    }

    public static Shading fromThemeName(String themeName, Shading defaultShading) {
        for (Shading item : Shading.values()) {
            if (item.themeName.equals(themeName)) {
                return item;
            }
        }
        return defaultShading;
    }

}
