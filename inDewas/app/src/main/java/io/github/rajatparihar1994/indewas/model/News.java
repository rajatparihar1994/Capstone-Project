package io.github.rajatparihar1994.indewas.model;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Created by rajpa on 24-Dec-16.
 */

public class News implements Parcelable {

    public String headline, news_content, image , date , time ;

    public News() {
    }

    public News(String headline, String news_content, String image, String date, String time) {
        this.headline = headline;
        this.news_content = news_content;
        this.image = image;
        this.date = date;
        this.time = time;
    }

    public News(String headline, String image) {
        this.headline = headline;
        this.image = image;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(getHeadline());
        parcel.writeString(getNews_content());
        parcel.writeString(getImage());
        parcel.writeString(getDate());
        parcel.writeString(getTime());

    }

    private News(Parcel in) {
        headline = in.readString();
        news_content = in.readString();
        date = in.readString();
        time = in.readString();
        image = in.readString();
    }

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



}
