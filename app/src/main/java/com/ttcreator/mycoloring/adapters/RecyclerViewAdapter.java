package com.ttcreator.mycoloring.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ttcreator.mycoloring.AdsManager;
import com.ttcreator.mycoloring.AllFromCategory;
import com.ttcreator.mycoloring.ColoringActivity;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.PurchaseDialog;
import com.ttcreator.mycoloring.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ttcreator.mycoloring.SharedPreferencesFactory;
import com.ttcreator.mycoloring.SplashScreenActivity;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<CacheImageModel> cacheImageModels;
    private Context context;
    private String imgUrl;
    private int imageKey, new_status, haveAds, premiumStatus;
    private String category;
    private String nameImage;
    private ProgressBar progressBarItem;
    private int lastPosition = -1;
    private FirebaseAnalytics mFirebaseAnalytics;
    private boolean isUserHavePrem;

    public RecyclerViewAdapter(ArrayList<CacheImageModel> cacheImageModels, Context context) {
        this.cacheImageModels = cacheImageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SplashScreenActivity.adsManager.loadInterstatialAd();
        imgUrl = cacheImageModels.get(position).getImageCacheUrl();
        nameImage = cacheImageModels.get(position).getName();
        category = cacheImageModels.get(position).getCategory();
        imageKey = cacheImageModels.get(position).getImageKey();
        new_status = cacheImageModels.get(position).getNewStatus();
        haveAds = cacheImageModels.get(position).getHaveAds();
        premiumStatus = cacheImageModels.get(position).getPremiumStatus();
        isUserHavePrem = SharedPreferencesFactory.getBoolean(context, "isPurchase");
        if (new_status != 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.iconNew.setImageResource(R.drawable.icon_new_1);
            } else {
                holder.iconNew.setImageResource(R.drawable.icon_new_2);
            }
            holder.iconNew.setVisibility(View.VISIBLE);
        } else {
            holder.iconNew.setVisibility(View.GONE);
        }
        if (premiumStatus != 0) {
            if (isUserHavePrem) {
                holder.iconPremium.setImageResource(R.drawable.icons_purple_star);
                holder.iconPremium.setVisibility(View.VISIBLE);
            } else {
                holder.iconPremium.setImageResource(R.drawable.icons_red_lock);
                holder.iconPremium.setVisibility(View.VISIBLE);
            }
        } else {
            holder.iconPremium.setVisibility(View.GONE);
        }
        holder.progressBarItem.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(((MyApp.getScreenWidth(context)) / 2) - 20,
                MyApp.getScreenWidth(context) / 2);
        layoutParamsFrame.setMargins(10, 10, 5, 10);
        holder.cardView.setLayoutParams(layoutParamsFrame);
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

        if (haveAds != 0) {
            setAnimation(holder.itemView, position);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posit = holder.getAdapterPosition();
                if (posit < 0 || posit >= cacheImageModels.size()) {
                    Log.d("POSITION", "Number postition " + posit + " its error");
                } else {
                    if (cacheImageModels.get(posit).getPremiumStatus() != 1) {
                        startToColoringActivity(v, posit);
                    } else {
                        if (isUserHavePrem) {
                            startToColoringActivity(v, posit);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("If you want get this image, you need buy subscription")
                                    .setPositiveButton("Get image", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PurchaseDialog purchaseDialog = new PurchaseDialog();
                                            purchaseDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),
                                                    "PurchaseDialogFragment");
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            builder.show();
                        }
                    }

                }
            }
        });
    }

    private void startToColoringActivity(View v, int posit) {

        String urlImagePosition = cacheImageModels.get(posit).getImageCacheUrl();
        String namePosition = cacheImageModels.get(posit).getName();
        String categoryPosition = cacheImageModels.get(posit).getCategory();
        int imageKeyPosition = cacheImageModels.get(posit).getImageKey();
        int id = cacheImageModels.get(posit).getId();
        Uri uri = ContentUris.withAppendedId(MCDataContract.CONTENT_URI, id);
        sendToGoogleAnalyticsEvetns(urlImagePosition, namePosition);

        if (!isUserHavePrem) {
            InterstitialAd mInterstitialAds = AdsManager.mInterstitialAd;
            if (mInterstitialAds != null ) {
                SplashScreenActivity.adsManager.showInterstitialAds((Activity) context);
                mInterstitialAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Intent intent = new Intent(v.getContext(),
                                ColoringActivity.class);
                        intent.putExtra("urlImagePosition", urlImagePosition);
                        intent.putExtra("nameImage", namePosition);
                        intent.putExtra("keyPosition", imageKeyPosition);
                        intent.putExtra("categoryPosition", categoryPosition);
                        intent.putExtra("position", posit);
                        intent.setData(uri);
                        v.getContext().startActivity(intent);
                    }
                });
            } else {
                Intent intent = new Intent(v.getContext(),
                        ColoringActivity.class);
                intent.putExtra("urlImagePosition", urlImagePosition);
                intent.putExtra("nameImage", namePosition);
                intent.putExtra("keyPosition", imageKeyPosition);
                intent.putExtra("categoryPosition", categoryPosition);
                intent.putExtra("position", posit);
                intent.setData(uri);
                v.getContext().startActivity(intent);
            }
        } else {
            Intent intent = new Intent(v.getContext(),
                    ColoringActivity.class);
            intent.putExtra("urlImagePosition", urlImagePosition);
            intent.putExtra("nameImage", namePosition);
            intent.putExtra("keyPosition", imageKeyPosition);
            intent.putExtra("categoryPosition", categoryPosition);
            intent.putExtra("position", posit);
            intent.setData(uri);
            v.getContext().startActivity(intent);
        }

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
        private ImageView iconPremium;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
            progressBarItem = itemView.findViewById(R.id.progressBarItem);
            cardView = itemView.findViewById(R.id.cardView);
            relativeLayout = itemView.findViewById(R.id.parentItem);
            iconNew = itemView.findViewById(R.id.iconNewRecycler);
            iconPremium = itemView.findViewById(R.id.iconPremiumRecycler);
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {

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


    private void sendToGoogleAnalyticsEvetns(String urlImagePosition, String nameImage) {
        Bundle params = new Bundle();
        params.putString("urlImagePosition", urlImagePosition);
        params.putString("nameImage", nameImage);
        mFirebaseAnalytics.logEvent("share_image", params);
    }
}
