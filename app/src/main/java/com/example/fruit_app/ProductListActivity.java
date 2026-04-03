package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.adapter.ProductAdapter;
import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Product;

import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private ListView listView;
    private AppDatabase database;
    private List<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        listView = findViewById(R.id.productListView);
        database = AppDatabase.getDatabase(this);

        int categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");
        
        if (categoryName != null) {
            setTitle(categoryName);
        }

        // Load products
        new Thread(() -> {
            if (categoryId != -1) {
                productList = database.productDao().getProductsByCategory(categoryId);
            } else {
                productList = database.productDao().getAllProducts();
            }
            runOnUiThread(this::displayProducts);
        }).start();
    }

    private void displayProducts() {
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = productList.get(position);
            Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", selectedProduct.id);
            intent.putExtra("product_name", selectedProduct.name);
            intent.putExtra("product_description", selectedProduct.description);
            intent.putExtra("product_price", selectedProduct.price);
            startActivity(intent);
        });
    }
}
