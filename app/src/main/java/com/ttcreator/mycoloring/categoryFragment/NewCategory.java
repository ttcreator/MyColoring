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
import com.ttcreator.mycoloring.model.CardItemModel;

import java.util.List;

public class NewCategory extends Fragment {

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

    public static final String[] category = {"new"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_old_category, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewOld);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dbHelper = new MCDBHelper(getContext());
        cacheImageModels = dbHelper.getCacheImageByCategory(category, getContext());
        recyclerViewAdapter = new RecyclerViewAdapter(cacheImageModels, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;
    }

}
