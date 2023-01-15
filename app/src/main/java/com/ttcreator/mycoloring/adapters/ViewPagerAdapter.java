package com.ttcreator.mycoloring.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabItem;
import com.ttcreator.mycoloring.BaseFragmentCategory;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    public ArrayList<String> fragmentTitle = new ArrayList<>();

    private BaseFragmentCategory baseFragmentCategory;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentArrayList.get(position);
    }

    public void addFragment (Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }


    public CharSequence getTitle (int position) {
        return fragmentTitle.get(position);
    }

}
