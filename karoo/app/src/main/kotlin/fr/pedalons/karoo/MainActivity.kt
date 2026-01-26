package fr.pedalons.karoo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.pedalons.karoo.api.Route
import fr.pedalons.karoo.api.PedalonsApiClient
import fr.pedalons.karoo.api.UnauthorizedException
import fr.pedalons.karoo.auth.AuthActivity
import fr.pedalons.karoo.auth.AuthManager
import fr.pedalons.karoo.ui.theme.PedalonsKarooTheme
import io.hammerhead.karooext.KarooSystemService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Main activity for browsing and syncing routes.
 */
class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_AUTH = 100
        // TODO: Make this configurable
        private const val BASE_URL = "https://www.pedalons.fr"
    }

    private var apiClient: PedalonsApiClient? = null
    private lateinit var authManager: AuthManager
    private lateinit var karooSystem: KarooSystemService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                apiClient = PedalonsApiClient(BASE_URL, karooSystem)
                isConnected = true
            }

            PedalonsKarooTheme {
                if (isConnected && apiClient != null) {
                    MainScreen(
                        apiClient = apiClient!!,
                        authManager = authManager,
                        onConnect = { startAuthFlow() }
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

    private fun startAuthFlow() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra(AuthActivity.EXTRA_BASE_URL, BASE_URL)
        }
        startActivityForResult(intent, REQUEST_AUTH)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_AUTH && resultCode == AuthActivity.RESULT_SUCCESS) {
            // Refresh the UI - will be handled by LaunchedEffect
        }
    }
}

@Composable
private fun MainScreen(
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    onConnect: () -> Unit
) {
    // Collect auth state - will update when tokens change
    val isAuthenticated by authManager.isAuthenticated.collectAsState(initial = null)
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var syncingRouteId by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load routes when authenticated
    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated == null) {
            // Still loading auth state
            return@LaunchedEffect
        }

        if (isAuthenticated == true) {
            isLoading = true
            loadRoutes(apiClient, authManager) { result ->
                result.onSuccess {
                    routes = it
                    isLoading = false
                    error = null
                }.onFailure { e ->
                    if (e is UnauthorizedException) {
                        // Token invalid, clear and show connect screen
                        scope.launch {
                            authManager.clearTokens()
                        }
                    } else {
                        error = e.message
                    }
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            routes = emptyList()
            error = null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            isAuthenticated == null || isLoading -> {
                LoadingScreen()
            }
            isAuthenticated == false -> {
                ConnectScreen(onConnect = onConnect)
            }
            error != null -> {
                ErrorScreen(
                    message = error!!,
                    onRetry = {
                        isLoading = true
                        error = null
                        scope.launch {
                            loadRoutes(apiClient, authManager) { result ->
                                result.onSuccess {
                                    routes = it
                                    isLoading = false
                                }.onFailure { e ->
                                    error = e.message
                                    isLoading = false
                                }
                            }
                        }
                    }
                )
            }
            routes.isEmpty() -> {
                EmptyScreen()
            }
            else -> {
                RouteListScreen(
                    routes = routes,
                    syncingRouteId = syncingRouteId,
                    onSync = { route ->
                        scope.launch {
                            syncRoute(context, apiClient, authManager, route) { syncing ->
                                syncingRouteId = if (syncing) "${route.teamSlug}/${route.routeSlug}" else null
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
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
private fun ConnectScreen(onConnect: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.auth_connect_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(
                    text = stringResource(R.string.auth_connect_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.error_unknown),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.loading))
            }
        }
    }
}

@Composable
private fun EmptyScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.routes_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RouteListScreen(
    routes: List<Route>,
    syncingRouteId: String?,
    onSync: (Route) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.routes_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Route list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(routes, key = { "${it.teamSlug}/${it.routeSlug}" }) { route ->
                RouteItem(
                    route = route,
                    isSyncing = syncingRouteId == "${route.teamSlug}/${route.routeSlug}",
                    onSync = { onSync(route) }
                )
            }
        }
    }
}

@Composable
private fun RouteItem(
    route: Route,
    isSyncing: Boolean,
    onSync: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onSync),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Route info
            Column(modifier = Modifier.weight(1f)) {
                // Line 1: label (or name if no label)
                Text(
                    text = route.label ?: route.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Line 2: name (only if label present)
                if (route.label != null) {
                    Text(
                        text = route.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Line 3: distance and elevation
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.distance_format, route.distanceKm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.elevation_format, route.elevationGainInt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Sync indicator
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = stringResource(R.string.nav_arrow),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private suspend fun loadRoutes(
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    onResult: (Result<List<Route>>) -> Unit
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
                    onResult(Result.failure(UnauthorizedException()))
                    return
                }
        }
    }

    if (accessToken == null) {
        onResult(Result.failure(UnauthorizedException()))
        return
    }

    apiClient.getRoutes(accessToken!!)
        .map { it.routes }
        .let { onResult(it) }
}

private suspend fun syncRoute(
    context: Context,
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    route: Route,
    onSyncing: (Boolean) -> Unit
) {
    onSyncing(true)

    val accessToken = authManager.getValidAccessToken()
    if (accessToken == null) {
        onSyncing(false)
        return
    }

    apiClient.syncRoute(accessToken, route.teamSlug, route.routeSlug)
        .onSuccess {
            Toast.makeText(context, R.string.route_synced, Toast.LENGTH_SHORT).show()
        }
        .onFailure {
            Toast.makeText(context, R.string.route_sync_error, Toast.LENGTH_SHORT).show()
        }

    onSyncing(false)
}
