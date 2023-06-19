package com.example.vriksha;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Map<String, Object>> cartList;

    private  String cartid=null;
    Button buttonPlaceOrder;
    public interface OnRefreshListener {
        void onRefresh();
    }

    private OnRefreshListener refreshListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        refreshListener = listener;
    }
    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        cartList = new ArrayList<>();
        fetchCartData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartAdapter = new CartAdapter(cartList);
        recyclerView.setAdapter(cartAdapter);


        Button buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();

            }
        });

        ;
        return view;
    }

    private void fetchCartData() {
        // Get the current user's ID, you can replace it with your own logic
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String userID = null;
        if (currentUser != null) {
            userID = currentUser.getUid();
            System.out.println(userID);
        } else {
            // User is not logged in or authenticated
        }

        if (userID != null) {
            // Retrieve the cart document for the user
            firestore.collection("carts")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            cartid=documentSnapshot.getId();
                            if (documentSnapshot.exists()) {
                                List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                                for (Map<String, Object> item : items) {
                                    item.put("cartid",documentSnapshot.getId());
                                    cartList.add(item);
                                }
                            } else {
                                System.out.println("Cart document does not exist.");
                            }
                        }
                        cartAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to retrieve cart data
                        System.out.println("Error retrieving cart data: " + e.getMessage());
                    });
        } else {
            // User ID is null, handle the scenario accordingly
        }
    }
    private void placeOrder() {

        // Retrieve the current user's ID
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userID = currentUser != null ? currentUser.getUid() : null;

        if (userID != null) {
            //Toast.makeText(getActivity(), "inisde function", Toast.LENGTH_SHORT).show();
            cartAdapter.notifyDataSetChanged();
            // Retrieve the cart document for the user
            firestore.collection("carts")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                // Get the cart details
                                List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");
                                double totalPrice = calculateTotalPrice(items);

                                // Create a new order document with the cart details and total price
                                Map<String, Object> orderData = new HashMap<>();
                                orderData.put("userID", userID);
                                orderData.put("items", items);
                                orderData.put("totalPrice", totalPrice);

                                // Add the order document to the "orders" collection
                                firestore.collection("orders")
                                        .add(orderData)
                                        .addOnSuccessListener(documentReference -> {
                                            // Order document added successfully, now remove the cart document
                                            documentSnapshot.getReference().delete()
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(getActivity(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                                        startActivity(intent);
                                                        getActivity().finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Failed to remove the cart document
                                                        Toast.makeText(getActivity(), "Failed to remove the cart. Please try again.", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to add the order document
                                            Toast.makeText(getActivity(), "Failed to place the order. Please try again.", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Cart document does not exist
                                Toast.makeText(getActivity(), "Cart document does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Failed to retrieve cart data
                        Toast.makeText(getActivity(), "Error retrieving cart data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // User ID is null, handle the scenario accordingly
            Toast.makeText(getActivity(), "User not logged in or authenticated.", Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateTotalPrice(List<Map<String, Object>> items) {
        double totalPrice = 0;
        for (Map<String, Object> item : items) {
            double price = (double) item.get("price");
            long quantity = (long) item.get("quantity");
            totalPrice += price * quantity;
        }
        return totalPrice;
    }

}
