package com.example.a1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private data class Dish(
        val name: String,
        val imageRes: Int,
        val goal: Int,
        val promo: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val clickCount = intent.getIntExtra("clickCount", 0)

        val dishes = listOf(
            Dish("Яйцо шоколадное с подарком - 30% Скидка!", R.drawable.b, 100, "DESSERT100"),
            Dish("Напиток сывороточный молочный с соком клубника-лайм-мята - 45% Скидка!", R.drawable.c, 500, "DRINK500"),
            Dish("Сорбет Гранат - 50% Скидка!", R.drawable.d, 1000, "COLLECTED1K"),
            Dish("Ягодный пунш - 25% Скидка!", R.drawable.e, 3000, "PUNCH3K"),
            Dish("Ролл сливочный с огурцом - 35% Скидка!", R.drawable.f, 10000, "ROLL10K"),
            Dish("Раф соленая карамель - 70% Скидка!", R.drawable.g, 50000, "RAF50K")
        )

        val itemIds = listOf(
            R.id.item1, R.id.item2, R.id.item3,
            R.id.item4, R.id.item5, R.id.item6
        )

        for (i in dishes.indices) {
            val container = findViewById<View>(itemIds[i])
            val image = container.findViewById<ImageView>(R.id.imageDish)
            val name = container.findViewById<TextView>(R.id.textDishName)
            val goalText = container.findViewById<TextView>(R.id.textDishGoal)
            val promoText = container.findViewById<TextView>(R.id.textPromoCode)
            val actionBtn = container.findViewById<Button>(R.id.buttonAction)

            val dish = dishes[i]
            image.setImageResource(dish.imageRes)
            name.text = dish.name

            if (clickCount >= dish.goal) {
                goalText.text = "Готово! Нажми «Получить»"
                goalText.setTextColor(getColor(android.R.color.holo_green_dark))
                actionBtn.text = "Получить"
                actionBtn.visibility = View.VISIBLE

                actionBtn.setOnClickListener {
                    // Копируем промокод
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Промокод", dish.promo)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(this, "Промокод скопирован!", Toast.LENGTH_SHORT).show()

                    // 👇 ГЛАВНОЕ: отправляем сигнал на сброс и закрываем экран
                    val resultIntent = Intent()
                    resultIntent.putExtra("shouldReset", true)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            } else {
                val need = dish.goal - clickCount
                goalText.text = "Нужно ещё: $need кликов"
                goalText.setTextColor(getColor(android.R.color.holo_red_dark))
                actionBtn.visibility = View.GONE
            }
        }

        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}