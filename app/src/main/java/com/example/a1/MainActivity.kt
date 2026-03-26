package com.example.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private var count = 0
    private lateinit var textCounter: TextView
    private val PREFS_NAME = "MyPrefs"
    private val COUNT_KEY = "clickCount"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var btnCart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Настройка Меню ---
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //  Устанавливаем ширину
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val oneThirdWidth = (screenWidth * 0.60).toInt()

        val params = navView.layoutParams
        params.width = oneThirdWidth
        navView.layoutParams = params

        // Логика пунктов меню
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_backup -> {
                    Toast.makeText(this, "Резервное копирование...", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Настройки...", Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()
                    getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(this, "Выход из аккаунта", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        // --- Логика КОРЗИНЫ ---
        btnCart = findViewById(R.id.btnCartTop)
        updateCartCount()

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // --- Логика Кликера ---
        textCounter = findViewById(R.id.textCounter)
        val buttonClick = findViewById<Button>(R.id.buttonClick)
        val buttonGoToSecond = findViewById<Button>(R.id.buttonGoToSecond)

        loadCount()

        buttonClick.setOnClickListener {
            count++
            textCounter.text = count.toString()
            saveCount()
        }

        buttonGoToSecond.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("clickCount", count)
            // Используем startActivityForResult, чтобы получить результат (новые клики)
            startActivityForResult(intent, 100)
        }
    }

    // Обновляем счетчик корзины при возврате на экран
    override fun onResume() {
        super.onResume()
        updateCartCount()
    }

    //  ВЫЧИТАНИЕ КЛИКОВ
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Проверяем, пришло ли новое количество кликов
            if (data?.hasExtra("newClickCount") == true) {
                // Получаем новое число (старое минус цена товара)
                val newCount = data.getIntExtra("newClickCount", 0)
                count = newCount
                saveCount()
                textCounter.text = count.toString()
                Toast.makeText(this, "Купон получен! Осталось кликов: $count", Toast.LENGTH_SHORT).show()
            }
            // Запасной вариант: если вдруг пришел сигнал полного сброса
            else if (data?.getBooleanExtra("shouldReset", false) == true) {
                count = 0
                saveCount()
                textCounter.text = "0"
                Toast.makeText(this, "Счётчик сброшен", Toast.LENGTH_SHORT).show()
            }
        }
        // Обновляем кнопку корзины
        updateCartCount()
    }

    private fun updateCartCount() {
        val cartSize = SecondActivity.cartItems.size
        if (cartSize > 0) {
            btnCart.text = "🛒 $cartSize"
            btnCart.visibility = View.VISIBLE
        } else {
            btnCart.text = "🛒 0"
            btnCart.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun saveCount() {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(COUNT_KEY, count).apply()
    }

    private fun loadCount() {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        count = prefs.getInt(COUNT_KEY, 0)
        textCounter.text = count.toString()
    }
}