package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ttcreator.mycoloring.AllFromCategory;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.adapters.SearchRecyclerAdapter;
import com.ttcreator.mycoloring.categoryFragment.BestCategory;
import com.ttcreator.mycoloring.categoryFragment.NewCategory;
import com.ttcreator.mycoloring.categoryFragment.OldCategory;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CategoryItemModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class Search extends Fragment {

    private RecyclerView recyclerViewBest, recyclerViewOld, recyclerViewNew;
    private TextView textViewSeeMoreBest, textViewSeeMoreOld, textViewSeeMoreNew;
    private TextView textViewNameCategoryBest, textViewNameCategoryOld, textViewNameCategoryNew;
    private TextView textCountImages3, textCountImages2, textCountImages;
    private SearchRecyclerAdapter recyclerViewAdapter;

    private List<CategoryItemModel> itemListCategoryBest,
            itemListCategoryOld, itemListCategoryNew;
    private View root;
    private DatabaseReference dbRef;

    public Search() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);

        textViewSeeMoreBest = root.findViewById(R.id.textViewSeeMore);
        textViewNameCategoryBest = root.findViewById(R.id.textViewNameCategory);
        recyclerViewBest = root.findViewById(R.id.recyclerViewBest);
        textViewSeeMoreOld = root.findViewById(R.id.textViewSeeMore2);
        textViewNameCategoryOld = root.findViewById(R.id.textViewNameCategory2);
        recyclerViewOld = root.findViewById(R.id.recyclerViewOld);
        textViewSeeMoreNew = root.findViewById(R.id.textViewSeeMore3);
        textViewNameCategoryNew = root.findViewById(R.id.textViewNameCategory3);
        recyclerViewNew = root.findViewById(R.id.recyclerViewNew);

        textCountImages3 = root.findViewById(R.id.textCountImages3);
        textCountImages2 = root.findViewById(R.id.textCountImages2);
        textCountImages = root.findViewById(R.id.textCountImages);

        recyclerViewNew.setHasFixedSize(true);
        dbRef = FirebaseDatabase.getInstance().getReference();

        itemListCategoryBest = new ArrayList<>();
        itemListCategoryOld = new ArrayList<>();
        itemListCategoryNew = new ArrayList<>();
        getDataFromDB();
        return root;
    }

    private void getDataFromDB() {

        String titleBest = "Best images";

        textViewSeeMoreBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", BestCategory.category[0]);
                intent.putExtra("title", titleBest);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryBest.setText("Best images");
        ContentResolver contentResolver = getContext().getContentResolver();
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS};
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + "=?";
        String[] selectionArgsBest = {BestCategory.category[0]};
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgsBest, null);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexHaveAds = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS);
        if (cursor.moveToNext())
            do {
                CategoryItemModel categoryItemModel = new CategoryItemModel();
                categoryItemModel.setImgUrl(cursor.getString(columnIndexUrl));
                categoryItemModel.setName(cursor.getString(columnIndexName));
                categoryItemModel.setimgKey(cursor.getInt(columnIndexKey));
                categoryItemModel.setCategory(cursor.getString(columnIndexCategory));
                categoryItemModel.setHaveAds(cursor.getInt(columnIndexHaveAds));
                itemListCategoryBest.add(categoryItemModel);
            } while (cursor.moveToNext());
        textCountImages.setText(String.valueOf(cursor.getCount()));
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryBest,
                getContext(), BestCategory.category[0]);
        recyclerViewBest.setAdapter(recyclerViewAdapter);
        recyclerViewBest.setLayoutManager(new StaggeredGridLayoutManager(2,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewBest.setHasFixedSize(true);

        String titleOld = "Old images";
        textViewSeeMoreOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", OldCategory.category[0]);
                intent.putExtra("title", titleOld);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryOld.setText(titleOld);
        String[] selectionArgsOld = {OldCategory.category[0]};
        cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgsOld, null);
        if (cursor.moveToNext())
            do {
                CategoryItemModel categoryItemModel = new CategoryItemModel();
                categoryItemModel.setImgUrl(cursor.getString(columnIndexUrl));
                categoryItemModel.setName(cursor.getString(columnIndexName));
                categoryItemModel.setimgKey(cursor.getInt(columnIndexKey));
                categoryItemModel.setCategory(cursor.getString(columnIndexCategory));
                categoryItemModel.setHaveAds(cursor.getInt(columnIndexHaveAds));
                itemListCategoryOld.add(categoryItemModel);
            } while (cursor.moveToNext());
        textCountImages2.setText(String.valueOf(cursor.getCount()));
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryOld,
                getContext(), OldCategory.category[0]);
        recyclerViewOld.setAdapter(recyclerViewAdapter);
        recyclerViewOld.setLayoutManager(new StaggeredGridLayoutManager(1,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewOld.setHasFixedSize(true);

        String titleNew = "New images";
        textViewSeeMoreNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", NewCategory.category[0]);
                intent.putExtra("title", titleNew);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryNew.setText(titleNew);
        String[] selectionArgsNew = {NewCategory.category[0]};
        cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgsNew, null);
        if (cursor.moveToNext())
            do {
                CategoryItemModel categoryItemModel = new CategoryItemModel();
                categoryItemModel.setImgUrl(cursor.getString(columnIndexUrl));
                categoryItemModel.setName(cursor.getString(columnIndexName));
                categoryItemModel.setimgKey(cursor.getInt(columnIndexKey));
                categoryItemModel.setCategory(cursor.getString(columnIndexCategory));
                categoryItemModel.setHaveAds(cursor.getInt(columnIndexHaveAds));
                itemListCategoryNew.add(categoryItemModel);
            } while (cursor.moveToNext());
        textCountImages3.setText(String.valueOf(cursor.getCount()));
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryNew,
                getContext(), NewCategory.category[0]);
        recyclerViewNew.setAdapter(recyclerViewAdapter);
        recyclerViewNew.setLayoutManager(new StaggeredGridLayoutManager(1,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewNew.setHasFixedSize(true);

    }

}