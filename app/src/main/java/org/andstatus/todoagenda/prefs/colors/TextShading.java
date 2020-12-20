package org.andstatus.todoagenda.prefs.colors;

import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import org.andstatus.todoagenda.R;

/**
 * @author yvolk@yurivolkov.com
*/
public enum TextShading {

    // For historical reasons we store theme names for text shadings i.e. "BLACK" theme for WHITE text shading
    BLACK("WHITE", R.style.Theme_ToDoAgenda_White, R.string.appearance_theme_white),
    DARK("LIGHT", R.style.Theme_ToDoAgenda_Light, R.string.appearance_theme_light),
    LIGHT("DARK", R.style.Theme_ToDoAgenda_Dark, R.string.appearance_theme_dark),
    WHITE("BLACK", R.style.Theme_ToDoAgenda_Black, R.string.appearance_theme_black);

    @StyleRes
    public final int themeResId;
    @StringRes
    public final int titleResId;
    public final String themeName;

    TextShading(String themeName, int themeResId, int titleResId) {
        this.themeName = themeName;
        this.themeResId = themeResId;
        this.titleResId = titleResId;
    }

    public static TextShading fromThemeName(String themeName, TextShading defaultShading) {
        for (TextShading item : TextShading.values()) {
            if (item.themeName.equals(themeName)) {
                return item;
            }
        }
        return defaultShading;
    }

}
