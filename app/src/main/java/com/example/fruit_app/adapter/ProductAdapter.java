package com.example.fruit_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fruit_app.R;
import com.example.fruit_app.entity.Product;
import com.example.fruit_app.util.PriceFormatter;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    public ProductAdapter(@NonNull Context context, @NonNull List<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        Product product = getItem(position);
        ImageView imageView = convertView.findViewById(R.id.productImageView);
        TextView nameTextView = convertView.findViewById(R.id.productNameTextView);
        TextView priceTextView = convertView.findViewById(R.id.productPriceTextView);
        TextView descTextView = convertView.findViewById(R.id.productDescriptionTextView);

        nameTextView.setText(product.name);
        priceTextView.setText(PriceFormatter.format(product.price));
        descTextView.setText(product.description);

        if (product.imageUrl != null && !product.imageUrl.isEmpty()) {
            int resId = getContext().getResources().getIdentifier(product.imageUrl, "drawable", getContext().getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        return convertView;
    }
}
