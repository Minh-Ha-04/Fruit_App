package com.example.fruit_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fruit_app.R;
import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.OrderDetail;
import com.example.fruit_app.entity.Product;
import com.example.fruit_app.util.PriceFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends ArrayAdapter<OrderDetail> {
    private final AppDatabase database;
    private final Map<Integer, Product> productMap = new HashMap<>();
    private final Map<Integer, Boolean> selectedItems = new HashMap<>();
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public OrderAdapter(@NonNull Context context, @NonNull List<OrderDetail> objects, OnCartChangedListener listener) {
        super(context, 0, objects);
        this.database = AppDatabase.getDatabase(context);
        this.listener = listener;
        for (OrderDetail detail : objects) {
            selectedItems.put(detail.id, true);
        }
    }

    public Map<Integer, Boolean> getSelectedItems() {
        return selectedItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order_detail, parent, false);
        }

        OrderDetail detail = getItem(position);
        CheckBox checkBox = convertView.findViewById(R.id.itemCheckBox);
        ImageView imageView = convertView.findViewById(R.id.productImageView);
        TextView nameTextView = convertView.findViewById(R.id.productNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.productPriceTextView);
        TextView quantityTextView = convertView.findViewById(R.id.quantityTextView);
        Button minusButton = convertView.findViewById(R.id.minusButton);
        Button plusButton = convertView.findViewById(R.id.plusButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        checkBox.setChecked(Boolean.TRUE.equals(selectedItems.get(detail.id)));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedItems.put(detail.id, isChecked);
            if (listener != null) listener.onCartChanged();
        });

        quantityTextView.setText(String.valueOf(detail.quantity));

        // Load product data if not already in map
        if (!productMap.containsKey(detail.productId)) {
            new Thread(() -> {
                Product product = database.productDao().getProductById(detail.productId);
                productMap.put(detail.productId, product);
                ((android.app.Activity) getContext()).runOnUiThread(() -> notifyDataSetChanged());
            }).start();
        } else {
            Product product = productMap.get(detail.productId);
            if (product != null) {
                nameTextView.setText(product.name);
                priceTextView.setText(PriceFormatter.format(detail.price));
                int resId = getContext().getResources().getIdentifier(product.imageUrl, "drawable", getContext().getPackageName());
                if (resId != 0) {
                    imageView.setImageResource(resId);
                } else {
                    imageView.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }
        }

        minusButton.setOnClickListener(v -> {
            if (detail.quantity > 1) {
                detail.quantity--;
                updateDetail(detail);
            }
        });

        plusButton.setOnClickListener(v -> {
            detail.quantity++;
            updateDetail(detail);
        });

        deleteButton.setOnClickListener(v -> {
            new Thread(() -> {
                database.orderDetailDao().delete(detail);
                ((android.app.Activity) getContext()).runOnUiThread(() -> {
                    remove(detail);
                    selectedItems.remove(detail.id);
                    if (listener != null) listener.onCartChanged();
                });
            }).start();
        });

        return convertView;
    }

    private void updateDetail(OrderDetail detail) {
        new Thread(() -> {
            database.orderDetailDao().update(detail);
            ((android.app.Activity) getContext()).runOnUiThread(() -> {
                notifyDataSetChanged();
                if (listener != null) listener.onCartChanged();
            });
        }).start();
    }
}
