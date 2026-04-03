package com.example.fruit_app.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders",
    foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;
    public String orderDate;
    public String status; // "Pending", "Paid", "Delivered", etc.
    public double totalAmount;

    public Order(int userId, String orderDate, String status, double totalAmount) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Order() {
    }
}
