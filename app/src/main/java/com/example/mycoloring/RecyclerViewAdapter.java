package com.example.mycoloring;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mycoloring.menuItemFragment.MyGalery;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoloring.model.CardItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<CardItemModel> imageList;
    private Context context;
    private String imgUrlArrayList;
    private String category;
    private String categoryFragment;
    private String nameImage;
    private ProgressBar progressBarItem;
    int occurrences;


    public RecyclerViewAdapter(List<CardItemModel> imageList, Context context, String categoryFragment) {
        this.imageList = imageList;
        this.context = context;
        this.categoryFragment = categoryFragment;
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

        nameImage = imageList.get(position).getName();
        category = imageList.get(position).getCategory();
        imgUrlArrayList = imageList.get(position).getImgUrl();
            holder.progressBarItem.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imgUrlArrayList)
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
                if (posit < 0 || posit >= imageList.size()) {
                    Log.d("POSITION", "Number postition " + posit + " its error");
                } else {
                    String urlImagePosition = imageList.get(posit).getImgUrl();
                    String namePosition = imageList.get(posit).getName();
                    Intent intent = new Intent(v.getContext(),
                            ColoringActivity.class);
                    intent.putExtra("urlImagePosition", urlImagePosition);
                    intent.putExtra("nameImage", namePosition);
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
        return imageList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private ProgressBar progressBarItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imageView);
            progressBarItem = itemView.findViewById(R.id.progressBarItem);
        }
    }
}
