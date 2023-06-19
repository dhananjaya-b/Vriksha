package com.example.vriksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputLayout usernameTextInputLayout, emailTextInputLayout, passwordTextInputLayout, phoneNumberTextInputLayout, addressTextInputLayout;
    private EditText usernameEditText, emailEditText, passwordEditText, phoneNumberEditText, addressEditText;
    private Button registerButton;
    private TextView loginTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameTextInputLayout = findViewById(R.id.username);
        emailTextInputLayout = findViewById(R.id.email);
        passwordTextInputLayout = findViewById(R.id.password);
        phoneNumberTextInputLayout = findViewById(R.id.phonenumber);
        addressTextInputLayout = findViewById(R.id.address);

        usernameEditText = usernameTextInputLayout.getEditText();
        emailEditText = emailTextInputLayout.getEditText();
        passwordEditText = passwordTextInputLayout.getEditText();
        phoneNumberEditText = phoneNumberTextInputLayout.getEditText();
        addressEditText = addressTextInputLayout.getEditText();

        registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setEnabled(false);
                createUser();
            }
        });
        loginTextView = findViewById(R.id.haveac);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SignUpActivity.this,LoginActivty.class);
            }
        });
    }
    private void createUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User creation successful, proceed to save user details in Firestore
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a User object with the entered details
                            UserModel user = new UserModel(userId, username, email, phoneNumber, address);

                            // Save the User object to Firestore
                            db.collection("users")
                                    .document(userId)
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // User details saved successfully
                                                Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent =new Intent(SignUpActivity.this,MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                // Failed to save user details
                                                Toast.makeText(SignUpActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                                Log.e("SignUpActivity", "Error saving user details to Firestore", task.getException());
                                                registerButton.setEnabled(true);
                                            }
                                        }
                                    });
                        } else {

                            Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                            Log.e("SignUpActivity", "Error creating user with Firebase Authentication", task.getException());
                            registerButton.setEnabled(true);
                        }
                    }
                });
    }
}