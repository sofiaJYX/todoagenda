package org.andstatus.todoagenda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.andstatus.todoagenda.util.IntentUtil;

/**
 * @author yvolk@yurivolkov.com
 */
public class ErrorReportActivity extends AppCompatActivity {
    public static final String EXTRA_APP_MESSAGE = RemoteViewsFactory.PACKAGE + ".extra.APP_MESSAGE";
    private static final String TAG = ErrorReportActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_report);

        EditText appMessage = findViewById(R.id.appMessage);
        if (appMessage != null) {
            appMessage.setText(getIntent().getStringExtra(EXTRA_APP_MESSAGE));
        }
    }

    public void onOkButtonClick(View view) {
        finish();
    }

    public static void showMessage(Context context, String message, @NonNull Exception exception) {
        Intent intent = IntentUtil.createViewIntent();
        intent.setClass(context, ErrorReportActivity.class);
        intent.putExtra(EXTRA_APP_MESSAGE, message + "\n\n" +
                "Caused by: " + exception.getClass() + ",\n" + exception.getMessage());
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            String msgLog = "Failed to start " + TAG + ". " + message;
            Log.e(TAG, msgLog, e);
            throw new RuntimeException(msgLog, e);
        }
    }
}
