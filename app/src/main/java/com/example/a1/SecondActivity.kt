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

    companion object {
        val cartItems = mutableListOf<CartCoupon>()
    }

    private lateinit var database: AppDatabase
    private lateinit var dao: ProductDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Инициализация БД
        database = AppDatabase.getDatabase(this)
        dao = database.productDao()

        val clickCount = intent.getIntExtra("clickCount", 0)


        val containerIds = listOf(
            R.id.item1, R.id.item2, R.id.item3,
            R.id.item4, R.id.item5, R.id.item6,
            R.id.item7, R.id.item8, R.id.item9, R.id.item10

        )

        // Загрузка данных ИЗ БАЗЫ ROOM
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Читаем данные из базы (это асинхронно)
                val productsFromDb = withContext(Dispatchers.IO) {
                    dao.getAllProducts()
                }

                if (productsFromDb.isEmpty()) {
                    Toast.makeText(this@SecondActivity, "База пуста! Проверь AppDatabase.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SecondActivity, "Загружено товаров: ${productsFromDb.size}", Toast.LENGTH_SHORT).show()
                }

                // Отображаем товары на экране
                displayProducts(productsFromDb, clickCount, containerIds)

            } catch (e: Exception) {
                Toast.makeText(this@SecondActivity, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        // Кнопка Назад
        findViewById<Button>(R.id.buttonBack)?.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun displayProducts(products: List<Product>, clickCount: Int, containerIds: List<Int>) {
        // Проходимся по всем товарам из базы
        for (i in products.indices) {
            // Если товаров в базе больше, чем контейнеров в XML - останавливаемся
            if (i >= containerIds.size) {
                Toast.makeText(this, "Внимание: товаров больше, чем мест на экране!", Toast.LENGTH_LONG).show()
                break
            }

            val product = products[i]

            // Безопасно находим контейнер
            val container = findViewById<View?>(containerIds[i]) ?: continue

            val image = container.findViewById<ImageView?>(R.id.imageDish)
            val name = container.findViewById<TextView?>(R.id.textDishName)
            val goalText = container.findViewById<TextView?>(R.id.textDishGoal)
            val actionBtn = container.findViewById<Button?>(R.id.buttonAction)

            if (image == null || name == null || goalText == null || actionBtn == null) continue

            // ЗАПОЛНЯЕМ ДАННЫМИ ИЗ БАЗЫ
            image.setImageResource(product.imageResId)
            name.text = product.name

            if (clickCount >= product.goal) {
                goalText.text = "Доступно!"
                goalText.setTextColor(getColor(android.R.color.holo_green_dark))
                actionBtn.text = "Забрать"
                actionBtn.visibility = View.VISIBLE

                actionBtn.setOnClickListener {
                    val coupon = CartCoupon(
                        id = System.currentTimeMillis().toInt(),
                        productName = product.name,
                        promoCode = product.promoCode,
                        qrData = product.promoCode,
                        imageResId = product.imageResId
                    )
                    cartItems.add(coupon)
                    Toast.makeText(this, "${product.name} в корзине!", Toast.LENGTH_SHORT).show()

                    val newCount = clickCount - product.goal
                    val resultIntent = Intent()
                    resultIntent.putExtra("newClickCount", newCount)
                    setResult(RESULT_OK, resultIntent)
                    finish()
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