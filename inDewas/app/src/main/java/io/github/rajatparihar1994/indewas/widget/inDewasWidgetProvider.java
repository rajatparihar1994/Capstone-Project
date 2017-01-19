package io.github.rajatparihar1994.indewas.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.rajatparihar1994.indewas.sync.inDewasSyncAdapter;

/**
 * Created by rajpa on 28-Dec-16.
 */

public class inDewasWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, inDewasWidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (inDewasSyncAdapter.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, inDewasWidgetIntentService.class));
        }
    }
}
