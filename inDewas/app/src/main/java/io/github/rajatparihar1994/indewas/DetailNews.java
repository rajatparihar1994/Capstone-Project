package io.github.rajatparihar1994.indewas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import io.github.rajatparihar1994.indewas.model.News;
import io.github.rajatparihar1994.indewas.utils.Constants;

public class DetailNews extends AppCompatActivity {

    private ImageView newsDetailImageView;
    private TextView newsHeadlineTextView, newsDetailTextView;
    private News currentNews;

    private SharedPreferences sharedPreferences;
    private StorageReference mStorageReference;
    private StorageReference filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "inDewas");
                intent.putExtra(Intent.EXTRA_TEXT, currentNews.getHeadline());
                startActivity(intent.createChooser(intent, "Pick one to share"));

            }
        });


        mStorageReference = FirebaseStorage.getInstance().getReference();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean show_image = sharedPreferences.getBoolean("IMAGE", Boolean.TRUE);


        Intent intent = getIntent();
        currentNews = intent.getParcelableExtra("singleNews");

        Log.e("DetailNews", currentNews.getImage() + "");
        newsDetailImageView = (ImageView) findViewById(R.id.image_view_news_image);
        newsHeadlineTextView = (TextView) findViewById(R.id.text_view_news_headline);
        newsDetailTextView = (TextView) findViewById(R.id.text_view_news_detail);

        newsHeadlineTextView.setText(currentNews.getHeadline());
        newsDetailTextView.setText(currentNews.getNews_content());

        if (show_image) {
            filePath = mStorageReference.child(Constants.FIREBASE_IMAGE_PATH).child(currentNews.getImage());
            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext())
                            .load(uri).placeholder(R.drawable.no_image_available)
                            .error(R.drawable.no_image_available)
                            .into(newsDetailImageView);
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
