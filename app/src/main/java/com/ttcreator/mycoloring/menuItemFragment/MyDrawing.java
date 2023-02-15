package com.ttcreator.mycoloring.menuItemFragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.SettingsColoringActivity;
import com.ttcreator.mycoloring.adapters.MyDrawingListAdapter;
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
    private Button settingsButton;
    private AppCompatImageView imageViewNoPictures;

    public MyDrawing() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.fragment_my_drawing, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewMyDrawing);
        imageViewNoPictures = root.findViewById(R.id.imageViewNoPictires);
        settingsButton = root.findViewById(R.id.settingsButton);
        position = getActivity().getIntent().getIntExtra("position", 0);
        List<CacheImageModel> cacheImageModels = getCacheImageByState();
        settingsButton.setText("Settings");
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSettings = new Intent(getContext(), SettingsColoringActivity.class);
                startActivity(openSettings);
            }
        });
        if (!cacheImageModels.isEmpty()) {
            recyclerViewAdapter = new MyDrawingListAdapter(cacheImageModels, getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyItemChanged(position);
        } else {
            SpannableString ss = new SpannableString("Start coloring here");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Fragment fragment = new Search();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };
            ss.setSpan(clickableSpan, 15, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView textView = (TextView) root.findViewById(R.id.text);
            textView.setText(ss);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
            imageViewNoPictures.setBackgroundResource(R.drawable.icon_pictures_light_grey100);
        }
        return root;
    }

    private List<CacheImageModel> getCacheImageByState() {
        List<CacheImageModel> cacheImagesByState = new ArrayList<>();
        ContentResolver contentResolver = requireContext().getContentResolver();
        String selection = MCDataContract.NewImages.MC_NEW_IMAGE_STATE + " IS NOT NULL";
        String[] projection = {
                MCDataContract.NewImages.MC_NEW_IMAGE_NAME,
                MCDataContract.NewImages.MC_NEW_IMAGE_URL,
                MCDataContract.NewImages.MC_NEW_IMAGE_STATE,
                MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY,
                MCDataContract.NewImages.MC_NEW_IMAGE_KEY,
                MCDataContract.NewImages._ID
        };
        Cursor cursor = contentResolver.query(MCDataContract.CONTENT_URI, projection,
                selection, null, null);
        int columnIndexState = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_STATE);
        int columnIndexUrl = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_URL);
        int columnIndexName = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_NAME);
        int columnIndexCategory = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_CATEGORY);
        int columnIndexKey = cursor.getColumnIndex(MCDataContract.NewImages.MC_NEW_IMAGE_KEY);
        int columnIndexId = cursor.getColumnIndex(MCDataContract.NewImages._ID);
        if (cursor.moveToNext()) {
            do {
                CacheImageModel cacheImageModel = new CacheImageModel();
                cacheImageModel.setState(cursor.getString(columnIndexState));
                cacheImageModel.setImageCacheUrl(cursor.getString(columnIndexUrl));
                cacheImageModel.setName(cursor.getString(columnIndexName));
                cacheImageModel.setCategory(cursor.getString(columnIndexCategory));
                cacheImageModel.setKey(cursor.getInt(columnIndexKey));
                cacheImageModel.setId(cursor.getInt(columnIndexId));
                cacheImagesByState.add(cacheImageModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cacheImagesByState;
    }


    @Override
    public void onResume() {
        super.onResume();
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