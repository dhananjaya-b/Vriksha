package com.example.vriksha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsCardAdapter extends RecyclerView.Adapter<NewsCardAdapter.ViewHolder> {

    private List<DocumentSnapshot> newsList;
    private FirebaseFirestore firestore;

    public NewsCardAdapter(List<DocumentSnapshot> newsList) {
        this.newsList = newsList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot newsSnapshot = newsList.get(position);
        String title = (String) newsSnapshot.get("Title");
        String imageUrl = (String) newsSnapshot.get("ImageUrl");
        String description = (String) newsSnapshot.get("Description");
        String date = (String) newsSnapshot.get("Date");

        holder.textViewTitle.setText(title);
        holder.textViewDescription.setText(description);
        holder.textViewDate.setText(date);

        // Load image using Picasso
        Picasso.get().load(imageUrl).into(holder.imageView);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Handle item click event
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewDate;
        TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}


