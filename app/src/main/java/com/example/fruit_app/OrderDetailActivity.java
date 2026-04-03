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

public class OrderDetailActivity extends AppCompatActivity {

    private ListView orderDetailListView;
    private TextView orderInfoTextView, totalAmountTextView;
    private Button backButton;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        database = AppDatabase.getDatabase(this);

        orderDetailListView = findViewById(R.id.orderDetailListView);
        orderInfoTextView = findViewById(R.id.orderInfoTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        backButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        int orderId = intent.getIntExtra("order_id", 0);

        loadOrderDetail(orderId);

        backButton.setOnClickListener(v -> finish());
    }

    private void loadOrderDetail(int orderId) {
        new Thread(() -> {
            Order order = database.orderDao().getOrderById(orderId);
            List<OrderDetail> orderDetails = database.orderDetailDao()
                    .getOrderDetailsByOrder(orderId);

            List<String> itemList = new ArrayList<>();
            double total = 0;

            if (order != null) {
                for (OrderDetail detail : orderDetails) {
                    Product product = database.productDao().getProductById(detail.productId);
                    if (product != null) {
                        String itemText = product.name + " x" + detail.quantity +
                                " - " + String.format("%.0f VND", detail.price * detail.quantity);
                        itemList.add(itemText);
                        total += detail.price * detail.quantity;
                    }
                }
            }

            double finalTotal = total;
            runOnUiThread(() -> displayOrderDetail(order, itemList, finalTotal));
        }).start();
    }

    private void displayOrderDetail(Order order, List<String> itemList, double total) {
        if (order == null) return;

        // Hiển thị thông tin đơn hàng
        String orderInfo = "Đơn Hàng #" + order.id + "\n" +
                "Ngày: " + order.orderDate + "\n" +
                "Trạng thái: " + order.status;
        orderInfoTextView.setText(orderInfo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, itemList);
        orderDetailListView.setAdapter(adapter);

        totalAmountTextView.setText(String.format("Tổng cộng: %.0f VND", total));
    }
}
