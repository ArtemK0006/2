package com.example.a1

import androidx.room.*

@Dao
interface UserDao {
    // --- Методы для Пользователей ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    // Метод для выхода (опционально)
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}