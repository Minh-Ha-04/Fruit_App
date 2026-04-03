package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Order;
import com.example.fruit_app.entity.OrderDetail;
import com.example.fruit_app.entity.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationActivity extends AppCompatActivity {

    private ListView confirmationDetailListView;
    private TextView confirmationMessageTextView, confirmationTotalTextView;
    private Button homeButton;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        database = AppDatabase.getDatabase(this);

        confirmationDetailListView = findViewById(R.id.confirmationDetailListView);
        confirmationMessageTextView = findViewById(R.id.confirmationMessageTextView);
        confirmationTotalTextView = findViewById(R.id.confirmationTotalTextView);
        homeButton = findViewById(R.id.homeButton);

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("order_id", 0);

        loadOrder(orderId);

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(OrderConfirmationActivity.this, MainActivity.class));
            finish();
        });
    }

    private void loadOrder(int orderId) {
        new Thread(() -> {
            Order order = database.orderDao().getOrderById(orderId);

            if (order != null) {
                List<OrderDetail> orderDetails = database.orderDetailDao()
                        .getOrderDetailsByOrder(orderId);

                List<String> itemList = new ArrayList<>();
                double total = 0;

                for (OrderDetail detail : orderDetails) {
                    Product product = database.productDao().getProductById(detail.productId);
                    if (product != null) {
                        String itemText = product.name + " x" + detail.quantity + 
                            " - " + String.format("%.0f VND", detail.price * detail.quantity);
                        itemList.add(itemText);
                        total += detail.price * detail.quantity;
                    }
                }

                double finalTotal = total;
                runOnUiThread(() -> displayConfirmation(order, itemList, finalTotal));
            }
        }).start();
    }

    private void displayConfirmation(Order order, List<String> itemList, double total) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_list_item_1, itemList);
        confirmationDetailListView.setAdapter(adapter);

        confirmationMessageTextView.setText("✓ Đơn hàng #" + order.id + " đã được tạo thành công!\n" +
            "Ngày đặt hàng: " + order.orderDate + "\n" +
            "Trạng thái: " + order.status);
        confirmationTotalTextView.setText(String.format("Tổng tiền: %.0f VND", total));
    }
}
