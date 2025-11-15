package com.example.letschat

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


data class GoogleSignInResult(
    val userId: String?,
    val userName: String?,
    val userEmail: String?,
    val profilePicture: String?
)

data class Friend(
    val id: String,
    val name: String,
    val unseenMessages: Int,
    val profilePicture: String?,
    val lastMsg: String,
    val lastMsgTime: LocalDateTime,
    val chatId: String,
    val email:String
)

object MessageType{
    val SENT = "sent"
    val RECEIVED = "Received"
    val SYSTEM = "System"
}


//Entities
data class Profile(
    val name:String,
    val email:String,
    val profilePicture:String,
    val uniqueId:String
)

@Entity(tableName = "message_table")
data class Messages(
    @PrimaryKey val id:String,
    val  chatId:String,
    val text: String,
    val type: String,
    val time: Long
)