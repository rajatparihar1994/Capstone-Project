package io.github.rajatparihar1994.indewas.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.rajatparihar1994.indewas.R;
import io.github.rajatparihar1994.indewas.database.NewsContract;
import io.github.rajatparihar1994.indewas.database.NewsDbHealper;
import io.github.rajatparihar1994.indewas.model.News;

/**
 * Created by rajpa on 28-Dec-16.
 */

public class inDewasSyncAdapter extends AbstractThreadedSyncAdapter {

    // Interval at which to sync with the weather, in milliseconds.
// 60 seconds (1 minute)  180 = 3 hours
//    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_INTERVAL = 30;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    public static final String ACTION_DATA_UPDATED = "io.github.rajatparihar1994.indewas.ACTION_DATA_UPDATED";
    SQLiteDatabase db;
    List<News> newsList = new ArrayList<>();
    private NewsDbHealper newsDbHealper;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference newsRef;

    public inDewasSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.e("inDewasSyncAdapter ", " syncAdapter called");
        Bundle bundle = new Bundle();
        Log.e("inDewasSyncAdapter ", " syncAdapter called 2");
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        Log.e("inDewasSyncAdapter ", " syncAdapter called 3");
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        Log.e("inDewasSyncAdapter ", " syncAdapter called 4");
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
        Log.e("inDewasSyncAdapter ", " syncAdapter called 5");
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        inDewasSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

        }
        return newAccount;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e("inDewasSyncAdapter ", " syncAdapter called on PerformSync");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        newsRef = mFirebaseDatabase.getReference("news");

        newsDbHealper = new NewsDbHealper(getContext());
        db = newsDbHealper.getWritableDatabase();

        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsList.clear();
                if (dataSnapshot.exists()) {
                    newsDbHealper.deleteNews(db);
                    for (DataSnapshot chilDataSnapshot : dataSnapshot.getChildren()) {
                        News news = chilDataSnapshot.getValue(News.class);
                        ContentValues values = new ContentValues();
                        values.put(NewsContract.NewsEntry.COLUMN_NEWSID, news.getNewsid() + "");
                        values.put(NewsContract.NewsEntry.COLUMN_DATE, news.getDate() + "");
                        values.put(NewsContract.NewsEntry.COLUMN_TIME, news.getTime() + "");
                        values.put(NewsContract.NewsEntry.COLUMN_HEADLINE, news.getHeadline() + "");
                        values.put(NewsContract.NewsEntry.COLUMN_NEWS_CONTENT, news.getNews_content() + "");
                        values.put(NewsContract.NewsEntry.COLUMN_IMAGE, news.getImage() + "");

                        Uri insertUri = getContext().getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, values);
                    }

                }
                updateWidgets();
                Log.e("inDewasSyncAdapter ", " syncAdapter called");
                Toast.makeText(getContext(), R.string.news_updated, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateWidgets() {
        Log.e("inTouchSyncAdapter ", "updateWidgets called ");
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}
