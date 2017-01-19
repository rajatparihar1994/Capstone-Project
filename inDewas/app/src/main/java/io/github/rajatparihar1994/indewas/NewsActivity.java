package io.github.rajatparihar1994.indewas;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import io.github.rajatparihar1994.indewas.database.NewsContract;
import io.github.rajatparihar1994.indewas.database.NewsDbHealper;
import io.github.rajatparihar1994.indewas.model.News;
import io.github.rajatparihar1994.indewas.sync.inDewasSyncAdapter;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    public static SharedPreferences sharedPreferences = null;
    List<News> newsList = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;

    private ListView listView;
    private NewsAdapter mNewsAdapter;

    private boolean show_image_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        listView = (ListView) findViewById(R.id.list);

        if (isNetworkAvailable()) {
            // Fetch data form Firebase
            fetchNewsFromDatabase();
            fetchNewsFromFirebase();
        } else {
            fetchNewsFromDatabase();
            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void fetchNewsFromFirebase() {
        inDewasSyncAdapter.syncImmediately(getApplicationContext());
    }

    public void fetchNewsFromDatabase() {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_check);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        show_image_status = sharedPreferences.getBoolean("IMAGE", Boolean.TRUE);
        item.setChecked(show_image_status);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_check) {


            if (item.isChecked()) {
                // If item already checked then unchecked it
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IMAGE", Boolean.FALSE).apply();
                item.setChecked(false);
                fetchNewsFromDatabase();
                Toast.makeText(getApplicationContext(), R.string.dontShowImageCheckbox, Toast.LENGTH_SHORT).show();


            } else {
                // If item is unchecked then checked it

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IMAGE", Boolean.TRUE).apply();
                item.setChecked(true);
                fetchNewsFromDatabase();


                Toast.makeText(getApplicationContext(), R.string.showImagecheckbox, Toast.LENGTH_SHORT).show();


            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String projection[] = {
                NewsContract.NewsEntry.COLUMN_NEWSID,
                NewsContract.NewsEntry.COLUMN_HEADLINE,
                NewsContract.NewsEntry.COLUMN_NEWS_CONTENT,
                NewsContract.NewsEntry.COLUMN_DATE,
                NewsContract.NewsEntry.COLUMN_TIME,
                NewsContract.NewsEntry.COLUMN_IMAGE
        };

        String sortOrder = NewsContract.NewsEntry.COLUMN_ID + " DESC ";
        Uri uri = NewsContract.NewsEntry.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(this);
        cursorLoader.setUri(uri);
        cursorLoader.setProjection(projection);
        cursorLoader.setSelection(null);
        cursorLoader.setSelectionArgs(null);
        cursorLoader.setSortOrder(null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursorData) {
        if (cursorData.moveToFirst()) {
            int columnCount = cursorData.getColumnCount();
            Log.e("FetchAllNews ", "Data present in database " + columnCount + "");
            newsList.clear();
            do {
                String headline, content, date, time, image;
                Long newsid;


                int newsIdColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWSID);
                int contentColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_CONTENT);
                int headlineColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_HEADLINE);
                int dateColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_DATE);
                int timeColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_TIME);
                int imageColumnIndex = cursorData.getColumnIndex(NewsContract.NewsEntry.COLUMN_IMAGE);

                newsid = cursorData.getLong(newsIdColumnIndex);
                headline = cursorData.getString(headlineColumnIndex);
                content = cursorData.getString(contentColumnIndex);
                date = cursorData.getString(dateColumnIndex);
                time = cursorData.getString(timeColumnIndex);
                image = cursorData.getString(imageColumnIndex);
                News newsProvider = new News(headline, content, newsid, image, date, time);
                newsList.add(newsProvider);
            } while (cursorData.moveToNext());

            mNewsAdapter = new NewsAdapter(this, R.layout.news_list_item, newsList);
            listView.setAdapter(mNewsAdapter);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.toastmessagenodata), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
