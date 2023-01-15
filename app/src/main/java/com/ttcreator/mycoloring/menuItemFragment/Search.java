package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttcreator.mycoloring.AllFromCategory;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.adapters.SearchRecyclerAdapter;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CategoryItemModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Search extends Fragment {

    private RecyclerView recyclerViewFirst, recyclerViewSecond, recyclerViewThird, recyclerViewFour,
            recyclerViewFive, recyclerViewSix;
    private ImageView iconViewSeeMoreFirst, iconViewSeeMoreSecond, iconViewSeeMoreThird, iconViewSeeMoreFour,
            iconViewSeeMoreFive, iconViewSeeMoreSix;
    private TextView textViewNameCategoryFirst, textViewNameCategorySecond, textViewNameCategoryThird,
            textViewNameCategoryFour, textViewNameCategoryFive, textViewNameCategorySix;
    private TextView textCountImagesThird, textCountImagesSecond, textCountImagesFirst, textCountImagesFour,
            textCountImagesFive, textCountImagesSix;
    private ContentResolver contentResolver;
    private Cursor cursor;
    private SearchRecyclerAdapter recyclerViewAdapter;

    DatabaseReference dbRef;

    public Search() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        initViews(root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewThird.setHasFixedSize(true);
        dbRef = FirebaseDatabase.getInstance().getReference();

        ArrayList<String> categoryList = new ArrayList<>();
        contentResolver = getContext().getContentResolver();
        String[] projection1 = {MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY};
        Cursor cursor1 = contentResolver.query(MCDataContract.CONTENT_URI, projection1,
                null, null, null);
        int columnIndexCategory1 = cursor1.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        if (cursor1.moveToNext())
            do {
                String currentCategory = cursor1.getString(columnIndexCategory1);
                if (!categoryList.contains(currentCategory)) {
                    categoryList.add(cursor1.getString(columnIndexCategory1));
                }
            } while (cursor1.moveToNext());

        getParentFragmentManager().setFragmentResultListener("resultPurchasedToFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("isPurchaseOK");
                if (result.equals("OK")) {
                    int position = getActivity().getIntent().getIntExtra("position", 0);
                    recyclerViewAdapter.notifyItemChanged(position);
                }
            }
        });

        String categoryFirst = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreFirst, categoryFirst);
        textViewNameCategoryFirst.setText(categoryFirst);
        List <CategoryItemModel> itemListCategoryFirst = getDataFromDB(categoryFirst, textCountImagesFirst);
        textCountImagesFirst.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewFirst, categoryFirst, itemListCategoryFirst, 1);

        String categorySecond = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreSecond, categorySecond);
        textViewNameCategorySecond.setText(categorySecond);
        List <CategoryItemModel> itemListCategorySecond = getDataFromDB(categorySecond, textCountImagesSecond);
        textCountImagesSecond.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewSecond, categorySecond, itemListCategorySecond, 1);

        String categoryThird = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreThird, categoryThird);
        textViewNameCategoryThird.setText(categoryThird);
        List <CategoryItemModel> itemListCategoryThird = getDataFromDB(categoryThird, textCountImagesThird);
        textCountImagesThird.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewThird, categoryThird, itemListCategoryThird, 2);

        String categoryFour = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreFour, categoryFour);
        textViewNameCategoryFour.setText(categoryFour);
        List <CategoryItemModel> itemListCategoryFour = getDataFromDB(categoryFour, textCountImagesFour);
        textCountImagesFour.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewFour, categoryFour, itemListCategoryFour, 1);

        String categoryFive = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreFive, categoryFive);
        textViewNameCategoryFive.setText(categoryFive);
        List <CategoryItemModel> itemListCategoryFive = getDataFromDB(categoryFive, textCountImagesFive);
        textCountImagesFive.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewFive, categoryFive, itemListCategoryFive, 2);

        String categorySix = getNameCategoryRandom(categoryList);
        setOnClickListenerToSeeMore(iconViewSeeMoreSix, categorySix);
        textViewNameCategorySix.setText(categorySix);
        List <CategoryItemModel> itemListCategorySix = getDataFromDB(categorySix, textCountImagesSix);
        textCountImagesSix.setText(String.valueOf(cursor.getCount()));
        setDataToRecyclerView(recyclerViewSix, categorySix, itemListCategorySix, 1);
    }

    private View initViews(View root) {

        iconViewSeeMoreFirst = root.findViewById(R.id.iconViewSeeMoreFirst);
        textViewNameCategoryFirst = root.findViewById(R.id.textViewNameCategoryFirst);
        recyclerViewFirst = root.findViewById(R.id.recyclerViewFirst);
        textCountImagesFirst = root.findViewById(R.id.textCountImagesFirst);

        iconViewSeeMoreSecond = root.findViewById(R.id.iconViewSeeMoreSecond);
        textViewNameCategorySecond = root.findViewById(R.id.textViewNameCategorySecond);
        recyclerViewSecond = root.findViewById(R.id.recyclerViewSecond);
        textCountImagesSecond = root.findViewById(R.id.textCountImagesSecond);

        iconViewSeeMoreThird = root.findViewById(R.id.iconViewSeeMoreThird);
        textViewNameCategoryThird = root.findViewById(R.id.textViewNameCategoryThird);
        recyclerViewThird = root.findViewById(R.id.recyclerViewThird);
        textCountImagesThird = root.findViewById(R.id.textCountImagesThird);

        iconViewSeeMoreFour = root.findViewById(R.id.iconViewSeeMoreFour);
        textViewNameCategoryFour= root.findViewById(R.id.textViewNameCategoryFour);
        recyclerViewFour = root.findViewById(R.id.recyclerViewFour);
        textCountImagesFour = root.findViewById(R.id.textCountImagesFour);

        iconViewSeeMoreFive = root.findViewById(R.id.iconViewSeeMoreFive);
        textViewNameCategoryFive= root.findViewById(R.id.textViewNameCategoryFive);
        recyclerViewFive = root.findViewById(R.id.recyclerViewFive);
        textCountImagesFive = root.findViewById(R.id.textCountImagesFive);

        iconViewSeeMoreSix = root.findViewById(R.id.iconViewSeeMoreSix);
        textViewNameCategorySix= root.findViewById(R.id.textViewNameCategorySix);
        recyclerViewSix = root.findViewById(R.id.recyclerViewSix);
        textCountImagesSix = root.findViewById(R.id.textCountImagesSix);

        return root;
    }


    private String getNameCategoryRandom(ArrayList<String> categoryList) {
        Random randomizer;
        randomizer = new Random();
        int intexCategoryFirstItem = randomizer.nextInt(categoryList.size());
        String categoryFirst = categoryList.get(intexCategoryFirstItem);
        categoryList.remove(intexCategoryFirstItem);
        return categoryFirst;
    }

    private void setDataToRecyclerView(RecyclerView recyclerView, String categoryFirst,
                                       List <CategoryItemModel> categoryList, int spanCount) {
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new SearchRecyclerAdapter(categoryList,
                getContext(), categoryFirst);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount,
                GridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setOnClickListenerToSeeMore(ImageView iconViewSeeMore, String category) {
        iconViewSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AllFromCategory.class);
                intent.putExtra("nameCat", category);
                intent.putExtra("title", category);
                v.getContext().startActivity(intent);
            }
        });
    }

    private List<CategoryItemModel> getDataFromDB(String category, TextView textCountImages) {
        List<CategoryItemModel> itemCategoryList = new ArrayList<>();
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS,
                MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS,
                MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS,
                MCDataContract.NewImages._ID
        };
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY + "=?";
        String[] selectionArgsNew = {category};
        cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection, selection,
                selectionArgsNew, null);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexHaveAds = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_ADS);
        int columnIndexNewStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NEW_STATUS);
        int columnIndexPremiumStatus = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_PREMIUM_STATUS);
        int columnIndexId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
        if (cursor.moveToNext())
            do {
                CategoryItemModel categoryItemModel = new CategoryItemModel();
                categoryItemModel.setImgUrl(cursor.getString(columnIndexUrl));
                categoryItemModel.setName(cursor.getString(columnIndexName));
                categoryItemModel.setimgKey(cursor.getInt(columnIndexKey));
                categoryItemModel.setCategory(cursor.getString(columnIndexCategory));
                categoryItemModel.setHaveAds(cursor.getInt(columnIndexHaveAds));
                categoryItemModel.setId(cursor.getInt(columnIndexId));
                categoryItemModel.setNewStatus(cursor.getInt(columnIndexNewStatus));
                categoryItemModel.setPremiumStatus(cursor.getInt(columnIndexPremiumStatus));
                itemCategoryList.add(categoryItemModel);
            } while (cursor.moveToNext());
        cursor.close();
        return itemCategoryList;
    }
}