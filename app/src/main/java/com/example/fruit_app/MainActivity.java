package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.util.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button viewProductsButton, viewCategoriesButton, viewCartButton, viewOrdersButton, logoutButton, loginButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(this);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        viewProductsButton = findViewById(R.id.viewProductsButton);
        viewCategoriesButton = findViewById(R.id.viewCategoriesButton);
        viewCartButton = findViewById(R.id.viewCartButton);
        viewOrdersButton = findViewById(R.id.viewOrdersButton);
        logoutButton = findViewById(R.id.logoutButton);
        loginButton = findViewById(R.id.loginButton); // I'll assume this ID exists or I should add it

        updateUI();

        viewProductsButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, ProductListActivity.class))
        );

        viewCategoriesButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, CategoryListActivity.class))
        );

        viewCartButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, OrderActivity.class))
        );

        viewOrdersButton.setOnClickListener(v -> 
            startActivity(new Intent(MainActivity.this, OrderListActivity.class))
        );

        logoutButton.setOnClickListener(v -> {
            preferenceManager.logout();
            updateUI();
        });

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void updateUI() {
        if (preferenceManager.isLoggedIn()) {
            welcomeTextView.setText("Chào mừng, " + preferenceManager.getUsername());
            viewCartButton.setVisibility(View.VISIBLE);
            viewOrdersButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            welcomeTextView.setText("Chào mừng khách!");
            viewCartButton.setVisibility(View.GONE);
            viewOrdersButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
