package com.example.fruit_app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fruit_app.entity.Order;

import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insert(Order order);

    @Update
    void update(Order order);

    @Delete
    void delete(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getOrderById(int id);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> getOrdersByUser(int userId);

    @Query("SELECT * FROM orders WHERE userId = :userId AND status = :status")
    Order getOrderByUserAndStatus(int userId, String status);

    @Query("SELECT * FROM orders")
    List<Order> getAllOrders();
}
