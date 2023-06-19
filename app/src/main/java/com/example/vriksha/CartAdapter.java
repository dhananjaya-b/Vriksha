package com.example.vriksha;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vriksha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Map<String, Object>> cartList;

    public CartAdapter(List<Map<String, Object>> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> cartItem = cartList.get(position);
        String productID = (String) cartItem.get("productID");
        long quantity = (long) cartItem.get("quantity");
        String name = (String) cartItem.get("name");
        double price = (double) cartItem.get("price");
        price=price*quantity;
        String imageUrl = (String) cartItem.get("imageUrl");
        String cartID = (String) cartItem.get("cartid");
        System.out.println("calling");
        // Set the product details to the views
        holder.textViewTitle.setText(name);
        holder.textViewQuantity.setText(String.valueOf(quantity));
        holder.textViewPrice.setText(String.valueOf(price));

        // Load the product image using Picasso or any other image loading library
        Picasso.get().load(imageUrl).into(holder.imageView);

        holder.buttonRemoveItem.setOnClickListener(v -> {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            String userID = currentUser != null ? currentUser.getUid() : null;
            // Remove the item from the list
            cartList.remove(position);
            notifyDataSetChanged();
            Map<String, Object> item=cartItem;
            item.remove("cartid");
            // Remove the item from the Firestore collection
            FirebaseFirestore.getInstance()
                    .collection("carts")
                    .document(cartID)
                    .update("items", FieldValue.arrayRemove(item))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(holder.itemView.getContext(), "Removed From cart!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure to remove the item
                        Log.e("CartAdapter", "Error removing item from cart: " + e.getMessage());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewQuantity;
        TextView textViewPrice;

        Button buttonRemoveItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cartImage);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewQuantity = itemView.findViewById(R.id.quantiycart);
            textViewPrice = itemView.findViewById(R.id.pricecart);
            buttonRemoveItem=itemView.findViewById(R.id.buttonRemoveItem);
        }
    }
}
