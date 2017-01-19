package io.github.rajatparihar1994.indewas.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static android.R.attr.id;

/**
 * Created by rajpa on 27-Dec-16.
 */

public class NewsProvider extends ContentProvider {
    private static final int NEWS = 100;
    private static final int NEWS_SINGLE = 101;
    private static final UriMatcher sURI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURI_MATCHER.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS, NEWS);
        sURI_MATCHER.addURI(NewsContract.CONTENT_AUTHORITY, NewsContract.PATH_NEWS + "/*", NEWS_SINGLE);
    }

    private NewsDbHealper newsDbHealper;

    @Override
    public boolean onCreate() {
        newsDbHealper = new NewsDbHealper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = newsDbHealper.getReadableDatabase();

        Cursor cursor;
        int match = sURI_MATCHER.match(uri);
        switch (match) {
            case NEWS:
                cursor = sqLiteDatabase.query(NewsContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            /*case NEWS_SINGLE:
                selection = NewsContract.NewsEntry.COLUMN_NEWSID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(NewsContract.NewsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;*/
            default:
                throw new IllegalArgumentException("Cannot querywithout uri" + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case NEWS:
                return insertNews(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertNews(Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = newsDbHealper.getWritableDatabase();
        sqLiteDatabase.insert(NewsContract.NewsEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
