package com.example.a1

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Версия 7 (увеличили, чтобы сбросить кэш)
@Database(entities = [Product::class, User::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d("DB_DEBUG", "🚀 Создание НОВОЙ чистой базы...")

                // МЕНЯЕМ ИМЯ БАЗЫ СНОВА, чтобы точно создать новую
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_shop_database_FINAL_CLEAN_V7"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("DB_DEBUG", "✅ База создана. Начинаем заполнение...")

                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val dao = database.productDao()


                                        dao.deleteAll()

                                        val products = listOf(
                                            Product(name = "Яйцо шоколадное с подарком - 4% Скидка!", imageResId = R.drawable.b, goal = 100, promoCode = "DESSERT100"),
                                            Product(name = "Напиток сывороточный молочный с соком клубника-лайм-мята - 6% Скидка!", imageResId = R.drawable.c, goal = 500, promoCode = "DRINK500"),
                                            Product(name = "Сорбет Гранат - 9% Скидка!", imageResId = R.drawable.d, goal = 1000, promoCode = "COLLECTED1K"),
                                            Product(name = "Ягодный пунш - 14% Скидка!", imageResId = R.drawable.e, goal = 3000, promoCode = "PUNCH3K"),
                                            Product(name = "Напиток кокосовый: ваниль - 16% Скидка!", imageResId = R.drawable.s, goal = 7000, promoCode = "VANILLA7K"),
                                            Product(name = "Ролл сливочный с огурцом - 21% Скидка!", imageResId = R.drawable.f, goal = 10000, promoCode = "ROLL10K"),
                                            Product(name = "Ролл с вялеными томатами - 23% Скидка!", imageResId = R.drawable.q, goal = 15000, promoCode = "ROLL15K"),
                                            Product(name = "Молоко растительное Green Milk с кокосом - 18% Скидка!", imageResId = R.drawable.y, goal = 18000, promoCode = "Milk18K"),
                                            Product(name = "Энергетик Маракуйя - 25% Скидка!", imageResId = R.drawable.u, goal = 20000, promoCode = "Energetit25K"),
                                            Product(name = "Раф соленая карамель - 50% Скидка!", imageResId = R.drawable.g, goal = 50000, promoCode = "RAF50K")
                                        )

                                        dao.insertAll(products)

                                        val count = dao.getAllProducts().size
                                        Log.d("DB_DEBUG", " Готово! В базе теперь ровно $count товаров.")

                                    } catch (e: Exception) {
                                        Log.e("DB_DEBUG", " Ошибка: ${e.message}")
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}