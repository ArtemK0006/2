package com.example.a1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SecondActivity : AppCompatActivity() {

    // Глобальный список корзины (доступен из MainActivity)
    companion object {
        val cartItems = mutableListOf<CartCoupon>()
    }

    private lateinit var database: AppDatabase
    private lateinit var dao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка: есть ли файл разметки
        try {
            setContentView(R.layout.activity_second)
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка загрузки экрана: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            finish()
            return
        }

        try {
            database = AppDatabase.getDatabase(this)
            dao = database.userDao()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка БД: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val clickCount = intent.getIntExtra("clickCount", 0)

        // ID контейнеров товаров
        val containerIds = listOf(
            R.id.item1, R.id.item2, R.id.item3,
            R.id.item4, R.id.item5, R.id.item6
        )

        // Загрузка товаров
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val products = withContext(Dispatchers.IO) {
                    dao.getAllProducts()
                }
                displayProducts(products, clickCount, containerIds)
            } catch (e: Exception) {
                Toast.makeText(this@SecondActivity, "Ошибка загрузки товаров: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Кнопка НАЗАД
        val btnBack = findViewById<Button>(R.id.buttonBack)
        btnBack?.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

    }

    private fun displayProducts(products: List<Product>, clickCount: Int, containerIds: List<Int>) {
        for (i in products.indices) {
            if (i >= containerIds.size) break

            val product = products[i]
            val container = findViewById<View>(containerIds[i])

            // Безопасное получение элементов внутри карточки
            val image = container.findViewById<ImageView?>(R.id.imageDish)
            val name = container.findViewById<TextView?>(R.id.textDishName)
            val goalText = container.findViewById<TextView?>(R.id.textDishGoal)
            val actionBtn = container.findViewById<Button?>(R.id.buttonAction)

            // Если хоть одного элемента нет — пропускаем эту карточку, чтобы не упасть
            if (image == null || name == null || goalText == null || actionBtn == null) {
                continue
            }

            image.setImageResource(product.imageResId)
            name.text = product.name

            if (clickCount >= product.goal) {
                goalText.text = "Доступно!"
                goalText.setTextColor(getColor(android.R.color.holo_green_dark))
                actionBtn.text = "В корзину"
                actionBtn.visibility = View.VISIBLE

                actionBtn.setOnClickListener {
                    // Добавляем в корзину
                    val coupon = CartCoupon(
                        id = System.currentTimeMillis().toInt(),
                        productName = product.name,
                        promoCode = product.promoCode,
                        qrData = product.promoCode
                    )
                    cartItems.add(coupon)

                    Toast.makeText(this, "${product.name} добавлен в корзину!", Toast.LENGTH_SHORT).show()

                    // goalText.text = "Добавлено"
                }
            } else {
                val need = product.goal - clickCount
                goalText.text = "Нужно ещё: $need кликов"
                goalText.setTextColor(getColor(android.R.color.holo_red_dark))
                actionBtn.visibility = View.GONE
            }
        }
    }
}