package com.ttcreator.mycoloring;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ttcreator.mycoloring.adapters.RecyclerViewAdapter;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllFromCategory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView titleCategory, desctiptionText;
    private ArrayList<CacheImageModel> imageList;
    private String nameCategory;
    private String title;
    private ImageView imageViewBackground;
    private TextView textCountImages;
    private FloatingActionButton closeActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_from_category);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        closeActionButton = findViewById(R.id.closeActionButton);
        desctiptionText = findViewById(R.id.descriptionText);
        textCountImages = findViewById(R.id.textCountImagesFirst);
        imageViewBackground = findViewById(R.id.imageViewBackground);
        recyclerView = findViewById(R.id.recyclerViewAllCategory);
        titleCategory = findViewById(R.id.titleCategory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imageList = new ArrayList<>();
        getDataFromDB();
        CacheImageModel cacheImageModel = new CacheImageModel();
        cacheImageModel = imageList.get(0);
        String urlBackground = cacheImageModel.getImageCacheUrl();
        imageViewBackground.setLayoutParams(new RelativeLayout.LayoutParams(MyApp.getScreenWidth(this) / 5,
                MyApp.getScreenHeight(this) / 8));
        Glide.with(getApplicationContext())
                .load(urlBackground)
                .into(imageViewBackground);
        closeActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getDataFromDB() {

        ContentResolver contentResolver = getContentResolver();
        Intent intent = getIntent();
        nameCategory = intent.getStringExtra("nameCat");
        title = intent.getStringExtra("nameCat");
        titleCategory.setText("This is "  + title + " category");
        switch (nameCategory) {
            case "best":
                desctiptionText.setText("This collection must best images for all time");
                break;
             case "new":
                desctiptionText.setText("This collection must new images for last time");
                break;
            case "old":
                desctiptionText.setText("This collection must old images, this real true");
                break;
            default:
                desctiptionText.setText("Make days is funny with own coloring");
        }
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY,
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS,
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS,
                MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS
        };
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + "=?";
        String[] selectionArgs = {nameCategory};
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgs, null);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexNewStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexHaveAds = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS);
        int columnIndexPremiumStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS);
        if (cursor.moveToFirst()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexUrl));
                cacheImageModel.setKey(cursor.getInt(columnIndexKey));
                cacheImageModel.setName(cursor.getString(columnIndexName));
                cacheImageModel.setNewStatus(cursor.getInt(columnIndexNewStatus));
                cacheImageModel.setCategory(cursor.getString(columnIndexCategory));
                cacheImageModel.setHaveAds(cursor.getInt(columnIndexHaveAds));
                cacheImageModel.setPremiumStatus(cursor.getInt(columnIndexPremiumStatus));
                imageList.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        textCountImages.setText(String.valueOf(cursor.getCount()));
        recyclerViewAdapter = new RecyclerViewAdapter(imageList,
                this);
        recyclerView.setAdapter(recyclerViewAdapter);
        int position = getIntent().getIntExtra("position", 0);
        recyclerViewAdapter.notifyItemChanged(position);

    }
}