package com.example.roleora.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roleora.data.auth.AuthState
import com.example.roleora.data.cloud.CloudSyncState
import com.example.roleora.ui.theme.EmeraldGreen
import com.example.roleora.ui.theme.PolishGreen
import com.example.roleora.ui.theme.PolishPrimary
import com.example.roleora.ui.theme.TealAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthAndCloudSyncModalSheet(
    authState: AuthState,
    syncState: CloudSyncState,
    totalRolesCount: Int,
    totalRecordsCount: Int,
    totalDiaryCount: Int,
    onSignInWithGoogle: (Context) -> Unit,
    onSignInWithDemo: () -> Unit,
    onSignInWithEmail: (String, String) -> Unit = { _, _ -> },
    onSignUpWithEmail: (String, String, String) -> Unit = { _, _, _ -> },
    onSendPasswordReset: (String) -> Unit = {},
    onUpdateProfile: (String) -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit,
    onRestoreFromCloud: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Google, 1: Email Sign In, 2: Sign Up, 3: Reset Password
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editDisplayName by remember { mutableStateOf("") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
                .testTag("auth_cloud_sheet_container")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Firebase Auth & Cloud",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Google Identity & Firestore Persistence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (authState is AuthState.Authenticated) PolishGreen.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (authState is AuthState.Authenticated) Icons.Default.CloudDone else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (authState is AuthState.Authenticated) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (authState is AuthState.Authenticated) "Cloud Online" else "Local Only",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (authState is AuthState.Authenticated) PolishGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // User Identity / Auth Form Section
            when (authState) {
                is AuthState.Authenticated -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val initials = authState.displayName?.take(2)?.uppercase() ?: "US"
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = authState.displayName ?: "Verified User",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "Verified",
                                            tint = PolishGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Text(
                                        text = authState.email ?: "Account Connected",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "UID: ${authState.uid.take(16)}...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editDisplayName = authState.displayName ?: ""
                                        showEditProfileDialog = true
                                    },
                                    modifier = Modifier.testTag("edit_profile_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onSignOut,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("sign_out_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                                ) {
                                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sign Out", fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { showDeleteConfirmDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("delete_account_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Purge Account", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                is AuthState.Authenticating -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Authenticating...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Connecting securely to Firebase Auth",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                is AuthState.Idle, is AuthState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            TabRow(
                                selectedTabIndex = selectedAuthTab,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                Tab(
                                    selected = selectedAuthTab == 0,
                                    onClick = { selectedAuthTab = 0; formError = null },
                                    text = { Text("Google", fontSize = 12.sp) }
                                )
                                Tab(
                                    selected = selectedAuthTab == 1,
                                    onClick = { selectedAuthTab = 1; formError = null },
                                    text = { Text("Email Login", fontSize = 12.sp) }
                                )
                                Tab(
                                    selected = selectedAuthTab == 2,
                                    onClick = { selectedAuthTab = 2; formError = null },
                                    text = { Text("Register", fontSize = 12.sp) }
                                )
                                Tab(
                                    selected = selectedAuthTab == 3,
                                    onClick = { selectedAuthTab = 3; formError = null },
                                    text = { Text("Reset", fontSize = 12.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (authState is AuthState.Error) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = authState.message,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }

                            if (formError != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = formError ?: "",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }

                            when (selectedAuthTab) {
                                0 -> {
                                    // Google Sign-In Tab
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "One-Click Google Authentication",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Sign in using your Google account via Jetpack Credential Manager for cloud synchronization.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { onSignInWithGoogle(context) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("google_sign_in_button"),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sign In with Google", fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        FilledTonalButton(
                                            onClick = onSignInWithDemo,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(44.dp)
                                                .testTag("demo_google_sign_in_button"),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Fast Test Sign-In")
                                        }
                                    }
                                }

                                1 -> {
                                    // Email Sign In Tab
                                    Column {
                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { emailInput = it },
                                            label = { Text("Email Address") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("email_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = passwordInput,
                                            onValueChange = { passwordInput = it },
                                            label = { Text("Password") },
                                            singleLine = true,
                                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                            trailingIcon = {
                                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                    Icon(
                                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = null
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("password_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = {
                                                if (emailInput.isBlank() || !emailInput.contains("@")) {
                                                    formError = "Please enter a valid email address"
                                                } else if (passwordInput.length < 6) {
                                                    formError = "Password must be at least 6 characters"
                                                } else {
                                                    formError = null
                                                    onSignInWithEmail(emailInput.trim(), passwordInput)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("email_sign_in_button"),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Text("Sign In with Email", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                2 -> {
                                    // Register / Sign Up Tab
                                    Column {
                                        OutlinedTextField(
                                            value = nameInput,
                                            onValueChange = { nameInput = it },
                                            label = { Text("Full Name") },
                                            singleLine = true,
                                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("display_name_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { emailInput = it },
                                            label = { Text("Email Address") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("register_email_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = passwordInput,
                                            onValueChange = { passwordInput = it },
                                            label = { Text("Password (min 6 chars)") },
                                            singleLine = true,
                                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                            trailingIcon = {
                                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                    Icon(
                                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = null
                                                    )
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("register_password_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = {
                                                if (nameInput.isBlank()) {
                                                    formError = "Please enter your name"
                                                } else if (emailInput.isBlank() || !emailInput.contains("@")) {
                                                    formError = "Please enter a valid email address"
                                                } else if (passwordInput.length < 6) {
                                                    formError = "Password must be at least 6 characters"
                                                } else {
                                                    formError = null
                                                    onSignUpWithEmail(emailInput.trim(), passwordInput, nameInput.trim())
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("email_sign_up_button"),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Text("Create Account", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                3 -> {
                                    // Password Reset Tab
                                    Column {
                                        Text(
                                            text = "Forgot your password? Enter your email and we'll send a recovery link.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedTextField(
                                            value = emailInput,
                                            onValueChange = { emailInput = it },
                                            label = { Text("Registered Email") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("reset_email_input"),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Button(
                                            onClick = {
                                                if (emailInput.isBlank() || !emailInput.contains("@")) {
                                                    formError = "Please enter your registered email"
                                                } else {
                                                    formError = null
                                                    onSendPasswordReset(emailInput.trim())
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("reset_password_button"),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Text("Send Password Reset Email", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cloud Data Persistence & Sync Section
            Text(
                text = "Firestore Cloud Persistence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (syncState) {
                                    is CloudSyncState.Syncing -> Icons.Default.Sync
                                    is CloudSyncState.Synced -> Icons.Default.CheckCircle
                                    is CloudSyncState.Error -> Icons.Default.ErrorOutline
                                    is CloudSyncState.Idle -> Icons.Default.Storage
                                },
                                contentDescription = null,
                                tint = when (syncState) {
                                    is CloudSyncState.Synced -> PolishGreen
                                    is CloudSyncState.Syncing -> MaterialTheme.colorScheme.primary
                                    is CloudSyncState.Error -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = when (syncState) {
                                        is CloudSyncState.Syncing -> "Synchronizing..."
                                        is CloudSyncState.Synced -> "Synced with Firestore"
                                        is CloudSyncState.Error -> "Sync Alert"
                                        is CloudSyncState.Idle -> if (authState is AuthState.Authenticated) "Cloud Storage Ready" else "Local Database Mode"
                                    },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                val timeFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                                Text(
                                    text = when (syncState) {
                                        is CloudSyncState.Syncing -> syncState.progressMessage
                                        is CloudSyncState.Synced -> "Last synced at ${timeFormat.format(Date(syncState.lastSyncTimestamp))}"
                                        is CloudSyncState.Error -> syncState.message
                                        is CloudSyncState.Idle -> "Local SQLite (Room) active"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (syncState is CloudSyncState.Syncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Storage Metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MetricBadge(label = "Roles", count = totalRolesCount.toString())
                        MetricBadge(label = "Records", count = totalRecordsCount.toString())
                        MetricBadge(label = "Diary Entries", count = totalDiaryCount.toString())
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sync & Restore Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onSyncNow,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("sync_to_firestore_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = authState is AuthState.Authenticated && syncState !is CloudSyncState.Syncing
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync Now", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onRestoreFromCloud,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("restore_from_firestore_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = authState is AuthState.Authenticated && syncState !is CloudSyncState.Syncing
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security & Privacy Guarantee
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = PolishGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Zero Cross-Role Leakage: Each profession workspace is individually partitioned under your authenticated Google/Email UID in Room & Firestore.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Display Profile") },
            text = {
                OutlinedTextField(
                    value = editDisplayName,
                    onValueChange = { editDisplayName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_display_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editDisplayName.isNotBlank()) {
                            onUpdateProfile(editDisplayName.trim())
                            showEditProfileDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Permanently Purge Account?") },
            text = {
                Text("This action will permanently delete your user account, all role workspaces, projects, records, and diary entries from both this device and Cloud Firestore. This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Purge Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricBadge(label: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
