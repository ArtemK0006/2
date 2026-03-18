package com.example.a1

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings) // ← ЭТА СТРОКА ОБЯЗАТЕЛЬНА!

        val exportBtn = findViewById<Button>(R.id.exportBtn)
        val importBtn = findViewById<Button>(R.id.importBtn)
        val backBtn = findViewById<Button>(R.id.backBtn)

        exportBtn.setOnClickListener {
            try {
                val count = intent.getIntExtra("clickCount", getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getInt("clickCount", 0))
                openFileOutput("clicker_backup.txt", Context.MODE_PRIVATE).use { out ->
                    out.write(count.toString().toByteArray())
                }
                Toast.makeText(this, "Прогресс сохранён", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка экспорта", Toast.LENGTH_SHORT).show()
            }
        }

        importBtn.setOnClickListener {
            try {
                openFileInput("clicker_backup.txt").use { input ->
                    val data = input.reader().readText()
                    val count = data.toInt()
                    getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        .edit().putInt("clickCount", count).apply()
                    Toast.makeText(this, "Прогресс восстановлен: $count кликов", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
            }
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}