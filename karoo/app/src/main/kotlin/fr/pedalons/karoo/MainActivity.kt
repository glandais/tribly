package fr.pedalons.karoo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import io.hammerhead.karooext.models.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Conversion constants
private const val METERS_PER_MILE = 1609.344
private const val METERS_PER_FOOT = 0.3048
private const val METERS_PER_KM = 1000.0

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
    private var userProfileConsumerId: String? = null

    // Navigation state accessible from key events
    internal var routes: List<Route> = emptyList()
    internal var selectedIndex: Int = 0
    internal var onNavigationChanged: ((Int) -> Unit)? = null
    internal var onSyncRequested: ((Route) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)
        karooSystem = KarooSystemService(applicationContext)

        setContent {
            var isConnected by remember { mutableStateOf(false) }
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }

            LaunchedEffect(Unit) {
                karooSystem.connect { }
                // Poll for connection status
                while (!karooSystem.connected) {
                    delay(100)
                }
                apiClient = PedalonsApiClient(BASE_URL, karooSystem)

                // Listen for user profile updates to get preferred units
                userProfileConsumerId = karooSystem.addConsumer<UserProfile> { profile ->
                    userProfile = profile
                }

                isConnected = true
            }

            PedalonsKarooTheme {
                if (isConnected && apiClient != null) {
                    MainScreen(
                        apiClient = apiClient!!,
                        authManager = authManager,
                        userProfile = userProfile,
                        activity = this@MainActivity,
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
            userProfileConsumerId?.let { karooSystem.removeConsumer(it) }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_NAVIGATE_NEXT -> {
                // Move to next route
                if (routes.isNotEmpty() && selectedIndex < routes.size - 1) {
                    selectedIndex++
                    onNavigationChanged?.invoke(selectedIndex)
                }
                return true
            }
            KeyEvent.KEYCODE_NAVIGATE_PREVIOUS -> {
                // Move to previous route
                if (routes.isNotEmpty() && selectedIndex > 0) {
                    selectedIndex--
                    onNavigationChanged?.invoke(selectedIndex)
                }
                return true
            }
            KeyEvent.KEYCODE_NAVIGATE_IN -> {
                // Sync selected route
                if (routes.isNotEmpty() && selectedIndex in routes.indices) {
                    onSyncRequested?.invoke(routes[selectedIndex])
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}

@Composable
private fun MainScreen(
    apiClient: PedalonsApiClient,
    authManager: AuthManager,
    userProfile: UserProfile?,
    activity: MainActivity,
    onConnect: () -> Unit
) {
    // Collect auth state - will update when tokens change
    val isAuthenticated by authManager.isAuthenticated.collectAsState(initial = null)
    var routes by remember { mutableStateOf<List<Route>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var syncingRouteId by remember { mutableStateOf<String?>(null) }
    var selectedIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Register navigation callbacks with Activity
    LaunchedEffect(Unit) {
        activity.onNavigationChanged = { newIndex ->
            selectedIndex = newIndex
        }
        activity.onSyncRequested = { route ->
            scope.launch {
                syncRoute(context, apiClient, authManager, route) { syncing ->
                    syncingRouteId = if (syncing) "${route.teamSlug}/${route.routeSlug}" else null
                }
            }
        }
    }

    // Update Activity's routes list when routes change
    LaunchedEffect(routes) {
        activity.routes = routes
        activity.selectedIndex = 0
        selectedIndex = 0
    }

    // Auto-scroll when selection changes
    LaunchedEffect(selectedIndex) {
        if (routes.isNotEmpty() && selectedIndex in routes.indices) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

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
                    userProfile = userProfile,
                    syncingRouteId = syncingRouteId,
                    selectedIndex = selectedIndex,
                    listState = listState,
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
    userProfile: UserProfile?,
    syncingRouteId: String?,
    selectedIndex: Int,
    listState: LazyListState,
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
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(routes, key = { _, route -> "${route.teamSlug}/${route.routeSlug}" }) { index, route ->
                RouteItem(
                    route = route,
                    userProfile = userProfile,
                    isSelected = index == selectedIndex,
                    isSyncing = syncingRouteId == "${route.teamSlug}/${route.routeSlug}",
                    onSync = { onSync(route) }
                )
            }
        }
    }
}

/**
 * Formats an ISO 8601 datetime string to a localized date/time display.
 * Uses the device's locale and 12/24h preference.
 */
private fun formatDateTime(context: Context, isoDateTime: String?): String? {
    if (isoDateTime.isNullOrBlank()) return null

    return try {
        val instant = Instant.parse(isoDateTime)
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

        // Use device's locale and time format preference
        val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        val timeFormatter = if (DateFormat.is24HourFormat(context)) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("h:mm a")
        }

        "${localDateTime.format(dateFormatter)} ${localDateTime.format(timeFormatter)}"
    } catch (e: Exception) {
        null
    }
}

/**
 * Formats distance in meters according to user's preferred unit.
 * Returns formatted string like "87.5 km" or "54.4 mi".
 */
private fun formatDistance(context: Context, distanceMeters: Float, userProfile: UserProfile?): String {
    val isImperial = userProfile?.preferredUnit?.distance == UserProfile.PreferredUnit.UnitType.IMPERIAL
    return if (isImperial) {
        val miles = distanceMeters / METERS_PER_MILE
        context.getString(R.string.distance_format_mi, miles)
    } else {
        val km = distanceMeters / METERS_PER_KM
        context.getString(R.string.distance_format_km, km)
    }
}

/**
 * Formats elevation in meters according to user's preferred unit.
 * Returns formatted string like "1250m" or "4101ft".
 */
private fun formatElevation(context: Context, elevationMeters: Float, userProfile: UserProfile?): String {
    val isImperial = userProfile?.preferredUnit?.elevation == UserProfile.PreferredUnit.UnitType.IMPERIAL
    return if (isImperial) {
        val feet = (elevationMeters / METERS_PER_FOOT).toInt()
        context.getString(R.string.elevation_format_ft, feet)
    } else {
        context.getString(R.string.elevation_format_m, elevationMeters.toInt())
    }
}

/**
 * Formats combined distance and elevation stats for route sublabel.
 */
private fun formatRouteStats(context: Context, route: Route, userProfile: UserProfile?): String {
    val isImperialDist = userProfile?.preferredUnit?.distance == UserProfile.PreferredUnit.UnitType.IMPERIAL
    val isImperialElev = userProfile?.preferredUnit?.elevation == UserProfile.PreferredUnit.UnitType.IMPERIAL

    val distanceStr = if (isImperialDist) {
        val miles = route.distance / METERS_PER_MILE
        context.getString(R.string.distance_format_mi, miles)
    } else {
        val km = route.distance / METERS_PER_KM
        context.getString(R.string.distance_format_km, km)
    }

    val elevationStr = if (isImperialElev) {
        val feet = (route.elevationGain / METERS_PER_FOOT).toInt()
        context.getString(R.string.elevation_dplus_ft, feet)
    } else {
        context.getString(R.string.elevation_dplus_m, route.elevationGain.toInt())
    }

    return "$distanceStr / $elevationStr"
}

@Composable
private fun RouteItem(
    route: Route,
    userProfile: UserProfile?,
    isSelected: Boolean,
    isSyncing: Boolean,
    onSync: () -> Unit
) {
    val context = LocalContext.current

    // Determine display type based on Garmin app logic:
    // 1. Group + Ride: Title = "Group - Ride", Sublabel = date/time
    // 2. Ride only: Title = "Ride", Sublabel = date/time
    // 3. Standalone: Title = "Route", Sublabel = "distance / elevation D+"
    val (title, sublabel) = when {
        route.hasGroup && route.hasRide -> {
            val title = "${route.groupName} - ${route.rideName}"
            val sublabel = formatDateTime(context, route.startDateTime)
            title to sublabel
        }
        route.hasRide -> {
            val title = route.rideName ?: route.routeName
            val sublabel = formatDateTime(context, route.startDateTime)
            title to sublabel
        }
        else -> {
            // Standalone route - show distance/elevation as sublabel
            val title = route.routeName
            val sublabel = formatRouteStats(context, route, userProfile)
            title to sublabel
        }
    }

    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.medium
        )
    } else {
        Modifier
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .then(borderModifier)
            .clickable(onClick = onSync),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
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
                // Line 1: Title (group-ride, ride, or route name)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Line 2: Sublabel (date/time or stats)
                if (sublabel != null) {
                    Text(
                        text = sublabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Line 3: Distance and elevation (always shown for context)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatDistance(context, route.distance, userProfile),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatElevation(context, route.elevationGain, userProfile),
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
