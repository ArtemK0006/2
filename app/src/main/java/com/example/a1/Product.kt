package com.example.a1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val name: String,
    val imageResId: Int,
    val goal: Int,
    val promoCode: String
)