package com.tribly.karoo.auth

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.tribly.karoo.R
import com.tribly.karoo.api.AuthorizationPendingException
import com.tribly.karoo.api.DeviceCodeResponse
import com.tribly.karoo.api.TokenExpiredException
import com.tribly.karoo.api.TriblyApiClient
import com.tribly.karoo.ui.theme.TriblyKarooTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity for device code authentication flow.
 * Displays QR code and user code, polls for authentication completion.
 */
class AuthActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BASE_URL = "base_url"
        const val RESULT_SUCCESS = 1
        const val RESULT_CANCELLED = 0
        const val RESULT_ERROR = -1
    }

    private lateinit var apiClient: TriblyApiClient
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseUrl = intent.getStringExtra(EXTRA_BASE_URL) ?: run {
            setResult(RESULT_ERROR)
            finish()
            return
        }

        apiClient = TriblyApiClient(baseUrl)
        authManager = AuthManager(this)

        setContent {
            TriblyKarooTheme {
                AuthScreen(
                    apiClient = apiClient,
                    authManager = authManager,
                    onSuccess = {
                        setResult(RESULT_SUCCESS)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELLED)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::apiClient.isInitialized) {
            apiClient.close()
        }
    }
}

@Composable
private fun AuthScreen(
    apiClient: TriblyApiClient,
    authManager: AuthManager,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var state by remember { mutableStateOf<AuthState>(AuthState.Loading) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Request device code
        apiClient.requestDeviceCode()
            .onSuccess { response ->
                state = AuthState.ShowingCode(response)
                // Start polling
                scope.launch {
                    pollForToken(apiClient, authManager, response, onSuccess) { newState ->
                        state = newState
                    }
                }
            }
            .onFailure { error ->
                state = AuthState.Error(error.message ?: "Failed to start authentication")
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val currentState = state) {
            is AuthState.Loading -> LoadingContent()
            is AuthState.ShowingCode -> CodeContent(currentState.deviceCodeResponse)
            is AuthState.Polling -> PollingContent(currentState.userCode)
            is AuthState.Success -> SuccessContent()
            is AuthState.Error -> ErrorContent(currentState.message, onCancel)
            is AuthState.Expired -> ExpiredContent(onCancel)
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CodeContent(deviceCode: DeviceCodeResponse) {
    val qrBitmap = remember(deviceCode.verificationUriComplete) {
        generateQrCode(deviceCode.verificationUriComplete, 200)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.auth_connect_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.auth_connect_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // QR Code
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User code
        Text(
            text = stringResource(R.string.auth_code_label),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = deviceCode.userCode,
            style = MaterialTheme.typography.displaySmall.copy(
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Polling indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.auth_waiting),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PollingContent(userCode: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = userCode,
                style = MaterialTheme.typography.displaySmall.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.auth_waiting),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SuccessContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.auth_success),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.auth_error),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Close")
            }
        }
    }
}

@Composable
private fun ExpiredContent(onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.auth_expired),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}

private sealed class AuthState {
    data object Loading : AuthState()
    data class ShowingCode(val deviceCodeResponse: DeviceCodeResponse) : AuthState()
    data class Polling(val userCode: String) : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
    data object Expired : AuthState()
}

private suspend fun pollForToken(
    apiClient: TriblyApiClient,
    authManager: AuthManager,
    deviceCode: DeviceCodeResponse,
    onSuccess: () -> Unit,
    updateState: (AuthState) -> Unit
) {
    val intervalMs = (deviceCode.interval * 1000).toLong()
    val expiresAt = System.currentTimeMillis() + (deviceCode.expiresIn * 1000)

    while (System.currentTimeMillis() < expiresAt) {
        delay(intervalMs)

        apiClient.pollForToken(deviceCode.deviceCode)
            .onSuccess { tokenResponse ->
                authManager.saveTokens(tokenResponse)
                updateState(AuthState.Success)
                delay(1000) // Show success briefly
                onSuccess()
                return
            }
            .onFailure { error ->
                when (error) {
                    is AuthorizationPendingException -> {
                        // Continue polling
                    }
                    is TokenExpiredException -> {
                        updateState(AuthState.Expired)
                        return
                    }
                    else -> {
                        updateState(AuthState.Error(error.message ?: "Unknown error"))
                        return
                    }
                }
            }
    }

    // Timeout
    updateState(AuthState.Expired)
}

private fun generateQrCode(content: String, size: Int): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}
