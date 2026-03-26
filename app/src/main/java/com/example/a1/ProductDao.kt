package com.example.a1

import androidx.room.*

@Dao
interface ProductDao {
    // --- Методы для Товаров ---

    @Query("SELECT * FROM products ORDER BY goal ASC")
    suspend fun getAllProducts(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}