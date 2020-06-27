package org.andstatus.todoagenda.util;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import org.andstatus.todoagenda.prefs.InstanceSettings;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class CalendarIntentUtil {

    private static final String KEY_DETAIL_VIEW = "DETAIL_VIEW";
    private static final String TIME = "time";

    public static Intent createOpenCalendarAtDayIntent(DateTime goToTime) {
        Intent intent = IntentUtil.createViewIntent();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath(TIME);
        if (goToTime.getMillis() != 0) {
            intent.putExtra(KEY_DETAIL_VIEW, true);
            ContentUris.appendId(builder, goToTime.getMillis());
        }
        intent.setData(builder.build());
        return intent;
    }

    public static PendingIntent createOpenCalendarPendingIntent(InstanceSettings settings) {
        return PermissionsUtil.getPermittedPendingActivityIntent(settings,
                createOpenCalendarAtDayIntent(new DateTime(settings.clock().getZone())));
    }

    public static Intent createNewEventIntent(DateTimeZone timeZone) {
        DateTime beginTime = new DateTime(timeZone).plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0)
                .withMillisOfSecond(0);
        DateTime endTime = beginTime.plusHours(1);
        return new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getMillis());
    }
}
