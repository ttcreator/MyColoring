package com.example.mycoloring;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Category1 extends Fragment {

    RecyclerView recyclerView;
    List <Integer> image;
    List <String> title;
    RecyclerViewAdapter recyclerViewAdapter;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_category1, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);

        image = new ArrayList<>();
        title = new ArrayList<>();

        image.add(R.drawable.ic_baseline_10k_24);
        image.add(R.drawable.ic_baseline_10k_24);
        image.add(R.drawable.ic_baseline_10k_24);
        image.add(R.drawable.ic_baseline_10k_24);

        title.add("Title 1");
        title.add("Title 2");
        title.add("Title 3");
        title.add("Title 4");

        recyclerViewAdapter = new RecyclerViewAdapter(image, title, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),
                2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        return root;

    }
}