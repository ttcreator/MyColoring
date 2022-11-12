package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.FrameMetricsAggregator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.ttcreator.mycoloring.ColoringActivity;
import com.ttcreator.mycoloring.MenuDialogFragment;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.SettingsColoringActivity;
import com.ttcreator.mycoloring.adapters.MyDrawingListAdapter;
import com.ttcreator.mycoloring.adapters.RecyclerViewAdapter;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.ArrayList;
import java.util.List;

public class MyDrawing extends Fragment {

    private RecyclerView recyclerView;
    private MyDrawingListAdapter recyclerViewAdapter;
    private View root;
    private TextView text;
    private int position;
    private Button clearAllImagesButton;

    public MyDrawing() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.fragment_my_drawing, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewMyDrawing);
        text = root.findViewById(R.id.text);
        clearAllImagesButton = root.findViewById(R.id.buttonClearAllImage);
        clearAllImagesButton.setText("CLEAR ALL IMAGES");
        clearAllImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("All you images was delete, are you sure?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentResolver contentResolver = getActivity().getContentResolver();
                                String[] projection = {MCDataContract.NewImages._ID,
                                        MCDataContract.NewImages.MC_NEW_IMAGE_STATE};
                                Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI,
                                        projection, null, null,
                                        null);
                                while (cursor.moveToNext()) {
                                    int columnIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                    int columnIndexId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
                                    String valueState = cursor.getString(columnIndexState);
                                    String valueId = cursor.getString(columnIndexId);
                                    if (valueState != null) {
                                        ContentValues cv = new ContentValues();
                                        cv.putNull(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
                                        Uri rowUri = Uri.withAppendedPath(MCDataContract.CONTENT_URI, valueId);
                                        contentResolver.update(rowUri, cv, null, null);
                                        MCDBHelper mcdbHelper = new MCDBHelper(getActivity().getApplicationContext());
                                        mcdbHelper.getAllRows(MCDataContract.NewImages.MC_NEW_IMAGE_TABLE_NAME);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });
        position = getActivity().getIntent().getIntExtra("position", 0);
        List<CacheImageModel> cacheImageModels = getCacheImageByState();
        recyclerViewAdapter = new MyDrawingListAdapter(cacheImageModels, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyItemChanged(position);
        return root;
    }

    private List<CacheImageModel> getCacheImageByState() {
        List<CacheImageModel> cacheImagesByState = new ArrayList<>();
        ContentResolver contentResolver = getContext().getContentResolver();
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_STATE + " IS NOT NULL";
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_STATE,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY
        };
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection,
                selection, null, null);
        int columnIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        if (cursor.moveToNext()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setState(cursor.getString(columnIndexState));
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexUrl));
                cacheImageModel.setName(cursor.getString(columnIndexName));
                cacheImageModel.setCategory(cursor.getString(columnIndexCategory));
                cacheImageModel.setKey(cursor.getInt(columnIndexKey));
                cacheImagesByState.add(cacheImageModel);
            } while (cursor.moveToNext());
        } else {
            clearAllImagesButton.setText("You have not image");
            text.setText("Start coloring here");
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Fragment fragment = new Search();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
        return cacheImagesByState;
    }


    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyItemChanged(position);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu_setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettings = new Intent(getContext(), SettingsColoringActivity.class);
            startActivity(openSettings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}