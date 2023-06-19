package com.example.vriksha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductCardAdapter productCardAdapter;
    private List<DocumentSnapshot> productList;
    private FirebaseFirestore firestore;
    public ProductFragment() {
        // Required empty public constructor
    }
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        // Initialize newsList
        productList = new ArrayList<>();

        // Fetch news data from Firestore
        fetchProductData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        recyclerView = view.findViewById(R.id.prodcutList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        productCardAdapter = new ProductCardAdapter(productList);
        recyclerView.setAdapter(productCardAdapter);

        return view;
    }
    private void fetchProductData() {
        firestore.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.addAll(queryDocumentSnapshots.getDocuments());
                    productCardAdapter.notifyDataSetChanged();
                    //System.out.println(productList);
                })
                .addOnFailureListener(e -> {

                });
    }
}