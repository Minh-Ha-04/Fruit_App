package com.example.fruit_app.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details",
    foreignKeys = {
        @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "orderId"),
        @ForeignKey(entity = Product.class, parentColumns = "id", childColumns = "productId")
    })
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int orderId;
    public int productId;
    public int quantity;
    public double price; // Price at the time of order

    public OrderDetail(int orderId, int productId, int quantity, double price) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderDetail() {
    }
}
