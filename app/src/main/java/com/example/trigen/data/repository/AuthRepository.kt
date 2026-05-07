package com.example.trigen.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1)

    init {
        refreshSignal.tryEmit(Unit)
    }

    fun authStateFlow(): Flow<FirebaseUser?> = merge(
        callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser)
            }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(listener)
            }
        },
        refreshSignal.map { firebaseAuth.currentUser }
    ).distinctUntilChanged { old, new ->
        old?.uid == new?.uid && old?.displayName == new?.displayName && old?.photoUrl == new?.photoUrl
    }

    suspend fun signUp(email: String, pass: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
                user.reload().await()
                refreshSignal.emit(Unit)
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                refreshSignal.emit(Unit)
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        refreshSignal.tryEmit(Unit)
    }

    suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
                user.reload().await()
                refreshSignal.emit(Unit)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.delete().await()
                refreshSignal.emit(Unit)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
