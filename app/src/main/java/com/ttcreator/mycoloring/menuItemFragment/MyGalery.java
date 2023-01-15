package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.ttcreator.mycoloring.AllFromCategory;
import com.ttcreator.mycoloring.BaseFragmentCategory;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.ArrayList;
import java.util.List;


public class MyGalery extends Fragment {

    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayoutCategory;
    private View root;
    private CardView cardView;
    private ImageSlider imageSlider;
    private MCDBHelper dbHelper;
    private List<CacheImageModel> cacheImageModels;


    public MyGalery() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_my_galery, container, false);

        imageSlider = root.findViewById(R.id.imageSlider);
        cardView = root.findViewById(R.id.parentItem);
        tabLayoutCategory = root.findViewById(R.id.tabLayoutCategory);
        viewPager2 = root.findViewById(R.id.pager);
        ArrayList<SlideModel> images = new ArrayList<>();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        layoutParams.width = MyApp.getScreenWidth(getContext());
        layoutParams.height = MyApp.getScreenHeight(getContext()) / 3;
        cardView.setLayoutParams(layoutParams);
        dbHelper = new MCDBHelper(getContext());

        images.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/mycoloring-4e5f8.appspot.com/o/images%2FEnchant%2Fenchant_main.png?alt=media&token=7437f99e-7cf4-41c9-b223-25130096835c",
                "Enchant", null));
        images.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/mycoloring-4e5f8.appspot.com/o/images%2FCats%2Fhome_cats.png?alt=media&token=178648bb-8131-4819-b6bf-db92a782b321",
                "Cats", null));
        images.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/mycoloring-4e5f8.appspot.com/o/images%2FEaster%2Fmain_easter.png?alt=media&token=bbc8b58f-7306-4187-964b-34332d0b61fa",
                "Easter", null));
        imageSlider.setImageList(images, ScaleTypes.FIT);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                Intent intent = new Intent(getContext(), AllFromCategory.class);
                switch (images.get(i).getTitle().toString()) {
                    case "Enchant":
                        intent.putExtra("nameCat", "enchant");
                        startActivity(intent);
                        break;
                    case "Cats":
                        intent.putExtra("nameCat", "cats");
                        startActivity(intent);
                        break;
                    case "Easter":
                        intent.putExtra("nameCat", "easter");
                        startActivity(intent);
                        break;

                }

            }
        });

        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(), getLifecycle());

        String[] projection = {MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY};
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI,
                projection,  null, null, null);
        List <String> allCategoty = new ArrayList<>();
        int categoryColumnIndex = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        while (cursor.moveToNext() ) {
            String currentCategory = cursor.getString(categoryColumnIndex);
            if (!allCategoty.contains(currentCategory)) {
                allCategoty.add(currentCategory);
                String [] currentCategortArray = {currentCategory};
                cacheImageModels = dbHelper.getCacheImageByCategory(currentCategortArray, getContext());
                ArrayList<CacheImageModel> cacheImageModelsArrayList = new ArrayList<>();
                cacheImageModelsArrayList.addAll(cacheImageModels);
                viewPagerAdapter.addFragment(BaseFragmentCategory.newInstance(cacheImageModelsArrayList, currentCategory), currentCategory);
                viewPager2.setAdapter(viewPagerAdapter);
                tabLayoutCategory.setTabMode(tabLayoutCategory.MODE_SCROLLABLE);

                new TabLayoutMediator(tabLayoutCategory, viewPager2,
                        (tab, position) -> tab.setText(viewPagerAdapter.getTitle(position))
                ).attach();
                viewPagerAdapter.notifyDataSetChanged();
            }
        }
        cursor.close();
        return root;
    }



}