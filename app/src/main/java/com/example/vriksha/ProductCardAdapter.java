package com.example.vriksha;

import static java.security.AccessController.getContext;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ViewHolder> {

    private List<DocumentSnapshot> productList;
    private FirebaseFirestore firestore;

    public ProductCardAdapter(List<DocumentSnapshot> newsList) {
        this.productList = newsList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot newsSnapshot = productList.get(position);
        String title = (String) newsSnapshot.get("name");
        String imageUrl = (String) newsSnapshot.get("imageUrl");
        Double price = (Double) newsSnapshot.get("price");
        System.out.println(newsSnapshot.getId());
        holder.textViewQuantity.setText(String.valueOf(holder.q));
        holder.textViewTitle.setText(title);
        holder.textViewPrice.setText(price.toString());
        Picasso.get().load(imageUrl).into(holder.imageView);

        holder.imageViewSub.setOnClickListener(view -> {
            if (holder.q > 1) {
                holder.q--;
                holder.textViewQuantity.setText(String.valueOf(holder.q));
            }
        });

        holder.imageViewAdd.setOnClickListener(view -> {
            holder.q++;
            holder.textViewQuantity.setText(String.valueOf(holder.q));
        });

        holder.buttonAddToCart.setOnClickListener(v -> {
            String productID = newsSnapshot.getId();
            int quantity = holder.q;

            // Get the current user's ID
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            String userID = null;
            if (currentUser != null) {
                userID = currentUser.getUid();
                // Use the userID as needed
            } else {
                // User is not logged in or authenticated
            }

            // Create a new cart item with the product ID and quantity
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("productID", productID);
            cartItem.put("name", title);
            cartItem.put("price", price);
            cartItem.put("imageUrl", imageUrl);
            cartItem.put("quantity", quantity);

            // Check if the cart already exists for the user
            String finalUserID = userID;
            firestore.collection("carts")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Cart already exists, check if the item already exists in the cart
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                String cartID = documentSnapshot.getId();
                                List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");

                                // Iterate through the items and find the matching product ID
                                boolean itemExists = false;
                                for (Map<String, Object> item : items) {
                                    String itemProductID = (String) item.get("productID");
                                    if (itemProductID != null && itemProductID.equals(productID)) {
                                        // Item already exists, increase the quantity
                                        long currentQuantity = (long) item.get("quantity");
                                        long updatedQuantity = currentQuantity + quantity;
                                        item.put("quantity", updatedQuantity);
                                        itemExists = true;
                                        break;
                                    }
                                }

                                // If the item doesn't exist, add it to the existing cart
                                if (!itemExists) {
                                    items.add(cartItem);
                                }

                                // Update the items array in the cart document
                                firestore.collection("carts")
                                        .document(cartID)
                                        .update("items", items)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                                            holder.q=1;
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                                            // Handle the error accordingly
                                        });
                            } else {
                                // Cart doesn't exist, create a new cart for the user
                                Map<String, Object> cartData = new HashMap<>();
                                cartData.put("userID", finalUserID);
                                cartData.put("items", Arrays.asList(cartItem));
                                firestore.collection("carts")
                                        .add(cartData)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                                            holder.q=1;
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                            // Handle the error accordingly
                        }
                    });
        });


    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView imageViewAdd;
        ImageView imageViewSub;
        TextView textViewTitle;
        TextView textViewQuantity;

        TextView textViewPrice;
        Button buttonAddToCart;
        int q;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageviewProdcut);
            textViewTitle = itemView.findViewById(R.id.ProductTitle);
            textViewPrice = itemView.findViewById(R.id.price);
            buttonAddToCart =itemView.findViewById(R.id.addtoCart);
            imageViewAdd=itemView.findViewById(R.id.addQuantity);
            imageViewSub=itemView.findViewById(R.id.subQuantity);
            textViewQuantity=itemView.findViewById(R.id.quantity);
            q = 1; // Set initial quantity value
        }
    }
}


