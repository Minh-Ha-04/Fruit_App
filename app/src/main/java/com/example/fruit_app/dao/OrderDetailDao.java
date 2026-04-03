package com.example.fruit_app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fruit_app.entity.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {

    @Insert
    void insert(OrderDetail orderDetail);

    @Update
    void update(OrderDetail orderDetail);

    @Delete
    void delete(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE id = :id")
    OrderDetail getOrderDetailById(int id);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getOrderDetailsByOrder(int orderId);

    @Query("SELECT * FROM order_details")
    List<OrderDetail> getAllOrderDetails();

    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    void deleteOrderDetailsByOrder(int orderId);
}
