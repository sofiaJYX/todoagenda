package org.andstatus.todoagenda.util;

import android.content.Intent;

public class IntentUtil {
    public static Intent createViewIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
