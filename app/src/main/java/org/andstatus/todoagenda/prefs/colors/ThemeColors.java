package org.andstatus.todoagenda.prefs.colors;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;

import androidx.annotation.ColorInt;

import org.andstatus.todoagenda.prefs.ApplicationPreferences;
import org.andstatus.todoagenda.util.StringUtil;
import org.andstatus.todoagenda.widget.WidgetEntry;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Colors part of settings for one theme, of one Widget
 * @author yvolk@yurivolkov.com
 */
public class ThemeColors {
    private static final String TAG = ThemeColors.class.getSimpleName();
    public static final int TRANSPARENT_BLACK = 0;
    public static final int TRANSPARENT_WHITE = 0x00FFFFFF;
    public final static ThemeColors EMPTY = new ThemeColors(null, ColorThemeType.SINGLE);
    private final Context context;
    public final ColorThemeType colorThemeType;

    public static final String PREF_WIDGET_HEADER_BACKGROUND_COLOR = "widgetHeaderBackgroundColor";
    @ColorInt public static final int PREF_WIDGET_HEADER_BACKGROUND_COLOR_DEFAULT = TRANSPARENT_BLACK;
    @ColorInt private int widgetHeaderBackgroundColor;
    private TextShading widgetHeaderBackgroundShading;
    public static final String PREF_PAST_EVENTS_BACKGROUND_COLOR = "pastEventsBackgroundColor";
    @ColorInt public static final int PREF_PAST_EVENTS_BACKGROUND_COLOR_DEFAULT = 0xBF78782C;
    @ColorInt private int pastEventsBackgroundColor;
    private TextShading pastEventsBackgroundShading;
    public static final String PREF_TODAYS_EVENTS_BACKGROUND_COLOR = "todaysEventsBackgroundColor";
    @ColorInt public static final int PREF_TODAYS_EVENTS_BACKGROUND_COLOR_DEFAULT = 0xDAFFFFFF;
    @ColorInt private int todaysEventsBackgroundColor;
    private TextShading todaysEventsBackgroundShading;
    public static final String PREF_EVENTS_BACKGROUND_COLOR = "backgroundColor";
    @ColorInt public static final int PREF_EVENTS_BACKGROUND_COLOR_DEFAULT = 0x80000000;
    @ColorInt private int eventsBackgroundColor;
    private TextShading eventsBackgroundShading;

    public static final String PREF_TEXT_COLOR_SOURCE = "textColorSource";
    public TextColorSource textColorSource = TextColorSource.defaultValue;
    public final Map<TextColorPref, TextShading> textColors = new ConcurrentHashMap<>();

    public static ThemeColors fromJson(Context context, ColorThemeType colorThemeType, JSONObject json) {
        return new ThemeColors(context, colorThemeType).setFromJson(json);
    }

    public ThemeColors(Context context, ColorThemeType colorThemeType) {
        this.context = context;
        this.colorThemeType = colorThemeType;
        setWidgetHeaderBackgroundColor(PREF_WIDGET_HEADER_BACKGROUND_COLOR_DEFAULT);
        setPastEventsBackgroundColor(PREF_PAST_EVENTS_BACKGROUND_COLOR_DEFAULT);
        setTodaysEventsBackgroundColor(PREF_TODAYS_EVENTS_BACKGROUND_COLOR_DEFAULT);
        setEventsBackgroundColor(PREF_EVENTS_BACKGROUND_COLOR_DEFAULT);
    }

    public ThemeColors copy(Context context, ColorThemeType colorThemeType) {
        ThemeColors themeColors = new ThemeColors(context, colorThemeType);
        return isEmpty()
                ? themeColors
                : themeColors.setFromJson(toJson(new JSONObject()));
    }

    private ThemeColors setFromJson(JSONObject json) {
        try {
            if (json.has(PREF_WIDGET_HEADER_BACKGROUND_COLOR)) {
                setWidgetHeaderBackgroundColor(json.getInt(PREF_WIDGET_HEADER_BACKGROUND_COLOR));
            }
            if (json.has(PREF_PAST_EVENTS_BACKGROUND_COLOR)) {
                setPastEventsBackgroundColor(json.getInt(PREF_PAST_EVENTS_BACKGROUND_COLOR));
            }
            if (json.has(PREF_TODAYS_EVENTS_BACKGROUND_COLOR)) {
                setTodaysEventsBackgroundColor(json.getInt(PREF_TODAYS_EVENTS_BACKGROUND_COLOR));
            }
            if (json.has(PREF_EVENTS_BACKGROUND_COLOR)) {
                setEventsBackgroundColor(json.getInt(PREF_EVENTS_BACKGROUND_COLOR));
            }
            if (json.has(PREF_EVENTS_BACKGROUND_COLOR)) {
                textColorSource = TextColorSource.fromValue(json.getString(PREF_TEXT_COLOR_SOURCE));
            } else {
                // This was default before v.4.4
                textColorSource = TextColorSource.SHADING;
            }

            for (TextColorPref pref: TextColorPref.values()) {
                if (json.has(pref.shadingPreferenceName)) {
                    textColors.put(pref,
                            TextShading.fromThemeName(json.getString(pref.shadingPreferenceName), pref.defaultShading));
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "setFromJson failed\n" + json);
            return this;
        }
        return this;
    }

    public ThemeColors setFromApplicationPreferences() {
        setWidgetHeaderBackgroundColor(ApplicationPreferences.getWidgetHeaderBackgroundColor(context));
        setPastEventsBackgroundColor(ApplicationPreferences.getPastEventsBackgroundColor(context));
        setTodaysEventsBackgroundColor(ApplicationPreferences.getTodaysEventsBackgroundColor(context));
        setEventsBackgroundColor(ApplicationPreferences.getEventsBackgroundColor(context));
        textColorSource = ApplicationPreferences.getTextColorSource(context);
        if (textColorSource == TextColorSource.SHADING) {
            for (TextColorPref pref: TextColorPref.values()) {
                String themeName = ApplicationPreferences.getString(context, pref.shadingPreferenceName, "");
                if (StringUtil.nonEmpty(themeName)) {
                    textColors.put(pref, TextShading.fromThemeName(themeName, pref.defaultShading));
                }
            }
        }
        return this;
    }

    public JSONObject toJson(JSONObject json) {
        try {
            json.put(PREF_WIDGET_HEADER_BACKGROUND_COLOR, widgetHeaderBackgroundColor);
            json.put(PREF_PAST_EVENTS_BACKGROUND_COLOR, pastEventsBackgroundColor);
            json.put(PREF_TODAYS_EVENTS_BACKGROUND_COLOR, todaysEventsBackgroundColor);
            json.put(PREF_EVENTS_BACKGROUND_COLOR, eventsBackgroundColor);
            json.put(PREF_TEXT_COLOR_SOURCE, textColorSource.value);
            for (TextColorPref pref: TextColorPref.values()) {
                json.put(pref.shadingPreferenceName, getShading(pref).themeName);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Saving settings to JSON", e);
        }
        return json;
    }

    public Context getContext() {
        return context;
    }

    public int getWidgetHeaderBackgroundColor() {
        return widgetHeaderBackgroundColor;
    }

    private void setWidgetHeaderBackgroundColor(int widgetHeaderBackgroundColor) {
        this.widgetHeaderBackgroundColor = widgetHeaderBackgroundColor;
        widgetHeaderBackgroundShading = colorToShadingPref(widgetHeaderBackgroundColor);
    }

    public int getPastEventsBackgroundColor() {
        return pastEventsBackgroundColor;
    }

    private void setPastEventsBackgroundColor(int pastEventsBackgroundColor) {
        this.pastEventsBackgroundColor = pastEventsBackgroundColor;
        pastEventsBackgroundShading = colorToShadingPref(pastEventsBackgroundColor);
    }

    public int getTodaysEventsBackgroundColor() {
        return todaysEventsBackgroundColor;
    }

    private void setTodaysEventsBackgroundColor(int todaysEventsBackgroundColor) {
        this.todaysEventsBackgroundColor = todaysEventsBackgroundColor;
        todaysEventsBackgroundShading = colorToShadingPref(todaysEventsBackgroundColor);
    }

    public int getEventsBackgroundColor() {
        return eventsBackgroundColor;
    }

    private void setEventsBackgroundColor(int eventsBackgroundColor) {
        this.eventsBackgroundColor = eventsBackgroundColor;
        eventsBackgroundShading = colorToShadingPref(eventsBackgroundColor);
    }

    public boolean isEmpty() {
        return context == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThemeColors settings = (ThemeColors) o;
        return toJson(new JSONObject()).toString().equals(settings.toJson(new JSONObject()).toString());
    }

    @Override
    public int hashCode() {
        return toJson(new JSONObject()).toString().hashCode();
    }

    public TextShading getShading(TextColorPref pref) {
        switch (textColorSource) {
            case SHADING:
                TextShading shading = textColors.get(pref);
                return shading == null ? pref.defaultShading : shading;
            case COLORS:
                // TODO
            default:
                return pref.getShadingForBackground(getBackgroundShading(pref));
        }
    }

    public final TextShading getBackgroundShading(TextColorPref pref) {
        switch (pref) {
            case WIDGET_HEADER:
                return widgetHeaderBackgroundShading;
            case DAY_HEADER_PAST:
            case ENTRY_PAST:
                return pastEventsBackgroundShading;
            case DAY_HEADER_TODAY:
            case ENTRY_TODAY:
                return todaysEventsBackgroundShading;
            case DAY_HEADER_FUTURE:
            case ENTRY_FUTURE:
                return eventsBackgroundShading;
            default:
                throw new IllegalArgumentException("TextShadingPref: " + pref);
        }
    }

    public int getEntryBackgroundColor(WidgetEntry<?> entry) {
        return entry.timeSection
                .select(getPastEventsBackgroundColor(), getTodaysEventsBackgroundColor(), getEventsBackgroundColor());
    }

    public ContextThemeWrapper getShadingContext(TextColorPref pref) {
        return new ContextThemeWrapper(context, getShading(pref).themeResId);
    }

    public static TextShading colorToShadingPref(@ColorInt int color) {
        float r = ((color >> 16) & 0xff) / 255.0f;
        float g = ((color >>  8) & 0xff) / 255.0f;
        float b = ((color      ) & 0xff) / 255.0f;

        // The formula is from https://stackoverflow.com/a/596243/297710
        double luminance = Math.sqrt(0.299 * r * r + 0.587 * g * g + 0.114 * b * b);

        // And this is my own guess
        if (luminance >= 0.70) {
            return TextShading.WHITE;
        } else if (luminance >= 0.5) {
            return TextShading.LIGHT;
        } else if (luminance >= 0.30) {
            return TextShading.DARK;
        } else {
            return TextShading.BLACK;
        }
    }
}
