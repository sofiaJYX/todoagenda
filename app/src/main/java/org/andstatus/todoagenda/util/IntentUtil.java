package org.andstatus.todoagenda.util;

import android.content.Intent;

public class IntentUtil {
    public static Intent newViewIntent() {
        return newIntent(Intent.ACTION_VIEW);
    }

    public static Intent newIntent(String action) {
        return new Intent(action)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
