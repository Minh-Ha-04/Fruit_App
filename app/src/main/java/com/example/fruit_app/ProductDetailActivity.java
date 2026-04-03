package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Product;
import com.example.fruit_app.util.PreferenceManager;
import com.example.fruit_app.util.PriceFormatter;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView productNameTextView, productDescriptionTextView, productPriceTextView;
    private EditText quantityEditText;
    private Button addToCartButton;
    private AppDatabase database;
    private PreferenceManager preferenceManager;
    private int productId;
    private double productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        database = AppDatabase.getDatabase(this);
        preferenceManager = new PreferenceManager(this);

        productNameTextView = findViewById(R.id.productNameTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        quantityEditText = findViewById(R.id.quantityEditText);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Get product details from intent
        Intent intent = getIntent();
        productId = intent.getIntExtra("product_id", 0);
        String productName = intent.getStringExtra("product_name");
        String productDescription = intent.getStringExtra("product_description");
        productPrice = intent.getDoubleExtra("product_price", 0);

        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        productPriceTextView.setText("Giá: " + PriceFormatter.format(productPrice));

        addToCartButton.setOnClickListener(v -> {
            if (!preferenceManager.isLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
            } else {
                addToCart();
            }
        });
    }

    private void addToCart() {
        String quantityStr = quantityEditText.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add to order on a background thread
        new Thread(() -> {
            int userId = preferenceManager.getUserId();
            
            // Get or create pending order
            com.example.fruit_app.entity.Order order = database.orderDao()
                    .getOrderByUserAndStatus(userId, "Pending");

            if (order == null) {
                order = new com.example.fruit_app.entity.Order(userId, 
                    java.time.LocalDate.now().toString(), "Pending", 0);
                long orderId = database.orderDao().insert(order);
                order.id = (int) orderId;
            }

            // Add order detail
            com.example.fruit_app.entity.OrderDetail orderDetail = 
                new com.example.fruit_app.entity.OrderDetail(order.id, productId, quantity, productPrice);
            database.orderDetailDao().insert(orderDetail);

            // Update order total
            order.totalAmount += productPrice * quantity;
            database.orderDao().update(order);

            runOnUiThread(() -> {
                Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
