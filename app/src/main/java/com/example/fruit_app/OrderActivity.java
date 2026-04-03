package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.adapter.OrderAdapter;
import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Order;
import com.example.fruit_app.entity.OrderDetail;
import com.example.fruit_app.util.PreferenceManager;
import com.example.fruit_app.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.OnCartChangedListener {

    private ListView orderDetailListView;
    private TextView totalAmountTextView;
    private Button checkoutButton, continueShoppingButton;
    private AppDatabase database;
    private PreferenceManager preferenceManager;
    private Order currentOrder;
    private OrderAdapter adapter;
    private List<OrderDetail> orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        database = AppDatabase.getDatabase(this);
        preferenceManager = new PreferenceManager(this);

        orderDetailListView = findViewById(R.id.orderDetailListView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        checkoutButton = findViewById(R.id.checkoutButton);
        continueShoppingButton = findViewById(R.id.continueShoppingButton);

        loadOrder();

        checkoutButton.setOnClickListener(v -> {
            if (currentOrder != null && orderDetails != null && !orderDetails.isEmpty()) {
                // Get selected items
                ArrayList<Integer> selectedDetailIds = new ArrayList<>();
                Map<Integer, Boolean> selectedItems = adapter.getSelectedItems();
                for (OrderDetail detail : orderDetails) {
                    if (Boolean.TRUE.equals(selectedItems.get(detail.id))) {
                        selectedDetailIds.add(detail.id);
                    }
                }

                if (selectedDetailIds.isEmpty()) {
                    Toast.makeText(this, "Vui lòng chọn sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(OrderActivity.this, CheckoutActivity.class);
                intent.putExtra("order_id", currentOrder.id);
                intent.putIntegerArrayListExtra("selected_detail_ids", selectedDetailIds);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            }
        });

        continueShoppingButton.setOnClickListener(v -> {
            startActivity(new Intent(OrderActivity.this, ProductListActivity.class));
            finish();
        });
    }

    private void loadOrder() {
        new Thread(() -> {
            int userId = preferenceManager.getUserId();
            currentOrder = database.orderDao().getOrderByUserAndStatus(userId, "Pending");

            if (currentOrder == null) {
                currentOrder = new Order(userId, java.time.LocalDate.now().toString(), "Pending", 0);
                long orderId = database.orderDao().insert(currentOrder);
                currentOrder.id = (int) orderId;
            }

            orderDetails = database.orderDetailDao().getOrderDetailsByOrder(currentOrder.id);
            
            runOnUiThread(() -> {
                adapter = new OrderAdapter(this, orderDetails, this);
                orderDetailListView.setAdapter(adapter);
                updateTotal();
            });
        }).start();
    }

    @Override
    public void onCartChanged() {
        updateTotal();
    }

    private void updateTotal() {
        if (adapter == null || orderDetails == null) return;

        double total = 0;
        Map<Integer, Boolean> selectedItems = adapter.getSelectedItems();
        for (OrderDetail detail : orderDetails) {
            if (Boolean.TRUE.equals(selectedItems.get(detail.id))) {
                total += detail.price * detail.quantity;
            }
        }
        totalAmountTextView.setText("Tổng cộng: " + PriceFormatter.format(total));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder();
    }
}
