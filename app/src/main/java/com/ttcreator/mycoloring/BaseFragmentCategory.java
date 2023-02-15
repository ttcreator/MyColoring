package com.ttcreator.mycoloring;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ttcreator.mycoloring.adapters.RecyclerViewAdapter;
import com.ttcreator.mycoloring.data.MCDBHelper;
import com.ttcreator.mycoloring.model.CacheImageModel;

import java.util.ArrayList;

public class BaseFragmentCategory extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private String category;
    private MCDBHelper dbHelper;
    private ArrayList<CacheImageModel> cacheImageModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_base_category, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewSecond);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cacheImageModels = requireArguments().getParcelableArrayList("cacheImageModelsList");
        category = requireArguments().getString("currentCategory");
        if (category != null) {
            String[] cat = {category};
            dbHelper = new MCDBHelper(getContext());
            cacheImageModels = dbHelper.getCacheImageByCategory(cat, requireContext());
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewAdapter = new RecyclerViewAdapter(cacheImageModels, getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        getParentFragmentManager().setFragmentResultListener("resultPurchasedToFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("isPurchaseOK");
                if (result.equals("OK")) {
                    int position = requireActivity().getIntent().getIntExtra("position", 0);
                    recyclerViewAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    public static Fragment newInstance(ArrayList <CacheImageModel> cacheImageModelsArrayList, String currentCategory) {

        BaseFragmentCategory f = new BaseFragmentCategory();
        Bundle args = new Bundle();
        args.putParcelableArrayList("cacheImageModelsList", cacheImageModelsArrayList);
        args.putString("currentCategory", currentCategory);
        f.setArguments(args);
        return f;
    }

}
