package com.ttcreator.mycoloring.categoryFragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.adapters.RecyclerViewAdapter;
import com.ttcreator.mycoloring.data.GetDataFromFirebase;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.List;

public class BestCategory extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private MCDBHelper dbHelper;
    private View root;
    private String imageUrl;
    private String name;
    private String imageCategory;
    private String status;
    private String keyImage;
    private List <CacheImageModel> cacheImageModels;

    public static final String[] category = {"best"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_old_category, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewOld);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dbHelper = new MCDBHelper(getContext());
        dbHelper = getDataFromFirebaseToSqlite("images");
        cacheImageModels = dbHelper.getCacheImageByCategory(category);
        recyclerViewAdapter = new RecyclerViewAdapter(cacheImageModels, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }

    public MCDBHelper getDataFromFirebaseToSqlite(String childName) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Query query = dbRef.child(childName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cursor cursorAllItem = getContext().getContentResolver().query(MCDataContract.CONTENT_URI,
                        null, null, null, null, null);
                if (cursorAllItem.getCount() == 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        imageUrl = dataSnapshot.child("imageUrl").getValue().toString().trim();
                        name = dataSnapshot.child("name").getValue().toString().trim();
                        imageCategory = dataSnapshot.child("category").getValue().toString().trim();
                        status = dataSnapshot.child("status").getValue().toString().trim();
                        keyImage = dataSnapshot.getKey();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_URL, imageUrl);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NAME, name);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY, imageCategory);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATUS, status);
                        contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_KEY, keyImage);
                        ContentResolver contentResolver = getContext().getContentResolver();
                        Uri uri = contentResolver.insert(MCDataContract.CONTENT_URI, contentValues);
                        if (uri == null) {
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Insertion of data in the table failed for " + uri, Toast.LENGTH_LONG);
                        } else {
                            dbHelper = getDataFromFirebaseToSqlite("images");
                            cacheImageModels = dbHelper.getCacheImageByCategory(category);
                            recyclerViewAdapter = new RecyclerViewAdapter(cacheImageModels, getContext());
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext().getApplicationContext(),
                                    "Data saved" + uri, Toast.LENGTH_LONG);
                        }
                    }
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        imageUrl = dataSnapshot.child("imageUrl").getValue().toString().trim();
                        name = dataSnapshot.child("name").getValue().toString().trim();
                        imageCategory = dataSnapshot.child("category").getValue().toString().trim();
                        status = dataSnapshot.child("status").getValue().toString().trim();
                        keyImage = dataSnapshot.getKey();
                        String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_URL};
                        Cursor cursor = getContext().getContentResolver().query(
                                MCDataContract.CONTENT_URI, projection, null, null,
                                null);
                        int imgUrlColumnIndex = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
                        int countDuplicate = 0;
                        while (cursor.moveToNext() && countDuplicate < 1) {
                            String currentUrl = cursor.getString(imgUrlColumnIndex);
                            if (currentUrl.equals(imageUrl)) {
                                countDuplicate++;
                                Log.d("itemExist", "item " + name + " already exist");
                            }
                        }
                        if (countDuplicate == 0) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_URL, imageUrl);
                            contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_NAME, name);
                            contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY, imageCategory);
                            contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_STATUS, status);
                            contentValues.put(MCDataContract.NewImages.MC_NEW_IMAGE_KEY, keyImage);
                            ContentResolver contentResolver = getContext().getContentResolver();
                            Uri uri = contentResolver.insert(MCDataContract.CONTENT_URI, contentValues);
                            if (uri == null) {
                                Toast.makeText(getContext().getApplicationContext(),
                                        "Insertion of data in the table failed for " + uri, Toast.LENGTH_LONG);
                            } else {
//                                dbHelper = getDataFromFirebaseToSqlite("images");
//                                cacheImageModels = dbHelper.getCacheImageByCategory(category, keyImage);
//                                recyclerViewAdapter = new RecyclerViewAdapter(cacheImageModels, getContext());
//                                recyclerView.setAdapter(recyclerViewAdapter);
//                                recyclerViewAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext().getApplicationContext(),
                                        "Data saved" + uri, Toast.LENGTH_LONG);
                            }

                        } else {
                            Log.d("allDuplicates", "All item now have in sqlite");
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        }); return dbHelper;


    }

}
