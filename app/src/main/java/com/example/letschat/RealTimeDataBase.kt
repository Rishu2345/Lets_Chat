package com.example.letschat

import android.content.Context
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.getValue

class RealTimeDatabaseFunction(context: Context) {

    val seenIds = mutableSetOf<String>()

    private val database = FirebaseDatabase
        .getInstance(context.getString(R.string.Database_Url))
        .reference

    // Create or get existing chat session
    fun createOrGetChatSession(chatId:String, userId1: String, onResult: (String?) -> Unit) {

        val chatRef = database.child("chats").child(chatId)
        chatRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Chat already exists
                onResult(chatId)
            } else {
                // Create new chat
                val chatData = mapOf(
                    "sendBy" to userId1,
                    "lastMessage" to "",
                    "timestamp" to ServerValue.TIMESTAMP
                )
                chatRef.setValue(chatData)
                    .addOnSuccessListener { onResult(chatId) }
                    .addOnFailureListener { onResult(null) }
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }

    // Send a message in a chat
    fun sendMessage(chatId: String, senderId: String, text: String, onSuccess: () -> Unit) {
        val messageRef = database.child("chats")
            .child(chatId)
            .push()
        val msgData = mapOf(
            "sender" to senderId,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        messageRef.setValue(msgData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {
                Log.e("Chat", "Failed to send message", it)
            }
    }

    // Listen for new messages in a chat
    fun listenForMessages(chatId: String, onNewMessage: (String , String, String, Long) -> Unit) {
        val messagesRef = database.child("chats")
            .child(chatId)
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildName: String?) {
                val id = snapshot.key ?: return
                if (!seenIds.add(id)) return
                Log.d("listen","added $seenIds")
                val sender = snapshot.child("sender").getValue<String>() ?: return
                val text = snapshot.child("text").getValue<String>() ?: return
                val timestamp = snapshot.child("timestamp").getValue<Long>() ?: return
                onNewMessage(id,sender, text, timestamp)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Chat", "Failed to read messages", error.toException())
            }
            override fun onChildChanged(snapshot: DataSnapshot, prevChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, prevChildName: String?) {}
        })
    }

    fun deleteChat(chatId: String){
        database.child("chats").child(chatId).removeValue()
        seenIds.clear()
    }
}

