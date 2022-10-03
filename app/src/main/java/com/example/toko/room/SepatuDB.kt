package com.example.toko.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class,Buy::class],
    version = 1
)

abstract class SepatuDB: RoomDatabase() {
    abstract fun userDao() : UserDao
    abstract fun buyDao() : BuyDao

    companion object {
        @Volatile private var instance : SepatuDB? = null
        private val LOCK = Any()
        operator fun invoke (context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, SepatuDB::class.java, "sepatu.db"
        ).build()
    }
}