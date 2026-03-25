package com.example.a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Имя файла должно совпадать с тем, что в RegisterActivity
    private val PREFS_NAME = "UserPrefs"
    private val KEY_LOGIN = "saved_login"
    private val KEY_PASS = "saved_pass"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textGoToRegister = findViewById<TextView>(R.id.textGoToRegister)

        btnLogin.setOnClickListener {
            val inputLogin = editEmail.text.toString().trim()
            val inputPass = editPassword.text.toString().trim()

            if (inputLogin.isEmpty() || inputPass.isEmpty()) {
                Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  сохраненые данные
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedLogin = prefs.getString(KEY_LOGIN, "")
            val savedPass = prefs.getString(KEY_PASS, "")

            //  Сравниввае введые данные
            if (inputLogin == savedLogin && inputPass == savedPass) {
                Toast.makeText(this, "Вход успешен! Добро пожаловать, $inputLogin", Toast.LENGTH_SHORT).show()

                // Переход на главный экран (Кликер)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                //  ЕСЛИ НЕ СОВПАДАЮТ
                Toast.makeText(this, "Неверный логин или пароль!", Toast.LENGTH_LONG).show()

                // android.util.Log.d("DEBUG", "Правильный логин: $savedLogin, Пароль: $savedPass")
            }
        }

        textGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}