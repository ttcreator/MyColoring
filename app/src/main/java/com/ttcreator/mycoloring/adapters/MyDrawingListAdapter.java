package com.ttcreator.mycoloring.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ttcreator.mycoloring.MenuDialogFragment;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.List;

public class MyDrawingListAdapter extends RecyclerView.Adapter<MyDrawingListAdapter.ViewHolder> {

    private List<CacheImageModel> cacheImageModels;
    private Context context;
    private String imgUrl, stateUrl;
    private int imageKey;
    private String category;
    private String nameImage;
    private ProgressBar progressBarItem;

    public MyDrawingListAdapter(List<CacheImageModel> cacheImageModels, Context context) {
        this.cacheImageModels = cacheImageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        imgUrl = cacheImageModels.get(position).getImageCacheUrl();
        stateUrl = cacheImageModels.get(position).getState();
        nameImage = cacheImageModels.get(position).getName();
        category = cacheImageModels.get(position).getCategory();
        imageKey = cacheImageModels.get(position).getImageKey();
        holder.progressBarItem.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(((MyApp.getScreenWidth(context)) / 2) - 40,
                (MyApp.getScreenWidth(context) / 2) - 40);
        layoutParamsFrame.setMargins(20, 10, 5, 10);
        holder.cardView.setLayoutParams(layoutParamsFrame);
        Glide.with(context)
                .load(stateUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBarItem.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posit = holder.getAdapterPosition();
                if (posit < 0 || posit >= cacheImageModels.size()) {
                    Log.d("POSITION", "Number postition " + posit + " its error");
                } else {
                    String urlImagePosition = cacheImageModels.get(posit).getImageCacheUrl();
                    String stateImagePosition = cacheImageModels.get(posit).getState();
                    String namePosition = cacheImageModels.get(posit).getName();
                    String categoryPosition = cacheImageModels.get(posit).getCategory();
                    int imageKeyPosition = cacheImageModels.get(posit).getImageKey();
                    int id = cacheImageModels.get(posit).getId();
                    Uri uri = ContentUris.withAppendedId(MCDataContract.CONTENT_URI, id);
                    Bundle args = new Bundle();
                    args.putString("uriToSting", uri.toString());
                    args.putString("urlImagePosition", urlImagePosition);
                    args.putString("nameImage", namePosition);
                    args.putInt("keyPosition", imageKeyPosition);
                    args.putString("categoryPosition", categoryPosition);
                    args.putString("statePosition", stateImagePosition);
                    args.putInt("position", posit);
                    MenuDialogFragment menuDialogFragment = new MenuDialogFragment();
                    menuDialogFragment.setArguments(args);
                    menuDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(),
                            "MenuDialogFragment");

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cacheImageModels.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ProgressBar progressBarItem;
        private RelativeLayout relativeLayout;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
            progressBarItem = itemView.findViewById(R.id.progressBarItem);
            cardView = itemView.findViewById(R.id.cardView);
            relativeLayout = itemView.findViewById(R.id.parentItem);
        }
    }
}
