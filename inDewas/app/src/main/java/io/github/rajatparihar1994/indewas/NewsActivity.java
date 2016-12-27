package io.github.rajatparihar1994.indewas;


import android.content.ContentValues;
import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.rajatparihar1994.indewas.database.NewsContract;
import io.github.rajatparihar1994.indewas.database.NewsDbHealper;
import io.github.rajatparihar1994.indewas.model.News;

public class NewsActivity extends AppCompatActivity {

    List<News> newsList = new ArrayList<>();
    private ListView listView;
    private NewsAdapter mNewsAdapter;


    private NewsDbHealper newsDbHealper;
    SQLiteDatabase db;
    Cursor cursorData;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference newsRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mNewsPhotoStorageReference;

    private CheckBox checkBox_show_image_option;

    private boolean show_image_status;
    public static SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        newsRef = mFirebaseDatabase.getReference(getString(R.string.firebse_database_news));
        mFirebaseStorage = FirebaseStorage.getInstance();
        mNewsPhotoStorageReference = mFirebaseStorage.getReference();
        listView = (ListView) findViewById(R.id.list);

        newsDbHealper = new NewsDbHealper(getBaseContext());
        db = newsDbHealper.getWritableDatabase();



        if (isNetworkAvailable()) {
            // Fetch data form Firebase
            fetchNewsFromDatabase();
            fetchNewsFromFirebase(newsRef);
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

    public void fetchNewsFromFirebase(Query newsRef) {

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

                        Uri insertUri = getContentResolver().insert(NewsContract.NewsEntry.CONTENT_URI, values);


                    }

                }
                fetchNewsFromDatabase();
                Toast.makeText(getApplicationContext(), R.string.news_updated, Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void fetchNewsFromDatabase() {


        String projection[] = {
                NewsContract.NewsEntry.COLUMN_NEWSID,
                NewsContract.NewsEntry.COLUMN_HEADLINE,
                NewsContract.NewsEntry.COLUMN_NEWS_CONTENT,
                NewsContract.NewsEntry.COLUMN_DATE,
                NewsContract.NewsEntry.COLUMN_TIME,
                NewsContract.NewsEntry.COLUMN_IMAGE
        };

        Uri uri = NewsContract.NewsEntry.CONTENT_URI;
        cursorData = getContentResolver().query(uri, projection, null, null, null);

        try {
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
                    Collections.reverse(newsList);
                } while (cursorData.moveToNext());

                mNewsAdapter = new NewsAdapter(this, R.layout.news_list_item, newsList);
                listView.setAdapter(mNewsAdapter);



            }
        } finally {
            cursorData.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_check);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        show_image_status = sharedPreferences.getBoolean("IMAGE",Boolean.TRUE);
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
                editor.putBoolean("IMAGE",Boolean.FALSE).apply();
                item.setChecked(false);
                fetchNewsFromDatabase();
                Toast.makeText(getApplicationContext(), R.string.dontShowImageCheckbox, Toast.LENGTH_SHORT).show();


            } else {
                // If item is unchecked then checked it

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IMAGE",Boolean.TRUE).apply();
                item.setChecked(true);
                fetchNewsFromDatabase();


                Toast.makeText(getApplicationContext(), R.string.showImagecheckbox , Toast.LENGTH_SHORT).show();


                }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
