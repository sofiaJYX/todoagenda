package org.andstatus.todoagenda.prefs.colors;

import androidx.annotation.StringRes;

import org.andstatus.todoagenda.R;
import org.andstatus.todoagenda.widget.TimeSection;
import org.andstatus.todoagenda.widget.WidgetEntry;

public enum TextColorPref {

    WIDGET_HEADER("headerTheme", TextShading.LIGHT, R.string.appearance_header_theme_title,
            false, TimeSection.ALL),
    DAY_HEADER_PAST("dayHeaderThemePast", TextShading.LIGHT, R.string.day_header_theme_title,
            true, TimeSection.PAST),
    ENTRY_PAST("entryThemePast", TextShading.WHITE, R.string.appearance_entries_theme_title,
            false, TimeSection.PAST),
    DAY_HEADER_TODAY("dayHeaderTheme", TextShading.DARK, R.string.day_header_theme_title,
            true, TimeSection.TODAY),
    ENTRY_TODAY("entryTheme", TextShading.BLACK, R.string.appearance_entries_theme_title,
            false, TimeSection.TODAY),
    DAY_HEADER_FUTURE("dayHeaderThemeFuture", TextShading.LIGHT, R.string.day_header_theme_title,
            true, TimeSection.FUTURE),
    ENTRY_FUTURE("entryThemeFuture", TextShading.WHITE, R.string.appearance_entries_theme_title,
            false, TimeSection.FUTURE);

    public final String shadingPreferenceName;
    public final TextShading defaultShading;
    @StringRes
    public final int titleResId;
    public final boolean dependsOnDayHeader;
    public final TimeSection timeSection;

    TextColorPref(String shadingPreferenceName, TextShading defaultShading, int titleResId,
                  boolean dependsOnDayHeader, TimeSection timeSection) {
        this.shadingPreferenceName = shadingPreferenceName;
        this.defaultShading = defaultShading;
        this.titleResId = titleResId;
        this.dependsOnDayHeader = dependsOnDayHeader;
        this.timeSection = timeSection;
    }

    public TextShading getShadingForBackground(TextShading backgroundShading) {
        switch (this) {
            case DAY_HEADER_PAST:
            case DAY_HEADER_TODAY:
            case DAY_HEADER_FUTURE:
                switch (backgroundShading) {
                    case BLACK:
                    case DARK:
                        return TextShading.LIGHT;
                    case LIGHT:
                    case WHITE:
                        return TextShading.DARK;
                }
            default:
                switch (backgroundShading) {
                    case BLACK:
                        return TextShading.LIGHT;
                    case DARK:
                        return TextShading.WHITE;
                    case LIGHT:
                        return TextShading.BLACK;
                    case WHITE:
                        return TextShading.DARK;
                }
        }
        throw new IllegalStateException("getShadingForBackground for " + this + " and background " + backgroundShading);
    }

    public static TextColorPref forDayHeader(WidgetEntry<?> entry) {
        return entry.timeSection.select(DAY_HEADER_PAST, DAY_HEADER_TODAY, DAY_HEADER_FUTURE);
    }

    public static TextColorPref forDetails(WidgetEntry<?> entry) {
        return entry.timeSection.select(DAY_HEADER_PAST, DAY_HEADER_TODAY, DAY_HEADER_FUTURE);
    }

    public static TextColorPref forTitle(WidgetEntry<?> entry) {
        return entry.timeSection.select(ENTRY_PAST, ENTRY_TODAY, ENTRY_FUTURE);
    }

}
