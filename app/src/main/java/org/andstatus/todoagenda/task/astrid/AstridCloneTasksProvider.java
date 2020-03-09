package org.andstatus.todoagenda.task.astrid;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import org.andstatus.todoagenda.prefs.EventSource;
import org.andstatus.todoagenda.prefs.FilterMode;
import org.andstatus.todoagenda.prefs.OrderedEventSource;
import org.andstatus.todoagenda.provider.EventProviderType;
import org.andstatus.todoagenda.task.AbstractTaskProvider;
import org.andstatus.todoagenda.task.TaskEvent;
import org.andstatus.todoagenda.task.TaskStatus;
import org.andstatus.todoagenda.util.CalendarIntentUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.vavr.control.Try;

public class AstridCloneTasksProvider extends AbstractTaskProvider {
    private static final String APPLICATION_ID = "org.tasks.debug";  // TODO: Single value for all build variants
    public static final String AUTHORITY = APPLICATION_ID + ".tasksprovider";
    public static final String PERMISSION = "org.tasks.permission.READ_TASKS";
    private static final String CONTENT_URI_STRING = "content://" + AUTHORITY;
    private static final Uri TASKS_URI = Uri.parse(CONTENT_URI_STRING + "/tasks");
    private static final Uri TAGS_URI = Uri.parse(CONTENT_URI_STRING + "/tags");

    private static final String TASKS_COLUMN_LIST_ID = "tags_id";
    private static final String TASKS_COLUMN_ID = "identifier";
    private static final String TASKS_COLUMN_TITLE = "name";
    private static final String TASKS_COLUMN_DUE_DATE = "definiteDueDate";
    private static final String TASKS_COLUMN_START_DATE = "preferredDueDate";  // TODO: Add Start Date ?!
    private static final String TASKS_COLUMN_IMPORTANCE = "importance";
    private static final String TASKS_COLUMN_COLOR = "importance_color";
    private static final String TASKS_COLUMN_STATUS = "???";  // TODO: Add status

    private static final String LISTS_COLUMN_ID = "id";
    private static final String LISTS_COLUMN_TITLE = "name";

    public AstridCloneTasksProvider(EventProviderType type, Context context, int widgetId) {
        super(type, context, widgetId);
    }

    @Override
    public List<TaskEvent> queryTasks() {
        myContentResolver.onQueryEvents();

        Uri uri = TASKS_URI;
        String[] projection = {
                TASKS_COLUMN_TITLE,
                TASKS_COLUMN_COLOR,
                TASKS_COLUMN_START_DATE,
                TASKS_COLUMN_DUE_DATE,
                TASKS_COLUMN_IMPORTANCE,
                TASKS_COLUMN_ID,
                TASKS_COLUMN_LIST_ID,
        };
        String where = getWhereClause();

        return myContentResolver.foldEvents(uri, projection, where, null, null,
                new ArrayList<>(), tasks -> cursor -> {
                    TaskEvent task = createTask(cursor);
                    if (matchedFilter(task)) {
                        tasks.add(task);
                    }
                    return tasks;
                });
    }

    private String getWhereClause() {
        StringBuilder whereBuilder = new StringBuilder();

        if (getFilterMode() == FilterMode.NORMAL_FILTER) {
            // TODO: exclude completed

            // TODO: filter by start date
        }

        Set<String> taskLists = new HashSet<>();
        for (OrderedEventSource orderedSource: getSettings().getActiveEventSources(type)) {
            taskLists.add(Integer.toString(orderedSource.source.getId()));
        }
        if (!taskLists.isEmpty()) {
            // TODO: filter by task lists

            if (whereBuilder.length() > 0) {
                whereBuilder.append(AND);
            }
            whereBuilder.append(TASKS_COLUMN_LIST_ID);
            whereBuilder.append(" IN ( ");
            whereBuilder.append(TextUtils.join(",", taskLists));
            whereBuilder.append(CLOSING_BRACKET);
        }

        return whereBuilder.toString();
    }

    private TaskEvent createTask(Cursor cursor) {
        OrderedEventSource source = getSettings()
                .getActiveEventSource(type,
                        cursor.getInt(cursor.getColumnIndex(TASKS_COLUMN_LIST_ID)));
        TaskEvent task = new TaskEvent(getSettings(), getSettings().clock().getZone());
        task.setEventSource(source);
        task.setId(cursor.getLong(cursor.getColumnIndex(TASKS_COLUMN_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndex(TASKS_COLUMN_TITLE)));

        int startDateIdx = cursor.getColumnIndex(TASKS_COLUMN_START_DATE);
        Long startMillis = cursor.isNull(startDateIdx) ? null : cursor.getLong(startDateIdx);
        int dueDateIdx = cursor.getColumnIndex(TASKS_COLUMN_DUE_DATE);
        Long dueMillis = cursor.isNull(dueDateIdx) ? null : cursor.getLong(dueDateIdx);
        task.setDates(startMillis, dueMillis);

        task.setColor(getAsOpaque(cursor.getInt(cursor.getColumnIndex(TASKS_COLUMN_COLOR))));
        task.setStatus(loadStatus(cursor));

        return task;
    }

    // TODO: No status yet...
    private TaskStatus loadStatus(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(TASKS_COLUMN_STATUS);
        if (columnIndex < 0) return TaskStatus.UNKNOWN;

        switch (cursor.getInt(columnIndex)) {
            case 0:
                return TaskStatus.NEEDS_ACTION;
            case 1:
                return TaskStatus.IN_PROGRESS;
            case 2:
                return TaskStatus.COMPLETED;
            case 3:
                return TaskStatus.CANCELLED;
            default:
                return TaskStatus.UNKNOWN;
        }
    }

    @Override
    public Try<List<EventSource>> fetchAvailableSources() {
        String[] projection = {
                LISTS_COLUMN_ID,
                LISTS_COLUMN_TITLE,
                // TODO: Add List color
        };

        return myContentResolver.foldAvailableSources(
                TAGS_URI,
                projection,
                new ArrayList<>(),
                eventSources -> cursor -> {
                    int indId = cursor.getColumnIndex(LISTS_COLUMN_ID);
                    int indTitle = cursor.getColumnIndex(LISTS_COLUMN_TITLE);
                    // TODO: Color
                    EventSource source = new EventSource(type, cursor.getInt(indId), cursor.getString(indTitle),
                            "", 0, true);
                    eventSources.add(source);
                    return eventSources;
                });
    }

    @Override
    public Intent createViewEventIntent(TaskEvent event) {
        Intent intent = CalendarIntentUtil.createViewIntent();
        // TODO: How do we view / Edit task?
        intent.setData(ContentUris.withAppendedId(TASKS_URI, event.getId()));
        return intent;
    }
}
