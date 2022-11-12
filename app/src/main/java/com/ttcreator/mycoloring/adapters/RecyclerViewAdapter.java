package com.ttcreator.mycoloring.adapters;

import android.animation.ValueAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ttcreator.mycoloring.ColoringActivity;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<CacheImageModel> cacheImageModels;
    private Context context;
    private String imgUrl;
    private int imageKey, new_status, haveAds;
    private String category;
    private String nameImage;
    private ProgressBar progressBarItem;
    private int lastPosition = -1;

    public RecyclerViewAdapter(List<CacheImageModel> cacheImageModels, Context context) {
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
        nameImage = cacheImageModels.get(position).getName();
        category = cacheImageModels.get(position).getCategory();
        imageKey = cacheImageModels.get(position).getImageKey();
        new_status = cacheImageModels.get(position).getNewStatus();
        haveAds = cacheImageModels.get(position).getHaveAds();
        if (new_status != 0) {
            holder.iconNew.setImageResource(R.drawable.icons_new);
            holder.iconNew.setVisibility(View.VISIBLE);
        } else {
            holder.iconNew.setVisibility(View.GONE);
        }
        holder.progressBarItem.setVisibility(View.VISIBLE);
        holder.relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(MyApp.getScreenWidth(context) / 2,
                MyApp.getScreenWidth(context) / 2));
        Glide.with(context)
                .load(imgUrl)
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

        if (haveAds != 0 ) {
            setAnimation(holder.itemView, position);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posit = holder.getAdapterPosition();
                if (posit < 0 || posit >= cacheImageModels.size()) {
                    Log.d("POSITION", "Number postition " + posit + " its error");
                } else {
                    String urlImagePosition = cacheImageModels.get(posit).getImageCacheUrl();
                    String namePosition = cacheImageModels.get(posit).getName();
                    String categoryPosition = cacheImageModels.get(posit).getCategory();
                    int imageKeyPosition = cacheImageModels.get(posit).getImageKey();
                    Uri uri = ContentUris.withAppendedId(MCDataContract.CONTENT_URI, imageKeyPosition);
                    Intent intent = new Intent(v.getContext(),
                            ColoringActivity.class);
                    intent.putExtra("urlImagePosition", urlImagePosition);
                    intent.putExtra("nameImage", namePosition);
                    intent.putExtra("keyPosition", imageKeyPosition);
                    intent.putExtra("categoryPosition", categoryPosition);
                    intent.setData(uri);
                    v.getContext().startActivity(intent);

                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return cacheImageModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ProgressBar progressBarItem;
        private RelativeLayout relativeLayout;
        private CardView cardView;
        private ImageView iconNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
            progressBarItem = itemView.findViewById(R.id.progressBarItem);
            relativeLayout = itemView.findViewById(R.id.parentItem);
            cardView = itemView.findViewById(R.id.cardView);
            iconNew = itemView.findViewById(R.id.iconRecycler);
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {

            Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake_animation);
                    viewToAnimate.startAnimation(shake);
                    handler.postDelayed(this, 6000);
                }
            };
            handler.postDelayed(r, 5000);
            lastPosition = position;
        }
    }
}
