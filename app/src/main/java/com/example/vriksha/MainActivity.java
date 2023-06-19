package com.example.vriksha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private BottomNavigationView bottomNavigationView;
    private ImageView cartbutton;
    private LocationManager locationManager;
    private LocationListener locationListener;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        cartbutton = findViewById(R.id.cartButton);
        CartFragment cartFragment = new CartFragment();


        cartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, cartFragment)
                        .commit();
            }
        });

    }







    HomeFragment firstFragment = new HomeFragment();
    NewsFragment secondFragment = new NewsFragment();
    SchemeFragment thirdFragment = new SchemeFragment();
    ProductFragment fourthFragment = new ProductFragment();
    ProfileFragment fifthFragment = new ProfileFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, firstFragment)
                        .commit();
                return true;

            case R.id.acction_news:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, secondFragment)
                        .commit();
                return true;

            case R.id.action_scheme:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, thirdFragment)
                        .commit();
                return true;
            case R.id.action_buy:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fourthFragment)
                        .commit();
                return true;
            case R.id.action_user:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fifthFragment)
                        .commit();
                return true;
        }
        return false;
    }
}
