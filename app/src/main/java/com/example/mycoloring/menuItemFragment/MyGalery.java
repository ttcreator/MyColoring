package com.example.mycoloring.menuItemFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mycoloring.R;
import com.example.mycoloring.ViewPagerAdapter;
import com.example.mycoloring.categoryFragment.OldCategory;
import com.example.mycoloring.categoryFragment.NewCategory;
import com.example.mycoloring.categoryFragment.BestCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class MyGalery extends Fragment {

    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayoutCategory;
    View root;

    public MyGalery() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_my_galery, container, false);

        ImageSlider imageSlider = root.findViewById(R.id.imageSlider);
        viewPager2 = root.findViewById(R.id.pager);
        tabLayoutCategory = root.findViewById(R.id.tabLayoutCategory);
        ArrayList<SlideModel> images = new ArrayList<>();

        images.add(new SlideModel(R.drawable.horizontal_image_1, null));
        images.add(new SlideModel(R.drawable.horizontal_image_2, null));
        images.add(new SlideModel(R.drawable.horizontal_image_3, null));
        imageSlider.setImageList(images);

        viewPagerAdapter = new ViewPagerAdapter(getParentFragmentManager(), getLifecycle());
        viewPagerAdapter.addFragment(new OldCategory(), "Old");
        viewPagerAdapter.addFragment(new NewCategory(), "New");
        viewPagerAdapter.addFragment(new BestCategory(), "Best");
        viewPager2.setAdapter(viewPagerAdapter);

        tabLayoutCategory.setTabMode(tabLayoutCategory.MODE_SCROLLABLE);

        new TabLayoutMediator(tabLayoutCategory, viewPager2,
                (tab, position) -> tab.setText(viewPagerAdapter.getTitle(position))
        ).attach();

        return root;
    }

}