package com.ttcreator.mycoloring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.data.GetDataFromFirebase;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.menuItemFragment.MyDrawing;
import com.ttcreator.mycoloring.menuItemFragment.MyGalery;
import com.ttcreator.mycoloring.menuItemFragment.Search;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private MyGalery myGalery;
    private MyDrawing myDrawingFragment;
    private Search searchFragment;
    private MCDBHelper mcdbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottom_menu);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.myGalery);

        mcdbHelper = new MCDBHelper(this);
        mcdbHelper.getAllRows(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
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
