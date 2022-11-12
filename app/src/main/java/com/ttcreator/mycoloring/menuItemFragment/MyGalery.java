package com.ttcreator.mycoloring.menuItemFragment;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.adapters.ViewPagerAdapter;
import com.ttcreator.mycoloring.categoryFragment.OldCategory;
import com.ttcreator.mycoloring.categoryFragment.NewCategory;
import com.ttcreator.mycoloring.categoryFragment.BestCategory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class MyGalery extends Fragment {

    private ViewPager2 viewPager2;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayoutCategory;
    private View root;
    private CardView cardView;
    private ImageSlider imageSlider;

    public MyGalery() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_my_galery, container, false);

        imageSlider = root.findViewById(R.id.imageSlider);
        cardView = root.findViewById(R.id.cardView);
        tabLayoutCategory = root.findViewById(R.id.tabLayoutCategory);
        viewPager2 = root.findViewById(R.id.pager);
        ArrayList<SlideModel> images = new ArrayList<>();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        layoutParams.width = MyApp.getScreenWidth(getContext());
        layoutParams.height = MyApp.getScreenHeight(getContext()) / 3;
        cardView.setLayoutParams(layoutParams);

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