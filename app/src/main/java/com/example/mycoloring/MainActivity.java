package com.example.mycoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.text.style.TtsSpan;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayoutCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        viewPager2 = findViewById(R.id.pager);
        tabLayoutCategory = findViewById(R.id.tabLayoutCategory);
        ArrayList<SlideModel> images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.horizontal_image_1, null));
        images.add(new SlideModel(R.drawable.horizontal_image_2, null));
        images.add(new SlideModel(R.drawable.horizontal_image_3, null));
        imageSlider.setImageList(images);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPagerAdapter.addFragment(new Category1(), "CATEGOTY1");
        viewPagerAdapter.addFragment(new Category2(), "CATEGOTY2");
        viewPagerAdapter.addFragment(new Fragment_three(), "FR THREE");
        viewPager2.setAdapter(viewPagerAdapter);

        tabLayoutCategory.setTabMode(tabLayoutCategory.MODE_SCROLLABLE);

        new TabLayoutMediator(tabLayoutCategory, viewPager2,
                (tab, position) -> tab.setText(viewPagerAdapter.getTitle(position))
        ).attach();


    }
}
