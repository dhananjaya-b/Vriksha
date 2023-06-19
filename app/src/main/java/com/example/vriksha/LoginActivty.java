package com.example.vriksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivty extends AppCompatActivity {
    TextInputLayout emailInputLayout, passwordInputLayout;
    TextInputEditText emailEditText, passwordEditText;
    Button login;
    TextView noAccount;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_login_activty);
        FirebaseAuth.getInstance().signOut();
        emailInputLayout = (TextInputLayout) findViewById(R.id.email);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.password);

        emailEditText = (TextInputEditText) emailInputLayout.getEditText();
        passwordEditText = (TextInputEditText) passwordInputLayout.getEditText();

        login=(Button)findViewById(R.id.login);
        noAccount=findViewById(R.id.noac);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(emailEditText.getText().toString());
                System.out.println(passwordEditText.getText().toString());
                mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                        .addOnCompleteListener(LoginActivty.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivty.this, "Sign in ", Toast.LENGTH_SHORT).show();
                                    Intent intent =new Intent(LoginActivty.this,MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // Handle error during sign in
                                    Toast.makeText(LoginActivty.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivty.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}