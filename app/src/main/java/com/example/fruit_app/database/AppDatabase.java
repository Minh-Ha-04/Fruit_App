package com.example.fruit_app.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.fruit_app.dao.CategoryDao;
import com.example.fruit_app.dao.OrderDao;
import com.example.fruit_app.dao.OrderDetailDao;
import com.example.fruit_app.dao.ProductDao;
import com.example.fruit_app.dao.UserDao;
import com.example.fruit_app.entity.Category;
import com.example.fruit_app.entity.Order;
import com.example.fruit_app.entity.OrderDetail;
import com.example.fruit_app.entity.Product;
import com.example.fruit_app.entity.User;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "fruit_app_database")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new Thread(() -> {
                UserDao userDao = INSTANCE.userDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();

                // Kiểm tra nếu chưa có user thì mới thêm
                if (userDao.getAllUsers().isEmpty()) {
                    userDao.insert(new User("user1", "password1", "user1@example.com", "Nguyễn Văn A"));
                    userDao.insert(new User("user2", "password2", "user2@example.com", "Trần Thị B"));
                    userDao.insert(new User("admin", "admin123", "admin@example.com", "Admin"));
                }

                // Kiểm tra và thêm category/product nếu trống
                if (categoryDao.getAllCategories().isEmpty()) {
                    long cat1Id = categoryDao.insert(new Category("Trái cây tươi", "Trái cây tươi ngon"));
                    long cat2Id = categoryDao.insert(new Category("Trái cây nhập khẩu", "Trái cây cao cấp"));
                    long cat3Id = categoryDao.insert(new Category("Rau xanh", "Rau sạch mỗi ngày"));

                    productDao.insert(new Product("Táo tươi", "Táo giàu vitamin", 25000, (int) cat1Id, 50, "img1"));
                    productDao.insert(new Product("Cam tươi", "Cam mọng nước", 20000, (int) cat1Id, 50, "img2"));
                    productDao.insert(new Product("Dâu tây", "Dâu tây Đà Lạt", 50000, (int) cat2Id, 20, "img5"));
                }
            }).start();
        }
    };
}
