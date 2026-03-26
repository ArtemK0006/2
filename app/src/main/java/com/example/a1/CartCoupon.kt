package com.example.a1

data class CartCoupon(
    val id: Int,              // Уникальный ID (время в миллисекундах)
    val productName: String,  // Название блюда
    val promoCode: String,    // Сам код
    val qrData: String,        // Данные для QR (тот же промокод)
    val imageResId: Int       // Картинка Товара
)