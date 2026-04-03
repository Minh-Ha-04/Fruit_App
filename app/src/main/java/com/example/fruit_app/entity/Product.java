package com.example.fruit_app.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "products",
    foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "categoryId"))
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public double price;
    public int categoryId;
    public int quantity;
    public String imageUrl;

    public Product(String name, String description, double price, int categoryId, int quantity, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public Product() {
    }
}
