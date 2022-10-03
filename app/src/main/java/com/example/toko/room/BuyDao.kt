package com.example.toko.room
import androidx.room.*

@Dao
interface BuyDao {
    @Insert
    suspend fun addBuy(buy: Buy)

    @Update
    suspend fun updateBuy(buy: Buy)

    @Delete
    suspend fun deleteBuy(buy: Buy)

    @Query("SELECT * FROM buy")
    suspend fun getBuy() : List<Buy>

    @Query("SELECT * FROM buy WHERE id =:buy_id")
    suspend fun getBuy(buy_id: Int) : List<Buy>
}