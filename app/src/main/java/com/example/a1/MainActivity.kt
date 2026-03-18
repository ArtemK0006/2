package com.example.a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var count = 0
    private lateinit var textCounter: TextView
    private val PREFS_NAME = "MyPrefs"
    private val COUNT_KEY = "clickCount"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textCounter = findViewById(R.id.textCounter)
        val buttonClick = findViewById<Button>(R.id.buttonClick)
        val buttonGoToSecond = findViewById<Button>(R.id.buttonGoToSecond)
        val buttonSettings = findViewById<Button>(R.id.buttonSettings)

        loadCount()

        buttonClick.setOnClickListener {
            count++
            textCounter.text = count.toString()
            saveCount()
        }

        buttonGoToSecond.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("clickCount", count)
            startActivityForResult(intent, 100)
        }

        buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val shouldReset = data?.getBooleanExtra("shouldReset", false) == true
            if (shouldReset) {
                count = 0
                saveCount()
                textCounter.text = "0"
                Toast.makeText(this, "Счётчик сброшен!", Toast.LENGTH_SHORT).show()
            }
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