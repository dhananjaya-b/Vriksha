
package com.example.vriksha;
import android.content.Intent;
import android.net.Uri;
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

public class SchemesCardAdapter extends RecyclerView.Adapter<SchemesCardAdapter.ViewHolder> {

    private List<DocumentSnapshot> schemeList;
    private FirebaseFirestore firestore;

    public SchemesCardAdapter(List<DocumentSnapshot> schemeList) {
        this.schemeList = schemeList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schemes_list_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot newsSnapshot = schemeList.get(position);
        String title = (String) newsSnapshot.get("Title");
        String imageUrl = (String) newsSnapshot.get("ImageUrl");
        String description = (String) newsSnapshot.get("Description");
        String link = (String) newsSnapshot.get("MoreDetailsLink");
        holder.textViewTitle.setText(title);
        holder.textViewDescription.setText(description);
        holder.textViewLink.setText(link);
        // Load image using Picasso
        Picasso.get().load(imageUrl).into(holder.imageView);
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // Handle item click event
        });
        holder.textViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (link != null && !link.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(link));
                    v.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle;
        TextView textViewLink;
        TextView textViewDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewLink = itemView.findViewById(R.id.textViewLink);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}


