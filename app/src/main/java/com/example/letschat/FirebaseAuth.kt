package com.example.letschat

import android.content.Context
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings


class GoogleAuthUIClient(private val context: Context , viewModel: MainViewModel){

    private val credentialManager = CredentialManager.create(context)
    val firestoreFunction = FirestoreFunction(viewModel)
    private fun createSignInRequest(): GetCredentialRequest{
        Log.d("signIn","Creating sign In Request")
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun signIn():GoogleSignInResult?{
        return try{
            Log.d("signIn","Signing In")
            val request = createSignInRequest()
            val result = credentialManager.getCredential(context,request)
            val user = handleSignIn(result)
            user?.let{
                firestoreFunction.storeUserInFirestore(it)
            }
            user

        } catch(e: GetCredentialException){
            Log.e("signIn","Error Signing In",e)
            null
        }

    }

    private fun handleSignIn(result: GetCredentialResponse):GoogleSignInResult?{
        val credential = result.credential

        return when(credential){
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    Log.d("signIn","Checkpoint 1")
                    try{
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken,null)
                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Log.d("GoogleSignIn","Firebase Authentication Successful")
                                }else{
                                    Log.e("GoogleSignIn","Firebase Authentication Failed",task.exception)
                                }

                            }
                        return GoogleSignInResult(
                            googleIdTokenCredential.id,
                            googleIdTokenCredential.displayName,
                            googleIdTokenCredential.idToken,
                            googleIdTokenCredential.profilePictureUri.toString()
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignIn", "Invalid Google ID Token", e)
                        null
                    }
                } else {
                    Log.e("GoogleSignIn", "Unexpected credential type")
                    null
                }
            }
            else -> {
                Log.e("GoogleSignIn", "Unexpected credential type")
                null
            }
        }
    }

    fun registerWithFirebase(
        email: String,
        password: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val fs = firestoreFunction

        // Step 1: Create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {

                        fs.storeUserInFirestore(
                            GoogleSignInResult(
                                userId = "with Email",
                                userName = username,
                                profilePicture = null,
                                userEmail = email
                            )
                        )
                        onSuccess()

                    } else {
                        onError("User ID not found after registration.")
                    }
                } else {
                    onError("Registration failed: ${task.exception?.message ?: "Unknown error"}")
                }
            }
            .addOnFailureListener {
                onError("Failed to create user: ${it.message}")
            }
    }



    fun loginWithFirebase(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login failed")
                }
            }
    }

}




