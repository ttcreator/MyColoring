package com.example.mycoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mycoloring.menuItemFragment.MyDrawing;
import com.example.mycoloring.menuItemFragment.MyGalery;
import com.example.mycoloring.menuItemFragment.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    MyGalery myGalery;
    MyDrawing myDrawingFragment;
    Search searchFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.myGalery);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myGalery:
                myGalery = new MyGalery();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myGalery).commit();
                return true;

            case R.id.search:
                searchFragment = new Search();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, searchFragment).commit();
                return true;

            case R.id.myDrawing:
                myDrawingFragment = new MyDrawing();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, myDrawingFragment).commit();
                return true;
        }
        return false;
    }
}
