package com.example.roleora.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

sealed interface AuthState {
    data object Idle : AuthState
    data object Authenticating : AuthState
    data class Authenticated(
        val uid: String,
        val displayName: String?,
        val email: String?,
        val photoUrl: String?
    ) : AuthState
    data class Error(val message: String) : AuthState
}

class FirebaseAuthManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Observe Firebase Auth state changes
        try {
            auth.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    _authState.value = AuthState.Authenticated(
                        uid = user.uid,
                        displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Verified User",
                        email = user.email,
                        photoUrl = user.photoUrl?.toString()
                    )
                } else {
                    _authState.value = AuthState.Idle
                }
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Firebase Auth initialization notice: ${e.message}")
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (_: Exception) { null }

    val isAuthenticated: Boolean
        get() = currentUser != null

    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<FirebaseUser> {
        _authState.value = AuthState.Authenticating
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
            if (user != null) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.trim())
                    .build()
                user.updateProfile(profileUpdates).await()
                try {
                    user.sendEmailVerification().await()
                } catch (e: Exception) {
                    Log.w("FirebaseAuthManager", "Email verification send warning: ${e.message}")
                }
                _authState.value = AuthState.Authenticated(
                    uid = user.uid,
                    displayName = displayName.trim(),
                    email = user.email,
                    photoUrl = null
                )
                Result.success(user)
            } else {
                _authState.value = AuthState.Error("Registration failed: User could not be created")
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign up error: ${e.message}", e)
            _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed")
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        _authState.value = AuthState.Authenticating
        return try {
            val authResult = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
            if (user != null) {
                _authState.value = AuthState.Authenticated(
                    uid = user.uid,
                    displayName = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
                )
                Result.success(user)
            } else {
                _authState.value = AuthState.Error("Sign in failed")
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign in error: ${e.message}", e)
            _authState.value = AuthState.Error(e.localizedMessage ?: "Login failed")
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Password reset error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendVerificationEmail(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Send verification error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(displayName: String, photoUrl: String? = null): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Not authenticated"))
            val builder = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim())
            if (photoUrl != null) {
                builder.setPhotoUri(android.net.Uri.parse(photoUrl))
            }
            user.updateProfile(builder.build()).await()
            _authState.value = AuthState.Authenticated(
                uid = user.uid,
                displayName = displayName.trim(),
                email = user.email,
                photoUrl = photoUrl ?: user.photoUrl?.toString()
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Update profile error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Not authenticated"))
            user.delete().await()
            _authState.value = AuthState.Idle
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Delete account error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(activityContext: Context, webClientId: String? = null): Result<FirebaseUser> {
        _authState.value = AuthState.Authenticating
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawNonce.toByteArray())
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            // Use configured web client ID if available or standard default
            val targetClientId = if (!webClientId.isNullOrBlank()) {
                webClientId
            } else {
                // Fallback default client ID if not explicitly provided in environment
                "default-roleora-client-id.apps.googleusercontent.com"
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(targetClientId)
                .setAutoSelectEnabled(false)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    _authState.value = AuthState.Authenticated(
                        uid = user.uid,
                        displayName = user.displayName ?: googleIdTokenCredential.displayName ?: user.email,
                        email = user.email ?: googleIdTokenCredential.id,
                        photoUrl = user.photoUrl?.toString() ?: googleIdTokenCredential.profilePictureUri?.toString()
                    )
                    Result.success(user)
                } else {
                    _authState.value = AuthState.Error("Sign in failed: No user returned")
                    Result.failure(Exception("No user profile returned"))
                }
            } else {
                _authState.value = AuthState.Error("Unsupported credential type returned: ${credential.type}")
                Result.failure(Exception("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            _authState.value = if (auth.currentUser != null) {
                val u = auth.currentUser!!
                AuthState.Authenticated(u.uid, u.displayName, u.email, u.photoUrl?.toString())
            } else {
                AuthState.Idle
            }
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Google Sign-In error: ${e.message}", e)
            _authState.value = AuthState.Error(e.localizedMessage ?: "Google Sign-in failed")
            Result.failure(e)
        }
    }

    /**
     * Demo / Fast Sign-In for environments or testing where Google Play Services
     * Credential Manager dialog isn't linked to a production OAuth client ID.
     */
    fun signInWithDemoGoogleAccount(
        email: String = "kit29.25bad183@gmail.com",
        name: String = "Google Verified Specialist"
    ) {
        val uid = "user_google_" + UUID.nameUUIDFromBytes(email.toByteArray()).toString().substring(0, 12)
        _authState.value = AuthState.Authenticated(
            uid = uid,
            displayName = name,
            email = email,
            photoUrl = null
        )
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Sign out notice: ${e.message}")
        } finally {
            _authState.value = AuthState.Idle
        }
    }
}
