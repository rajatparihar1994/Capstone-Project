package io.github.rajatparihar1994.indewas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.R.attr.version;

/**
 * Created by rajpa on 27-Dec-16.
 */

public class NewsDbHealper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "news.db";

    public NewsDbHealper(Context context){
        super (context,DATABASE_NAME,null,version);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_NEWS_TABLE  = "CREATE TABLE "+ NewsContract.NewsEntry.TABLE_NAME+" (" +
                    NewsContract.NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                NewsContract.NewsEntry.COLUMN_NEWSID +" TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_DATE + " TEXT NOT NULL, "+
                NewsContract.NewsEntry.COLUMN_TIME + " TEXT NOT NULL, "+
                NewsContract.NewsEntry.COLUMN_HEADLINE +" TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_NEWS_CONTENT +" TEXT NOT NULL, " +
                NewsContract.NewsEntry.COLUMN_IMAGE +" TEXT NOT NULL );" ;

                sqLiteDatabase.execSQL(SQL_CREATE_NEWS_TABLE);
                Log.e("DATABASE","database created");
    }

    public void addNews(String news_id,String Date,String Time,String Headline,String Content,String Image,SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(NewsContract.NewsEntry.COLUMN_NEWSID,news_id);
        contentValues.put(NewsContract.NewsEntry.COLUMN_DATE,Date);
        contentValues.put(NewsContract.NewsEntry.COLUMN_TIME,Time);
        contentValues.put(NewsContract.NewsEntry.COLUMN_HEADLINE,Headline);
        contentValues.put(NewsContract.NewsEntry.COLUMN_NEWS_CONTENT,Content);
        contentValues.put(NewsContract.NewsEntry.COLUMN_IMAGE,Image);
        db.insert(NewsContract.NewsEntry.TABLE_NAME,null,contentValues);
    }

    public void deleteNews(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL("DELETE FROM "+NewsContract.NewsEntry.TABLE_NAME);
        Log.e("deleteNews","News Deleted");

    }

    public Cursor getAllNews(SQLiteDatabase db){
        Cursor cursor;
        String projection[] = {
                NewsContract.NewsEntry.COLUMN_NEWSID,
                NewsContract.NewsEntry.COLUMN_HEADLINE,
                NewsContract.NewsEntry.COLUMN_NEWS_CONTENT,
                NewsContract.NewsEntry.COLUMN_DATE,
                NewsContract.NewsEntry.COLUMN_TIME,
                NewsContract.NewsEntry.COLUMN_IMAGE

        };
        cursor =db.query(NewsContract.NewsEntry.TABLE_NAME,projection,null,null,null,null,NewsContract.NewsEntry.COLUMN_NEWSID + "DESC");

        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ NewsContract.NewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
