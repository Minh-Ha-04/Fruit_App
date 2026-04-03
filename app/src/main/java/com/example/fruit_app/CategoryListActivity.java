package com.example.fruit_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fruit_app.database.AppDatabase;
import com.example.fruit_app.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {

    private ListView listView;
    private AppDatabase database;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        listView = findViewById(R.id.categoryListView);
        database = AppDatabase.getDatabase(this);

        // Load categories
        new Thread(() -> {
            categoryList = database.categoryDao().getAllCategories();
            runOnUiThread(this::displayCategories);
        }).start();
    }

    private void displayCategories() {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoryList) {
            categoryNames.add(category.name + "\n" + category.description);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Category selectedCategory = categoryList.get(position);
            Intent intent = new Intent(CategoryListActivity.this, ProductListActivity.class);
            intent.putExtra("category_id", selectedCategory.id);
            intent.putExtra("category_name", selectedCategory.name);
            startActivity(intent);
        });
    }
}
