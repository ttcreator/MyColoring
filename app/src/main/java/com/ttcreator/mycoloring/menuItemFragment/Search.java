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

        recyclerViewNew.setHasFixedSize(true);
        dbRef = FirebaseDatabase.getInstance().getReference();

        itemListCategoryBest = new ArrayList<>();
        itemListCategoryOld = new ArrayList<>();
        itemListCategoryNew = new ArrayList<>();
        getDataFromDB();
        return root;
    }

    private void getDataFromDB() {

        textViewSeeMoreBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", BestCategory.category[0]);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryBest.setText(BestCategory.category[0]);
        ContentResolver contentResolver = getContext().getContentResolver();
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY};
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + "=?";
        String[] selectionArgsBest = {BestCategory.category[0]};
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgsBest, null);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        if (cursor.moveToNext())
            do {
                CategoryItemModel categoryItemModel = new CategoryItemModel();
                categoryItemModel.setImgUrl(cursor.getString(columnIndexUrl));
                categoryItemModel.setName(cursor.getString(columnIndexName));
                categoryItemModel.setimgKey(cursor.getInt(columnIndexKey));
                categoryItemModel.setCategory(cursor.getString(columnIndexCategory));
                itemListCategoryBest.add(categoryItemModel);
            } while (cursor.moveToNext());
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryBest,
                getContext(), BestCategory.category[0]);
        recyclerViewBest.setAdapter(recyclerViewAdapter);
        recyclerViewBest.setLayoutManager(new StaggeredGridLayoutManager(2,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewBest.setHasFixedSize(true);

        textViewSeeMoreOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", OldCategory.category[0]);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryOld.setText(OldCategory.category[0]);
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
                itemListCategoryOld.add(categoryItemModel);
            } while (cursor.moveToNext());
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryOld,
                getContext(), OldCategory.category[0]);
        recyclerViewOld.setAdapter(recyclerViewAdapter);
        recyclerViewOld.setLayoutManager(new StaggeredGridLayoutManager(1,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewOld.setHasFixedSize(true);

        textViewSeeMoreNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", NewCategory.category[0]);
                v.getContext().startActivity(intent);
            }
        });
        textViewNameCategoryNew.setText(NewCategory.category[0]);
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
                itemListCategoryNew.add(categoryItemModel);
            } while (cursor.moveToNext());
        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategoryNew,
                getContext(), NewCategory.category[0]);
        recyclerViewNew.setAdapter(recyclerViewAdapter);
        recyclerViewNew.setLayoutManager(new StaggeredGridLayoutManager(1,
                GridLayoutManager.HORIZONTAL));
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerViewNew.setHasFixedSize(true);

//        Query query = dbRef.child("images");
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    CategoryItemModel categoryItemModel = new CategoryItemModel();
//                    String nameCategory = dataSnapshot.child("category").getValue().toString();
//                    if (nameCategory.equals("best")) {
//                        textViewSeeMore.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
//                                intent.putExtra("nameCat", nameCategory);
//                                v.getContext().startActivity(intent);
//                            }
//                        });
//                        textViewNameCategory.setText(dataSnapshot.child("category")
//                                .getValue().toString());
//                        categoryItemModel.setImgUrl(dataSnapshot.child("imageUrl")
//                                .getValue().toString());
//                        categoryItemModel.setName(dataSnapshot.child("name")
//                                .getValue().toString());
//                        categoryItemModel.setCategory(dataSnapshot.child("category")
//                                .getValue().toString());
//                        itemListCategory.add(categoryItemModel);
//                        recyclerViewAdapter = new SearchRecyclerAdapter(itemListCategory,
//                                getContext(), "best");
//                        recyclerView.setAdapter(recyclerViewAdapter);
//                        recyclerViewAdapter.notifyDataSetChanged();
////                    } else if (dataSnapshot.child("category").getValue().toString().equals("old")) {
////                        textViewSeeMoreOld.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
////                                intent.putExtra("nameCat", nameCategory);
////                                v.getContext().startActivity(intent);
////                            }
////                        });
////                        textViewNameCategoryOld.setText(dataSnapshot.child("category")
////                                .getValue().toString());
////                        categoryItemModel.setImgUrl(dataSnapshot.child("imageUrl")
////                                .getValue().toString());
////                        categoryItemModel.setName(dataSnapshot.child("name")
////                                .getValue().toString());
////                        categoryItemModel.setCategory(dataSnapshot.child("category")
////                                .getValue().toString());
////                        itemListCategoryOld.add(categoryItemModel);
////                        recyclerViewAdapterSearch = new SearchRecyclerAdapter(itemListCategoryOld,
////                                getContext(), "old");
////                        recyclerViewOld.setAdapter(recyclerViewAdapterSearch);
////                        recyclerViewAdapterSearch.notifyDataSetChanged();
////                    } else if (dataSnapshot.child("category").getValue().toString().equals("new")) {
////                        textViewSeeMoreNew.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View v) {
////                                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
////                                intent.putExtra("nameCat", nameCategory);
////                                v.getContext().startActivity(intent);
////                            }
////                        });
////                        textViewNameCategoryNew.setText(dataSnapshot.child("category")
////                                .getValue().toString());
////                        categoryItemModel.setImgUrl(dataSnapshot.child("imageUrl")
////                                .getValue().toString());
////                        categoryItemModel.setName(dataSnapshot.child("name")
////                                .getValue().toString());
////                        categoryItemModel.setCategory(dataSnapshot.child("category")
////                                .getValue().toString());
////                        itemListCategoryNew.add(categoryItemModel);
////                        recyclerViewAdapterSearch = new SearchRecyclerAdapter(itemListCategoryNew,
////                                getContext(), "new");
////                        recyclerViewNew.setAdapter(recyclerViewAdapterSearch);
////                        recyclerViewAdapterSearch.notifyDataSetChanged();
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

}