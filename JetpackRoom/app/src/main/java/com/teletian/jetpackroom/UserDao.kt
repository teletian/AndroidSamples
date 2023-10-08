package com.teletian.jetpackroom

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM USER")
    fun getAll(): List<User>

    @Query("SELECT * FROM USER WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM USER WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM USER")
    fun deleteAll()
}