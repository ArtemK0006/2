package com.example.a1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// 1. В список entities добавлен Product::class через запятую после User::class
// 2. version увеличен до 2 (обязательно при добавлении новой таблицы!)
// 3. exportSchema = false убирает предупреждение про схему
@Database(entities = [User::class, Product::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Абстрактный метод для получения DAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "my_database" // Имя файла базы данных
                )
                    // если версия базы изменилась (с 1 на 2),
                    // она удалит старую базу и создаст новую с таблицей products.
                    // Для учебного проекта это самый простой способ избежать ошибок миграции.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}