package com.ttcreator.mycoloring.adapters;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ttcreator.mycoloring.ColoringActivity;
import com.ttcreator.mycoloring.MyApp;
import com.ttcreator.mycoloring.PurchaseDialog;
import com.ttcreator.mycoloring.R;
import com.ttcreator.mycoloring.SharedPreferencesFactory;
import com.ttcreator.mycoloring.data.MCDataContract;
import com.ttcreator.mycoloring.model.CategoryItemModel;

import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private List<CategoryItemModel> categoryItemModelList;
    private Context context;
    private String imgUrl, imageName, categoryName;
    private int imageKey, haveAds, premiumStatus, new_status;
    private String categoryBlock;
    private String nameItem;
    private String category;
    private int lastPosition = -1;

    public SearchRecyclerAdapter(List<CategoryItemModel> categoryItemModelList, Context context, String categoryBlock) {
        this.categoryItemModelList = categoryItemModelList;
        this.context = context;
        this.categoryBlock = categoryBlock;
    }

    @NonNull
    @Override
    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecyclerAdapter.ViewHolder holder, int position) {

        imgUrl = categoryItemModelList.get(position).getImgUrl();
        imageName = categoryItemModelList.get(position).getName();
        imageKey = categoryItemModelList.get(position).getimgKey();
        categoryName = categoryItemModelList.get(position).getCategory();
        new_status = categoryItemModelList.get(position).getNewStatus();
        haveAds = categoryItemModelList.get(position).getHaveAds();
        premiumStatus = categoryItemModelList.get(position).getPremiumStatus();
        boolean isUserHavePrem = SharedPreferencesFactory.getBoolean(context, "isPurchase");
        if (new_status != 0) {
            holder.iconNew.setImageResource(R.drawable.icons_new);
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
        holder.progressBarCategory.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(((MyApp.getScreenWidth(context)) / 3) - 20,
                MyApp.getScreenWidth(context) / 3);
        layoutParamsFrame.setMargins(5, 10, 5, 10);
        holder.cardView.setLayoutParams(layoutParamsFrame);
        Glide.with(context)
                .load(imgUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        holder.itemView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBarCategory.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageCategory);

        if (haveAds != 0) {
            setAnimation(holder.itemView, position);
        }

        holder.imageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posit = holder.getAdapterPosition();
                if (posit < 0 || posit >= categoryItemModelList.size()) {
                    Log.d("POSITION", "Number postition " + posit + " its error");
                } else {
                    if (categoryItemModelList.get(posit).getPremiumStatus() != 1) {
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

        String urlImagePosition = categoryItemModelList.get(posit).getImgUrl();
        String namePosition = categoryItemModelList.get(posit).getName();
        String catNamePosition = categoryItemModelList.get(posit).getCategory();
        int imageKeyPosition = categoryItemModelList.get(posit).getimgKey();
        int id = categoryItemModelList.get(posit).getId();
        Uri uri = ContentUris.withAppendedId(MCDataContract.CONTENT_URI, id);
        Intent intent = new Intent(v.getContext(),
                ColoringActivity.class);
        intent.putExtra("urlImagePosition", urlImagePosition);
        intent.putExtra("nameImage", namePosition);
        intent.putExtra("keyPosition", imageKeyPosition);
        intent.putExtra("categoryPosition", catNamePosition);
        intent.putExtra("position", posit);
        intent.setData(uri);
        v.getContext().startActivity(intent);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        if (categoryItemModelList.size() > 11) {
            return 11;
        } else {
            return categoryItemModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageCategory;
        private ProgressBar progressBarCategory;
        private ImageView iconNew;
        private ImageView iconPremium;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageCategory = itemView.findViewById(R.id.imageViewSearch);
            progressBarCategory = itemView.findViewById(R.id.progressBarSearch);
            iconNew = itemView.findViewById(R.id.iconNewRecyclerSearch);
            iconPremium = itemView.findViewById(R.id.iconPremiumRecyclerSearch);
            cardView = itemView.findViewById(R.id.parentItemSearch);
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
}
