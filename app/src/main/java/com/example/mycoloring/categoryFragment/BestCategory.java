package com.example.mycoloring.categoryFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mycoloring.R;
import com.example.mycoloring.RecyclerViewAdapter;
import com.example.mycoloring.model.CardItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BestCategory extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    private List<CardItemModel> imageList;
    View root;
    DatabaseReference dbRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_best_category, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewBest);
        recyclerView.setHasFixedSize(true);
        dbRef = FirebaseDatabase.getInstance().getReference();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        imageList = new ArrayList<>();
        getDataFromFirebase();

        return root;
    }

    private void getDataFromFirebase() {

        Query query = dbRef.child("images");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CardItemModel cardItemModel = new CardItemModel();
                    if (dataSnapshot.child("category").getValue().toString().equals("best")) {
                        cardItemModel.setImgUrl(dataSnapshot.child("imageUrl")
                                .getValue().toString());
                        cardItemModel.setName(dataSnapshot.child("name")
                                .getValue().toString());
                        cardItemModel.setCategory(dataSnapshot.child("category")
                                .getValue().toString());
                        imageList.add(cardItemModel);
                    }
                }
                recyclerViewAdapter = new RecyclerViewAdapter(imageList,
                        getContext(), "best");
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}