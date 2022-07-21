package com.example.mycoloring;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        List <Integer> image;
        List <String> title;
        LayoutInflater inflater;

        public RecyclerViewAdapter(List<Integer> image, List<String> title, Context context) {
                this.image = image;
                this.title = title;
                this.inflater = inflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = inflater.inflate(R.layout.recycler_view_item, parent, false);

                view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), ColoringActivity.class);
                                view.getContext().startActivity(intent);
                        }
                });
                return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.title.setText(title.get(position));
                holder.gridImage.setImageResource(image.get(position));

        }

        @Override
        public int getItemCount() {
                return title.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

                TextView title;
                ImageView gridImage;

                public ViewHolder(@NonNull View itemView) {
                        super(itemView);
                        title = itemView.findViewById(R.id.textView);
                        gridImage = itemView.findViewById(R.id.imageView);
                }
        }
        }
