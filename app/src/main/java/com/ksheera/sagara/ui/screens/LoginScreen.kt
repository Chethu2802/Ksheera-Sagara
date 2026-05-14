package com.ksheera.sagara.ui.screens

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.ksheera.sagara.ui.components.ScrollFabsOverlay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ksheera.sagara.R
import com.ksheera.sagara.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(auth: AuthViewModel, onSignedIn: () -> Unit) {

    val __scrState = rememberScrollState()
    val __scrScope = rememberCoroutineScope()
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {

    var tab by remember { mutableStateOf(0) }
    var showForgot by remember { mutableStateOf(false) }
    val msg by auth.message.collectAsState()
    val snack = remember { SnackbarHostState() }
    LaunchedEffect(msg) { msg?.let { snack.showSnackbar(it) } }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color(0xCC000000), Color(0x66000000), Color(0xCC000000))
                )
            )
        )
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snack) }
        ) { pad ->
            Column(
                Modifier.padding(pad).fillMaxSize().verticalScroll(__scrState).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text("Ksheera Sagara", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("ಕ್ಷೀರ ಸಾಗರ", fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Text(
                    "Dairy Farm Manager · ಡೈರಿ ನಿರ್ವಹಣೆ",
                    fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xF2FFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        TabRow(tab) {
                            Tab(tab == 0, { tab = 0 }, text = { Text("Sign In / ಲಾಗಿನ್") })
                            Tab(tab == 1, { tab = 1 }, text = { Text("Sign Up / ನೋಂದಣಿ") })
                        }
                        Spacer(Modifier.height(20.dp))
                        if (tab == 0) SignInForm(auth, onSignedIn, onForgot = { showForgot = true })
                        else SignUpForm(auth, onSignedUp = { tab = 0 })
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }

    if (showForgot) {
        ForgotPasswordDialog(auth = auth, onDismiss = { showForgot = false })
    }

        ScrollFabsOverlay(__scrState, __scrScope, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun SignInForm(auth: AuthViewModel, onSignedIn: () -> Unit, onForgot: () -> Unit) {
    var u by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var remember1 by remember { mutableStateOf(true) }
    OutlinedTextField(u, { u = it }, label = { Text("Username / ಬಳಕೆದಾರ ಹೆಸರು") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(p, { p = it }, label = { Text("Password / ಪಾಸ್‌ವರ್ಡ್") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = remember1, onCheckedChange = { remember1 = it })
        Text("Remember me / ನೆನಪಿಡಿ", fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(
            "Forgot password? / ಮರೆತಿರಾ?",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onForgot() }
        )
    }
    Spacer(Modifier.height(16.dp))
    Button({ auth.signIn(u, p, remember1) { ok -> if (ok) onSignedIn() } }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Text("Sign In / ಲಾಗಿನ್", fontSize = 16.sp)
    }
}

@Composable
private fun SignUpForm(auth: AuthViewModel, onSignedUp: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    OutlinedTextField(name, { name = it }, label = { Text("Name / ಹೆಸರು") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(username, { username = it }, label = { Text("Username / ಬಳಕೆದಾರ ಹೆಸರು") }, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(email, { email = it }, label = { Text("Gmail / ಇಮೇಲ್") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(pw, { pw = it }, label = { Text("Password / ಪಾಸ್‌ವರ್ಡ್") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(key, { key = it }, label = { Text("Secret Key Number / ರಹಸ್ಯ ಸಂಖ್ಯೆ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
    Text(
        "Remember the Secret Key — needed to reset password.",
        fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp)
    )
    Spacer(Modifier.height(16.dp))
    Button({ auth.signUp(name, username, email, pw, key) { ok -> if (ok) onSignedUp() } }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
        Text("Create Account / ನೋಂದಣಿ", fontSize = 16.sp)
    }
}

@Composable
private fun ForgotPasswordDialog(auth: AuthViewModel, onDismiss: () -> Unit) {
    var ident by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password / ಪಾಸ್‌ವರ್ಡ್ ಮರುಹೊಂದಿಸಿ") },
        text = {
            Column {
                Text("Enter your username or email, the secret key you set during sign-up, and a new password.", fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(ident, { ident = it }, label = { Text("Username or Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(key, { key = it }, label = { Text("Secret Key Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(pw, { pw = it }, label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton({ auth.resetPassword(ident, key, pw) { ok -> if (ok) onDismiss() } }) {
                Text("Reset")
            }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } }
    )
}
