package io.github.rajatparihar1994.indewas.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rajpa on 27-Dec-16.
 */

public class NewsContract {

    public static final String CONTENT_AUTHORITY = "io.github.rajatparihar1994.indewas";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NEWS = "news";

    public NewsContract() {
    }

    public static final class NewsEntry implements BaseColumns {

        public static final String TABLE_NAME = "news";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NEWSID = "newsid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_HEADLINE = "headline";
        public static final String COLUMN_NEWS_CONTENT = "news_content";
        public static final String COLUMN_IMAGE = "image";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS);
    }

}
