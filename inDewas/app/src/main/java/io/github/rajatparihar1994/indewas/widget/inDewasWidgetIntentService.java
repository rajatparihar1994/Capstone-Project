package io.github.rajatparihar1994.indewas.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import io.github.rajatparihar1994.indewas.NewsActivity;
import io.github.rajatparihar1994.indewas.R;
import io.github.rajatparihar1994.indewas.database.NewsContract;

/**
 * Created by rajpa on 28-Dec-16.
 */

public class inDewasWidgetIntentService extends IntentService {
    public inDewasWidgetIntentService() {
        super("inDewasWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, inDewasWidgetProvider.class));

        String projection[] = {
                NewsContract.NewsEntry.COLUMN_NEWSID,
                NewsContract.NewsEntry.COLUMN_HEADLINE,
                NewsContract.NewsEntry.COLUMN_DATE,
                NewsContract.NewsEntry.COLUMN_IMAGE
        };

        String sortOrder = NewsContract.NewsEntry.COLUMN_NEWSID + " ASC LIMIT 1";
        Uri uri = NewsContract.NewsEntry.CONTENT_URI;

        Cursor cursorData = getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursorData == null) {
            return;
        }
        if (!cursorData.moveToFirst()) {
            cursorData.close();
            return;
        }

        int newsIdColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWSID);
        int headlineColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_HEADLINE);
        int dateColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_DATE);
        int imageColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE);

        Long newsid = cursorData.getLong(newsIdColumnIndex);
        String headline = cursorData.getString(headlineColumnIndex);
        String date = cursorData.getString(dateColumnIndex);
        String image = cursorData.getString(imageColumnIndex);

        for (final int appWidgetId : appWidgetIds) {
            final RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_indewas_large);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, "Clear");
            }
            views.setTextViewText(R.id.widget_headline, headline);
            views.setImageViewResource(R.id.widget_image, R.drawable.no_image_available);

            StorageReference filePathRef = mStorageReference.child(getString(R.string.firebase_storage_newsImages)).child(image);

            filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.e("WidgetIntentService", uri + " ");
                    views.setImageViewUri(R.id.widget_image, uri);
                    Picasso.with(getApplicationContext()).load(uri)

                            .into(views,R.id.widget_image, appWidgetIds);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, NewsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_image, description);
    }
}
