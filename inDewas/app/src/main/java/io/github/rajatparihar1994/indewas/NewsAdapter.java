package io.github.rajatparihar1994.indewas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;

import io.github.rajatparihar1994.indewas.model.News;
import io.github.rajatparihar1994.indewas.utils.Constants;

/**
 * Created by rajpa on 24-Dec-16.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private StorageReference mStorageRef;


    private SharedPreferences sharedPreferences;

    private static final int VIEW_TYPE_FIRST_NEWS = 0;
    private static final int VIEW_TYPE_AFTER_FIRST_NEWS = 1;

    private View listItemView;
    private StorageReference filePathRef;
    private Uri imageUri;



    public NewsAdapter(Context context, int resource, List<News> newsList) {
        super(context, resource, newsList);

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean show_image = sharedPreferences.getBoolean("IMAGE",Boolean.FALSE);


        listItemView = convertView;



        if (listItemView == null) {
            int layoutno = position;
            Log.e("LayoutNo",position+"");
            if(layoutno == 0)
            {

                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.first_news_list_item, parent, false);
            }
            else
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        final News currentNews = getItem(position);



        TextView newsHeadline = (TextView) listItemView.findViewById(R.id.news_headline);
        newsHeadline.setText(currentNews.getHeadline());


        if(show_image)
        {

            final ImageView imageview = (ImageView) listItemView.findViewById(R.id.news_image);

            filePathRef = mStorageRef.child(Constants.FIREBASE_IMAGE_PATH).child(currentNews.getImage());

            filePathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageUri = uri;
                    Log.e("NewsAdapter Uri", uri + "");
                    Picasso.with(getContext())
                            .load(uri).placeholder(R.drawable.no_image_available)
                            .error(R.drawable.no_image_available)
                            .into(imageview);
                }
            });

        }
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DetailNews.class);
                intent.putExtra("singleNews", currentNews);
                intent.putExtra("currentNews_image", imageUri+"");
                getContext().startActivity(intent);

            }
        });


        return listItemView;
    }
}
