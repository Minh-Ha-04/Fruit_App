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
import com.example.fruit_app.util.PriceFormatter;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private ListView orderDetailListView;
    private TextView totalAmountTextView;
    private Button payButton, backButton;
    private AppDatabase database;
    private int orderId;
    private ArrayList<Integer> selectedDetailIds;
    private List<OrderDetail> selectedDetails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        database = AppDatabase.getDatabase(this);

        orderDetailListView = findViewById(R.id.checkoutDetailListView);
        totalAmountTextView = findViewById(R.id.checkoutTotalTextView);
        payButton = findViewById(R.id.payButton);
        backButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        orderId = intent.getIntExtra("order_id", 0);
        selectedDetailIds = intent.getIntegerArrayListExtra("selected_detail_ids");

        loadSelectedItems();

        payButton.setOnClickListener(v -> processPayment());
        backButton.setOnClickListener(v -> finish());
    }

    private void loadSelectedItems() {
        new Thread(() -> {
            List<OrderDetail> allDetails = database.orderDetailDao().getOrderDetailsByOrder(orderId);
            selectedDetails.clear();
            double total = 0;
            List<String> itemList = new ArrayList<>();

            for (OrderDetail detail : allDetails) {
                if (selectedDetailIds.contains(detail.id)) {
                    selectedDetails.add(detail);
                    Product product = database.productDao().getProductById(detail.productId);
                    if (product != null) {
                        itemList.add(product.name + " x" + detail.quantity + " - " + PriceFormatter.format(detail.price * detail.quantity));
                        total += detail.price * detail.quantity;
                    }
                }
            }

            double finalTotal = total;
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
                orderDetailListView.setAdapter(adapter);
                totalAmountTextView.setText("Tổng cộng thanh toán: " + PriceFormatter.format(finalTotal));
            });
        }).start();
    }

    private void processPayment() {
        new Thread(() -> {
            Order originalOrder = database.orderDao().getOrderById(orderId);
            if (originalOrder == null) return;

            // Create a NEW Paid order
            Order paidOrder = new Order(originalOrder.userId, java.time.LocalDate.now().toString(), "Paid", 0);
            double total = 0;
            for (OrderDetail detail : selectedDetails) {
                total += detail.price * detail.quantity;
            }
            paidOrder.totalAmount = total;
            long newOrderId = database.orderDao().insert(paidOrder);

            // Move selected details to the new order
            for (OrderDetail detail : selectedDetails) {
                detail.orderId = (int) newOrderId;
                database.orderDetailDao().update(detail);
            }

            // Update total of original pending order (it might still have other items)
            List<OrderDetail> remainingDetails = database.orderDetailDao().getOrderDetailsByOrder(orderId);
            double remainingTotal = 0;
            for (OrderDetail d : remainingDetails) {
                remainingTotal += d.price * d.quantity;
            }
            originalOrder.totalAmount = remainingTotal;
            database.orderDao().update(originalOrder);

            runOnUiThread(() -> {
                Intent intent = new Intent(CheckoutActivity.this, OrderConfirmationActivity.class);
                intent.putExtra("order_id", (int) newOrderId);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}
