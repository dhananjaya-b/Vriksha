package com.example.vriksha;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NewsCardAdapter newsCardAdapter;
    private List<DocumentSnapshot> newsList;
    private FirebaseFirestore firestore;
    public NewsFragment() {
        // Required empty public constructor
    }
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        // Initialize newsList
        newsList = new ArrayList<>();

        // Fetch news data from Firestore
        fetchNewsData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewCards);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsCardAdapter = new NewsCardAdapter(newsList);
        recyclerView.setAdapter(newsCardAdapter);

        return view;
    }
    private void fetchNewsData() {
        firestore.collection("newsData")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    newsList.addAll(queryDocumentSnapshots.getDocuments());
                    newsCardAdapter.notifyDataSetChanged();
                    System.out.println(newsList);
                })
                .addOnFailureListener(e -> {

                });
    }
}