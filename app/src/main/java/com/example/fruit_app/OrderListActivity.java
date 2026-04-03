package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Order;
import com.example.fruit_app.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private ListView orderListView;
    private TextView emptyMessageTextView;
    private AppDatabase database;
    private PreferenceManager preferenceManager;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        database = AppDatabase.getDatabase(this);
        preferenceManager = new PreferenceManager(this);

        orderListView = findViewById(R.id.orderListView);
        emptyMessageTextView = findViewById(R.id.emptyMessageTextView);

        loadOrders();
    }

    private void loadOrders() {
        new Thread(() -> {
            int userId = preferenceManager.getUserId();
            // Lấy tất cả orders đã thanh toán (status = "Paid")
            List<Order> allOrders = database.orderDao().getOrdersByUser(userId);
            orderList = new ArrayList<>();
            
            for (Order order : allOrders) {
                if ("Paid".equals(order.status)) {
                    orderList.add(order);
                }
            }

            runOnUiThread(this::displayOrders);
        }).start();
    }

    private void displayOrders() {
        if (orderList == null || orderList.isEmpty()) {
            orderListView.setVisibility(ListView.GONE);
            emptyMessageTextView.setVisibility(TextView.VISIBLE);
            emptyMessageTextView.setText("Bạn chưa có đơn hàng nào");
            return;
        }

        emptyMessageTextView.setVisibility(TextView.GONE);
        orderListView.setVisibility(ListView.VISIBLE);

        List<String> orderDisplay = new ArrayList<>();
        for (Order order : orderList) {
            String orderInfo = "Đơn #" + order.id + 
                " | Ngày: " + order.orderDate + 
                " | Tiền: " + String.format("%.0f VND", order.totalAmount);
            orderDisplay.add(orderInfo);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, orderDisplay);
        orderListView.setAdapter(adapter);

        // Click vào order để xem chi tiết
        orderListView.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = orderList.get(position);
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            intent.putExtra("order_id", selectedOrder.id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
