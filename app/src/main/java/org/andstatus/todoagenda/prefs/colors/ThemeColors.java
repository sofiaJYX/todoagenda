package org.andstatus.todoagenda.prefs.colors;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextThemeWrapper;

import androidx.annotation.ColorInt;

import org.andstatus.todoagenda.prefs.ApplicationPreferences;
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
    public final static ThemeColors EMPTY = new ThemeColors(null, ColorThemeType.SINGLE);
    private final Context context;
    public final ColorThemeType colorThemeType;

    public static final String PREF_WIDGET_HEADER_BACKGROUND_COLOR = "widgetHeaderBackgroundColor";
    @ColorInt public static final int PREF_WIDGET_HEADER_BACKGROUND_COLOR_DEFAULT = Color.TRANSPARENT;
    private int widgetHeaderBackgroundColor = PREF_WIDGET_HEADER_BACKGROUND_COLOR_DEFAULT;
    public static final String PREF_PAST_EVENTS_BACKGROUND_COLOR = "pastEventsBackgroundColor";
    @ColorInt public static final int PREF_PAST_EVENTS_BACKGROUND_COLOR_DEFAULT = 0xBF78782C;
    private int pastEventsBackgroundColor = PREF_PAST_EVENTS_BACKGROUND_COLOR_DEFAULT;
    public static final String PREF_TODAYS_EVENTS_BACKGROUND_COLOR = "todaysEventsBackgroundColor";
    @ColorInt public static final int PREF_TODAYS_EVENTS_BACKGROUND_COLOR_DEFAULT = 0xDAFFFFFF;
    private int todaysEventsBackgroundColor = PREF_TODAYS_EVENTS_BACKGROUND_COLOR_DEFAULT;
    public static final String PREF_EVENTS_BACKGROUND_COLOR = "backgroundColor";
    @ColorInt public static final int PREF_EVENTS_BACKGROUND_COLOR_DEFAULT = 0x80000000;
    private int eventsBackgroundColor = PREF_EVENTS_BACKGROUND_COLOR_DEFAULT;

    public final Map<TextShadingPref, TextShading> shadings = new ConcurrentHashMap<>();

    public static ThemeColors fromJson(Context context, ColorThemeType colorThemeType, JSONObject json) {
        return new ThemeColors(context, colorThemeType).setFromJson(json);
    }

    public ThemeColors(Context context, ColorThemeType colorThemeType) {
        this.context = context;
        this.colorThemeType = colorThemeType;
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
                widgetHeaderBackgroundColor = json.getInt(PREF_WIDGET_HEADER_BACKGROUND_COLOR);
            }
            if (json.has(PREF_PAST_EVENTS_BACKGROUND_COLOR)) {
                pastEventsBackgroundColor = json.getInt(PREF_PAST_EVENTS_BACKGROUND_COLOR);
            }
            if (json.has(PREF_TODAYS_EVENTS_BACKGROUND_COLOR)) {
                todaysEventsBackgroundColor = json.getInt(PREF_TODAYS_EVENTS_BACKGROUND_COLOR);
            }
            if (json.has(PREF_EVENTS_BACKGROUND_COLOR)) {
                eventsBackgroundColor = json.getInt(PREF_EVENTS_BACKGROUND_COLOR);
            }

            for (TextShadingPref pref: TextShadingPref.values()) {
                if (json.has(pref.preferenceName)) {
                    shadings.put(pref,
                            TextShading.fromName(json.getString(pref.preferenceName), pref.defaultShading));
                }
            }
        } catch (JSONException e) {
            Log.w(TAG, "setFromJson failed\n" + json);
            return this;
        }
        return this;
    }

    public ThemeColors setFromApplicationPreferences() {
        widgetHeaderBackgroundColor = ApplicationPreferences.getWidgetHeaderBackgroundColor(context);
        pastEventsBackgroundColor = ApplicationPreferences.getPastEventsBackgroundColor(context);
        todaysEventsBackgroundColor = ApplicationPreferences.getTodaysEventsBackgroundColor(context);
        eventsBackgroundColor = ApplicationPreferences.getEventsBackgroundColor(context);
        for (TextShadingPref pref: TextShadingPref.values()) {
            String themeName = ApplicationPreferences.getString(context, pref.preferenceName,
                    pref.defaultShading.name());
            shadings.put(pref, TextShading.fromName(themeName, pref.defaultShading));
        }
        return this;
    }

    public JSONObject toJson(JSONObject json) {
        try {
            json.put(PREF_WIDGET_HEADER_BACKGROUND_COLOR, widgetHeaderBackgroundColor);
            json.put(PREF_PAST_EVENTS_BACKGROUND_COLOR, pastEventsBackgroundColor);
            json.put(PREF_TODAYS_EVENTS_BACKGROUND_COLOR, todaysEventsBackgroundColor);
            json.put(PREF_EVENTS_BACKGROUND_COLOR, eventsBackgroundColor);
            for (TextShadingPref pref: TextShadingPref.values()) {
                json.put(pref.preferenceName, getShading(pref).name());
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

    public int getPastEventsBackgroundColor() {
        return pastEventsBackgroundColor;
    }

    public int getTodaysEventsBackgroundColor() {
        return todaysEventsBackgroundColor;
    }

    public int getEventsBackgroundColor() {
        return eventsBackgroundColor;
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

    public TextShading getShading(TextShadingPref pref) {
        TextShading shading = shadings.get(pref);
        return shading == null ? pref.defaultShading : shading;
    }

    public int getEntryBackgroundColor(WidgetEntry<?> entry) {
        return entry.timeSection
                .select(getPastEventsBackgroundColor(), getTodaysEventsBackgroundColor(), getEventsBackgroundColor());
    }

    public ContextThemeWrapper getShadingContext(TextShadingPref pref) {
        return new ContextThemeWrapper(context, getShading(pref).themeResId);
    }
}
