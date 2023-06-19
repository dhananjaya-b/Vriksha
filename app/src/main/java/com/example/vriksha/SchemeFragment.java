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

public class SchemeFragment extends Fragment {

    private RecyclerView recyclerView;
    private SchemesCardAdapter schemesCardAdapter;
    private List<DocumentSnapshot> schemeList;
    private FirebaseFirestore firestore;
    public SchemeFragment() {
        // Required empty public constructor
    }

    public static SchemeFragment newInstance(String param1, String param2) {
        SchemeFragment fragment = new SchemeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        schemeList = new ArrayList<>();
        fetchSchemeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheme, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCards1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        schemesCardAdapter = new SchemesCardAdapter(schemeList);
        recyclerView.setAdapter(schemesCardAdapter);

        return view;
    }
    private void fetchSchemeData() {
        firestore.collection("schemesData")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    schemeList.addAll(queryDocumentSnapshots.getDocuments());
                    schemesCardAdapter.notifyDataSetChanged();
                    System.out.println(schemeList);
                })
                .addOnFailureListener(e -> {

                });
    }
}