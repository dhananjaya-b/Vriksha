package com.example.vriksha;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {




    private FirebaseFirestore db;
    private TextView usernameTextView ;
    private TextView emailTextView ;
    private TextView phoneTextView ;
    private TextView addressTextView;
    private Button logoutButton;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db=FirebaseFirestore.getInstance();
        usernameTextView = view.findViewById(R.id.usernameUser);
        emailTextView = view.findViewById(R.id.emailUser);
        phoneTextView = view.findViewById(R.id.phoneUser);
        addressTextView = view.findViewById(R.id.useraddressUser);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        String userID = null;
        if (currentUser != null) {
            userID = currentUser.getUid();
            System.out.println(userID);
        } else {
            // User is not logged in or authenticated
        }
        // Fetch and set the user details
        fetchUserDetails(userID,view);

        logoutButton = view.findViewById(R.id.logoutButton);

        // Set an OnClickListener to handle the logout action
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the logout action here
                logoutUser();
            }
        });

        return view;
    }
    private void logoutUser() {
        // Implement your logout logic here
        // For example, clear the user session, navigate to the login screen, etc.
        // You can use FirebaseAuth or your preferred authentication mechanism for logout

        // For FirebaseAuth example:
        FirebaseAuth.getInstance().signOut();

        // Navigate to the login screen or any other desired screen
        // For example, navigate to MainActivity:
        Intent intent = new Intent(getActivity(), LoginActivty.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void fetchUserDetails(String userID, View view) {
        db.collection("users")
                .whereEqualTo("userId", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Retrieve the first document from the query results
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Retrieve the user details from the document
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");
                            String phoneNumber = documentSnapshot.getString("phoneNumber");
                            String address = documentSnapshot.getString("address");

                            // Set the retrieved user details to the respective TextViews
                            usernameTextView.setText("Username: " + username);
                            emailTextView.setText("Email: " + email);
                            phoneTextView.setText("Phone Number: " + phoneNumber);
                            addressTextView.setText("Address: " + address);
                        } else {
                            // No matching documents found
                            Toast.makeText(view.getContext(), "User details not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while fetching user details
                        Toast.makeText(view.getContext(), "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}