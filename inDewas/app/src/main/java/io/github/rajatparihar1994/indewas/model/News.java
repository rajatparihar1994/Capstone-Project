package io.github.rajatparihar1994.indewas.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by rajpa on 24-Dec-16.
 */

public class News implements Parcelable {

    public static final Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
    public String headline;
    public String news_content;
    public String image;
    public String date;
    public String time;
    public Long newsid;

    public News() {
    }

    public News(String headline, String news_content, Long newsid, String image, String date, String time) {
        this.headline = headline;
        this.news_content = news_content;
        this.image = image;
        this.date = date;
        this.time = time;
        this.newsid = newsid;
    }

    private News(Parcel in) {
        headline = in.readString();
        news_content = in.readString();
        newsid = in.readLong();
        image = in.readString();
        date = in.readString();
        time = in.readString();
    }

    public String getHeadline() {
        return headline;
    }

    public String getNews_content() {
        return news_content;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Long getNewsid() {
        return newsid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(headline);
        parcel.writeString(news_content);
        parcel.writeLong(newsid);
        parcel.writeString(image);
        parcel.writeString(date);
        parcel.writeString(time);
    }


}
