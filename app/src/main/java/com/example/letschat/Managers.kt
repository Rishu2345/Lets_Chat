package com.example.letschat



import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class DeleteOldMessagesWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    val database = FirebaseDatabase
        .getInstance(context.getString(R.string.Database_Url))
    override suspend fun doWork(): Result {
        Log.d("DeleteOldMessagesWorker", "Deleting old messages")

        val chatsRef = database.getReference("chats")

        val currentTime = System.currentTimeMillis()
        val oneHourMillis = TimeUnit.HOURS.toMillis(1)

        val chatsSnapshot = chatsRef.get().await()
        for (chatSnapshot in chatsSnapshot.children) {
            for (msgSnapshot in chatSnapshot.children) {
                val timestamp = msgSnapshot.child("timestamp").getValue(Long::class.java)
                if (timestamp != null && (currentTime - timestamp) >= oneHourMillis) {
                    msgSnapshot.ref.removeValue()
                }
            }
        }

        return Result.success()
    }
}


fun scheduleMessageCleanup(context: Context) {
    Log.d("DeleteOldMessagesWorker", "Scheduling message cleanup")
    val workRequest = PeriodicWorkRequestBuilder<DeleteOldMessagesWorker>(
        1, TimeUnit.HOURS
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "delete_old_messages",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}
