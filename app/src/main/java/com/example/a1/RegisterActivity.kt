package com.example.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    // Имя файла для хранения данных
    private val PREFS_NAME = "UserPrefs"
    private val KEY_LOGIN = "saved_login"
    private val KEY_PASS = "saved_pass"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editEmail = findViewById<EditText>(R.id.editRegEmail)
        val editUsername = findViewById<EditText>(R.id.editRegUsername)
        val editPassword = findViewById<EditText>(R.id.editRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val textGoToLogin = findViewById<TextView>(R.id.textGoToLogin)

        btnRegister.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val username = editUsername.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                //  Сохраняет данные в память телефона
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString(KEY_LOGIN, email) // Сохраняем email как логин
                editor.putString(KEY_PASS, password) // Сохраняем пароль
                editor.apply()

                Toast.makeText(this, "Аккаунт создан! Теперь войдите.", Toast.LENGTH_LONG).show()

                // Переход на экран входа
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            }
        }

        textGoToLogin.setOnClickListener {
            finish()
        }
    }
}