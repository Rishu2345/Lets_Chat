package com.example.letschat

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(task: Messages)

    @Query("Select * from message_table where chatId = :chatId")
    fun getAllMessages(chatId :String): Flow<List<Messages>>



    @Query("Delete from message_table where chatId = :chatId")
    suspend fun deleteAllChat(chatId:String)
}
@Database(entities = [Messages::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habit_tracker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

