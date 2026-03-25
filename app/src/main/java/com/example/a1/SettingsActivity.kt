package com.example.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream
import java.io.FileInputStream

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val exportBtn = findViewById<Button>(R.id.exportBtn)
        val importBtn = findViewById<Button>(R.id.importBtn)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val btnBack = findViewById<Button>(R.id.backBtn)

        // Экспорт
        exportBtn.setOnClickListener {
            try {
                val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val count = prefs.getInt("clickCount", 0)
                openFileOutput("clicker_backup.txt", Context.MODE_PRIVATE).use { out ->
                    out.write(count.toString().toByteArray())
                }
                Toast.makeText(this, "Прогресс сохранён в файл", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка экспорта", Toast.LENGTH_SHORT).show()
            }
        }

        // Импорт
        importBtn.setOnClickListener {
            try {
                openFileInput("clicker_backup.txt").use { input ->
                    val data = input.reader().readText()
                    val count = data.toInt()
                    val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    prefs.edit().putInt("clickCount", count).apply()
                    Toast.makeText(this, "Прогресс восстановлен: $count кликов", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show()
            }
        }

        // ВЫХОД ИЗ АККАУНТА
        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            val prefsClicks = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefsClicks.edit().clear().apply()

            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}