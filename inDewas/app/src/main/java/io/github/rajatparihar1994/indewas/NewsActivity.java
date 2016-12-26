package io.github.rajatparihar1994.indewas;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.rajatparihar1994.indewas.model.News;

public class NewsActivity extends AppCompatActivity {

    List<News> newsList = new ArrayList<>();
    private ListView listView;
    private NewsAdapter mNewsAdapter;



    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference newsRef;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mNewsPhotoStorageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        newsRef = mFirebaseDatabase.getReference(getString(R.string.firebse_database_news));
        mFirebaseStorage = FirebaseStorage.getInstance();
        mNewsPhotoStorageReference = mFirebaseStorage.getReference();
        Query queryNewRef = newsRef.orderByChild(getString(R.string.firebse_database_newsid));

        fetchNews(queryNewRef);


        mNewsAdapter = new NewsAdapter(this,R.layout.news_list_item,newsList, mNewsPhotoStorageReference);

        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mNewsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News singleNews = mNewsAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(),DetailNews.class);
                intent.putExtra("singleNews", singleNews);
                startActivity(intent);


            }
        });

    }


    public void fetchNews(Query newsRef) {

        newsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot chilDataSnapshot : dataSnapshot.getChildren()){
                        News news = chilDataSnapshot.getValue(News.class);
                        mNewsAdapter.add(news);
                    }

                }
                Collections.reverse(newsList);
                Toast.makeText(getApplicationContext(),R.string.news_updated,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.checkbox)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
