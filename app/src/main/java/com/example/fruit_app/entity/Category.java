package com.example.fruit_app.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Category() {
    }
}
