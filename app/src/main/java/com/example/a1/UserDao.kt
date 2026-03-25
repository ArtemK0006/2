package com.example.a1

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // --- Методы для Пользователей ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // --- Методы для Товаров ---
    @Query("SELECT * FROM products ORDER BY goal ASC")
    suspend fun getAllProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductsCount(): Int
}