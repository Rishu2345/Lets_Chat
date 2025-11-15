package com.example.letschat

import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class FirestoreFunction(viewModel: MainViewModel) {
    private val vw = viewModel
    val firestore = FirebaseFirestore.getInstance()
    fun storeUserInFirestore(user: GoogleSignInResult) {
        Log.d("FireStore","Storing $user")
        val uniqueId = vw.generateUniqueCode()
        val userRef = firestore.collection("users").document(user.userId.toString())

        val userData = mapOf(
            "Id" to uniqueId,
            "userName" to user.userName,
            "profilePicture" to (user.profilePicture ?: "") ,
            "status" to "Offline",
            "bio" to "",
            "friends" to mapOf<String,String>() ,
            "createdAt" to System.currentTimeMillis(),
            "lastSeen" to System.currentTimeMillis(),
            "email" to user.userId
        )

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                userRef.set(userData)
                    .addOnSuccessListener { Log.d("Firestore", "User added successfully!") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error adding user", e) }
            } else {
                Log.d("Firestore", "User already exists")

            }
        }
    }
    private fun updateLastSeen(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("lastSeen", System.currentTimeMillis())
            .addOnSuccessListener { Log.d("Firestore", "Last seen updated successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating last seen", e) }
    }

    private fun updateStatus(userId: String, status: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("status", status)
            .addOnSuccessListener { Log.d("Firestore", "Status updated successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating status", e) }

    }
    fun addFriend(userId: String, friendId: String, callback: (String?) -> Unit) {
        val userRef = firestore.collection("users").document(userId)
        val friendRef = firestore.collection("users").document(friendId)
        val chatId = "${userId.split("@")[0]}-${friendId.split("@")[0]}"

        val userField = FieldPath.of("friends", friendId)
        val friendField = FieldPath.of("friends", userId)

        val userTask = userRef.update(userField, chatId)
        val friendTask = friendRef.update(friendField, chatId)

        Tasks.whenAllSuccess<Void>(userTask, friendTask)
            .addOnSuccessListener {
                Log.d("Firestore", "Friend added successfully for both users!")
                callback(chatId)
            }
            .addOnFailureListener {
                Log.e("Firestore", "Error adding friend", it)
                callback(null)
            }
    }


    fun updateProfileName(userId: String, newName: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("userName", newName)
            .addOnSuccessListener { Log.d("Firestore", "Profile name updated successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating profile name", e) }

    }

    fun updateProfilePicture(userId: String, newPicture: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("profilePicture", newPicture)
            .addOnSuccessListener { Log.d("Firestore", "Profile picture updated successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating profile picture", e) }
    }

    fun updateBio(userId: String, newBio: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("bio", newBio)
            .addOnSuccessListener { Log.d("Firestore", "Bio updated successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating bio", e) }

    }

    fun getFriendsInfoCallback(
        userId: String,
        callback: (List<Friend>) -> Unit,
        onError: ((Exception) -> Unit)? = null
    ) {
        val userRef = firestore.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { userDoc ->
                if (!userDoc.exists()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                val friendsMap = userDoc.get("friends") as? Map<*, *> ?: emptyMap<Any, Any>()
                Log.d("ff","Friends Map $friendsMap")
                val friendIds = friendsMap.keys.map { it.toString() }
                Log.d("ff","Friend Ids $friendIds")
                if (friendIds.isEmpty()) {
                    callback(emptyList()); return@addOnSuccessListener
                }

                // prepare tasks
                val tasks = friendIds.map { fid ->
                    firestore.collection("users").document(fid).get()
                }
                Log.d("ff","Tasks $tasks")

                //here the task shows but the it does not return any friends
                // wait for all document gets to succeed
                Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
                    .addOnSuccessListener { snapshots ->
                        Log.d("ff", "We are in the success listener")
                        val friends = snapshots.mapNotNull { doc ->
                            if (!doc.exists()) return@mapNotNull null
                            Log.d("ff","document exist this does not get logged")
                            val data = doc.data ?: return@mapNotNull null
                            Log.d("ff","document does not have data")
                            Log.d("ff", "We are in the second stage of success listener this does not get Logged")
                            val rawId = data["Id"]

                            val docId = doc.id
                            Log.d("ff","This is $docId")
                            Friend(
                                id = rawId as? String ?: "",
                                name = data["userName"] as? String ?: "",
                                unseenMessages = 0,
                                profilePicture = data["profilePicture"] as? String ?: "",
                                lastMsg = "",
                                lastMsgTime = LocalDateTime.now(),
                                chatId = friendsMap[docId] as? String ?: "",
                                email = data["email"] as? String ?: ""
                            )

                        }
                        callback(friends)
                    }
                    .addOnFailureListener { e ->
                        onError?.invoke(e)
                    }
            }
            .addOnFailureListener { e ->
                onError?.invoke(e)
            }
    }

    fun findUserIdByUniqueCode(uniqueCode: String, callback: (String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("Id", uniqueCode)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val foundUserId = result.documents[0].id
                    callback(foundUserId)
                } else {
                    callback(null)  // No user with that code
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error finding user by code", e)
                callback(null)
            }
    }

    fun fetchProfile(userId:String,success:(Profile)->Unit,failure:(Exception)->Unit){
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc->
                if(!userDoc.exists()){
                    failure(Exception("User Not Found"))
                }else{
                    val data = userDoc.data
                    val profile = Profile(
                        name = data?.get("userName") as? String ?: "",
                        email = data?.get("email") as? String ?: "",
                        profilePicture = data?.get("profilePicture") as? String ?: "",
                        uniqueId = data?.get("Id") as? String ?: ""
                    )
                    success(profile)
                }

            }

    }

    fun removeFriend(userId: String, friendId: String, callback: (Boolean) -> Unit) {
        val userRef = firestore.collection("users").document(userId)
        val friendRef = firestore.collection("users").document(friendId)

        Log.d("RemoveFriend", "Starting removeFriend() for userId=$userId, friendId=$friendId")

        userRef.get().addOnSuccessListener { userDoc ->
            if (userDoc.exists()) {
                Log.d("RemoveFriend", "User document found for $userId")

                val friendsMap = userDoc.get("friends") as? Map<*, *> ?: emptyMap<Any, Any>()
                Log.d("RemoveFriend", "Current friends of $userId: $friendsMap")

                val updateMap = friendsMap - friendId
                Log.d("RemoveFriend", "Updated map after removing $friendId: $updateMap")

                // Update user’s friend list
                userRef.update("friends", updateMap)
                    .addOnSuccessListener {
                        Log.d("RemoveFriend", "Successfully updated $userId’s friends list")

                        // Now remove the user from the friend's friend list
                        friendRef.get().addOnSuccessListener { friendDoc ->
                            if (friendDoc.exists()) {
                                Log.d("RemoveFriend", "Friend document found for $friendId")

                                val friendMap = friendDoc.get("friends") as? Map<*, *> ?: emptyMap<Any, Any>()
                                Log.d("RemoveFriend", "Current friends of $friendId: $friendMap")

                                val updatedFriendMap = friendMap - userId
                                Log.d("RemoveFriend", "Updated map after removing $userId: $updatedFriendMap")

                                friendRef.update("friends", updatedFriendMap)
                                    .addOnSuccessListener {
                                        Log.d("RemoveFriend", "Successfully removed $userId from $friendId’s friend list")
                                        callback(true)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RemoveFriend", "Failed to update $friendId’s friend list: ${e.message}")
                                        callback(false)
                                    }
                            } else {
                                Log.w("RemoveFriend", "Friend document ($friendId) does not exist")
                                callback(false)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("RemoveFriend", "Failed to update $userId’s friend list: ${e.message}")
                        callback(false)
                    }
            } else {
                Log.w("RemoveFriend", "User document ($userId) does not exist")
                callback(false)
            }
        }.addOnFailureListener { e ->
            Log.e("RemoveFriend", "Error fetching user document for $userId: ${e.message}")
            callback(false)
        }
    }



}