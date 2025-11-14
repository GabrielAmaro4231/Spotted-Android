package com.gabrielamaro.spotted.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID
import androidx.activity.ComponentActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import com.gabrielamaro.spotted.data.supabase

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Spotted ✈️",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        InsertButton()
        GoogleSignInButton(onLoginSuccess = onLoginClick)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun InsertButton() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Button(onClick = {
        coroutineScope.launch {
            try {
                supabase.from("posts").insert(mapOf("content" to "Hello from Android"))
                Toast.makeText(context, "New Row Inserted", Toast.LENGTH_SHORT).show()
            } catch (e: RestException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Text("Insert a new row")
    }
}

@Composable
fun GoogleSignInButton(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = context as? ComponentActivity

    val onClick: () -> Unit = {
        if (activity != null) {
            val credentialManager = CredentialManager.create(activity)

            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setServerClientId("395041422171-vfaouss3is01npief8he623kc93ocuna.apps.googleusercontent.com")
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = activity
                    )

                    val credential = result.credential
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    val googleIdToken = googleIdTokenCredential.idToken

                    supabase.auth.signInWith(IDToken) {
                        idToken = googleIdToken
                        provider = Google
                        nonce = rawNonce
                    }

                    Toast.makeText(context, "You are signed in!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } catch (e: GetCredentialException) {
                    Log.e("GoogleSignIn", "GetCredentialException", e)
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("GoogleSignIn", "ParsingException", e)
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("GoogleSignIn", "Unexpected error", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Activity not available", Toast.LENGTH_SHORT).show()
        }
    }

    Button(onClick = onClick) {
        Text("Sign in with Google!")
    }
}
