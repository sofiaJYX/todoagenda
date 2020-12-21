package org.andstatus.todoagenda.prefs.colors;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import org.andstatus.todoagenda.R;
import org.andstatus.todoagenda.widget.TimeSection;
import org.andstatus.todoagenda.widget.WidgetEntry;

public enum TextColorPref {

    WIDGET_HEADER("headerTheme", TextShading.LIGHT, R.string.appearance_header_theme_title,
            "widgetHeaderTextColor", 0x9AFFFFFF,
            R.string.widget_header_text_color, false, TimeSection.ALL),
    DAY_HEADER_PAST("dayHeaderThemePast", TextShading.LIGHT, R.string.day_header_theme_title,
            "dayHeaderTextColorPast", 0xFFCCCCCC, R.string.day_header_text_color,
            true, TimeSection.PAST),
    EVENT_PAST("entryThemePast", TextShading.WHITE, R.string.appearance_entries_theme_title,
            "eventTextColorPast", 0xFFFFFFFF, R.string.event_text_color,
            false, TimeSection.PAST),
    DAY_HEADER_TODAY("dayHeaderTheme", TextShading.DARK, R.string.day_header_theme_title,
            "dayHeaderTextColorToday", 0xFF777777, R.string.day_header_text_color,
            true, TimeSection.TODAY),
    EVENT_TODAY("entryTheme", TextShading.BLACK, R.string.appearance_entries_theme_title,
            "eventTextColorToday", 0xFF000000, R.string.event_text_color,
            false, TimeSection.TODAY),
    DAY_HEADER_FUTURE("dayHeaderThemeFuture", TextShading.LIGHT, R.string.day_header_theme_title,
            "dayHeaderTextColorFuture", 0xFFCCCCCC, R.string.day_header_text_color,
            true, TimeSection.FUTURE),
    EVENT_FUTURE("entryThemeFuture", TextShading.WHITE, R.string.appearance_entries_theme_title,
            "eventTextColorFuture", 0xFFFFFFFF, R.string.event_text_color,
            false, TimeSection.FUTURE);

    public final String shadingPreferenceName;
    public final TextShading defaultShading;
    @StringRes public final int shadingTitleResId;

    public final String colorPreferenceName;
    @ColorInt public final int defaultColor;
    @StringRes public final int colorTitleResId;

    public final boolean dependsOnDayHeader;
    public final TimeSection timeSection;

    TextColorPref(String shadingPreferenceName, TextShading defaultShading, int shadingTitleResId,
                  String colorPreferenceName,@ColorInt int defaultColor, int colorTitleResId,
                  boolean dependsOnDayHeader, TimeSection timeSection) {
        this.shadingPreferenceName = shadingPreferenceName;
        this.defaultShading = defaultShading;
        this.colorPreferenceName = colorPreferenceName;
        this.defaultColor = defaultColor;
        this.shadingTitleResId = shadingTitleResId;
        this.colorTitleResId = colorTitleResId;
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
        return entry.timeSection.select(EVENT_PAST, EVENT_TODAY, EVENT_FUTURE);
    }

}
