package fr.pedalons.karoo.auth

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.pedalons.karoo.R
import fr.pedalons.karoo.api.PedalonsApiClient
import fr.pedalons.karoo.api.UnauthorizedException
import fr.pedalons.karoo.ui.theme.PedalonsKarooTheme
import io.hammerhead.karooext.KarooSystemService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Activity for connecting Hammerhead GPS service.
 * Displays QR code to profile page where user can connect Hammerhead OAuth.
 */
class GpsConnectActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BASE_URL = "base_url"
        const val RESULT_SUCCESS = 1
        const val RESULT_SKIPPED = 0
        const val RESULT_ERROR = -1
    }

    private var apiClient: PedalonsApiClient? = null
    private lateinit var authManager: AuthManager
    private lateinit var karooSystem: KarooSystemService
    private var baseUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseUrl = intent.getStringExtra(EXTRA_BASE_URL) ?: run {
            setResult(RESULT_ERROR)
            finish()
            return
        }

        authManager = AuthManager(this)
        karooSystem = KarooSystemService(applicationContext)

        setContent {
            var isConnected by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                karooSystem.connect { }
                // Poll for connection status
                while (!karooSystem.connected) {
                    delay(100)
                }
                apiClient = PedalonsApiClient(baseUrl!!, karooSystem)
                isConnected = true
            }

            PedalonsKarooTheme {
                if (isConnected && apiClient != null) {
                    GpsConnectScreen(
                        apiClient = apiClient!!,
                        authManager = authManager,
                        profileUrl = "$baseUrl/profile",
                        onSuccess = {
                            setResult(RESULT_SUCCESS)
                            finish()
                        },
                        onSkip = {
                            setResult(RESULT_SKIPPED)
                            finish()
                        }
                    )
                } else {
                    // Show loading while connecting to Karoo System Service
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(stringResource(R.string.connecting))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        apiClient?.close()
        if (::karooSystem.isInitialized) {
            karooSystem.disconnect()
        }
    }
}

@Composable
private fun GpsConnectScreen(
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    profileUrl: String,
    onSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    var isChecking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val qrBitmap = remember(profileUrl) {
        generateQrCode(profileUrl)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Title section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.gps_connect_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.gps_connect_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // QR Code section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(140.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.gps_connect_scan),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Buttons section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            isChecking = true
                            checkHammerheadConnection(
                                apiClient = apiClient,
                                authManager = authManager,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        R.string.gps_connect_success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSuccess()
                                },
                                onNotConnected = {
                                    Toast.makeText(
                                        context,
                                        R.string.gps_connect_not_detected,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isChecking = false
                                },
                                onError = {
                                    Toast.makeText(
                                        context,
                                        R.string.error_unknown,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isChecking = false
                                }
                            )
                        }
                    },
                    enabled = !isChecking,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    if (isChecking) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.gps_connect_checking))
                        }
                    } else {
                        Text(stringResource(R.string.gps_connect_done))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onSkip,
                    enabled = !isChecking,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(stringResource(R.string.gps_connect_skip))
                }
            }
        }
    }
}

private suspend fun checkHammerheadConnection(
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    onSuccess: () -> Unit,
    onNotConnected: () -> Unit,
    onError: () -> Unit
) {
    // Get access token, refresh if needed
    var accessToken = authManager.getValidAccessToken()
    if (accessToken == null && authManager.needsRefresh()) {
        val refreshToken = authManager.getRefreshToken()
        if (refreshToken != null) {
            apiClient.refreshToken(refreshToken)
                .onSuccess { tokenResponse ->
                    authManager.updateAccessToken(tokenResponse.accessToken, tokenResponse.expiresIn)
                    accessToken = tokenResponse.accessToken
                }
                .onFailure {
                    onError()
                    return
                }
        }
    }

    if (accessToken == null) {
        onError()
        return
    }

    apiClient.getUserStatus(accessToken)
        .onSuccess { status ->
            if (status.isHammerheadConnected()) {
                onSuccess()
            } else {
                onNotConnected()
            }
        }
        .onFailure { error ->
            if (error is UnauthorizedException) {
                onError()
            } else {
                onError()
            }
        }
}
