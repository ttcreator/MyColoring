package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.slider.Slider;
import com.ttcreator.mycoloring.ColoringActivity;
import com.ttcreator.mycoloring.R;
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
    int position;

    public MyDrawing() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_my_drawing, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewMyDrawing);
        text = root.findViewById(R.id.text);
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
                MCDataContract.NewImages.MC_NEW_IMAGE_STATE,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY
        };
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection,
                selection, null, null);
        int columnIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        if (cursor.moveToNext()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexState));
                cacheImageModel.setName(cursor.getString(columnIndexName));
                cacheImageModel.setCategory(cursor.getString(columnIndexCategory));
                cacheImageModel.setKey(cursor.getInt(columnIndexKey));
                cacheImagesByState.add(cacheImageModel);
            } while (cursor.moveToNext());
        } else {
            text.setText("You have not image");
        }
        return cacheImagesByState;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyItemChanged(position);
    }
}