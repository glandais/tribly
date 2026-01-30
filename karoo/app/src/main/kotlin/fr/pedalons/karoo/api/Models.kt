package fr.pedalons.karoo.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Device Code Flow
@Serializable
data class DeviceCodeRequest(
    @SerialName("clientId") val clientId: String = "karoo"
)

@Serializable
data class DeviceCodeResponse(
    @SerialName("deviceCode") val deviceCode: String,
    @SerialName("userCode") val userCode: String,
    @SerialName("verificationUri") val verificationUri: String,
    @SerialName("verificationUriComplete") val verificationUriComplete: String,
    @SerialName("expiresIn") val expiresIn: Int,
    @SerialName("interval") val interval: Int
)

@Serializable
data class TokenRequest(
    @SerialName("grantType") val grantType: String,
    @SerialName("deviceCode") val deviceCode: String? = null,
    @SerialName("refreshToken") val refreshToken: String? = null
) {
    companion object {
        const val GRANT_TYPE_DEVICE_CODE = "urn:ietf:params:oauth:grant-type:device_code"
        const val GRANT_TYPE_REFRESH = "refresh_token"
    }
}

@Serializable
data class TokenResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("tokenType") val tokenType: String,
    @SerialName("expiresIn") val expiresIn: Int,
    @SerialName("refreshToken") val refreshToken: String? = null
)

// Routes response with rides and standalone routes
@Serializable
data class RoutesResponse(
    @SerialName("rides") val rides: List<DeviceRide>,
    @SerialName("routes") val routes: List<DeviceRoute>
)

@Serializable
data class DeviceRide(
    @SerialName("teamSlug") val teamSlug: String,
    @SerialName("rideSlug") val rideSlug: String,
    @SerialName("rideName") val rideName: String,
    @SerialName("startDateTime") val startDateTime: String? = null,
    @SerialName("entries") val entries: List<DeviceRideEntry>
)

@Serializable
data class DeviceRideEntry(
    @SerialName("routeSlug") val routeSlug: String,
    @SerialName("routeName") val routeName: String,
    @SerialName("groupName") val groupName: String? = null,
    @SerialName("distance") val distance: Float,
    @SerialName("elevationGain") val elevationGain: Float,
    @SerialName("startLat") val startLat: Double? = null,
    @SerialName("startLon") val startLon: Double? = null
)

@Serializable
data class DeviceRoute(
    @SerialName("teamSlug") val teamSlug: String,
    @SerialName("routeSlug") val routeSlug: String,
    @SerialName("routeName") val routeName: String,
    @SerialName("distance") val distance: Float,
    @SerialName("elevationGain") val elevationGain: Float,
    @SerialName("startLat") val startLat: Double? = null,
    @SerialName("startLon") val startLon: Double? = null
)

@Serializable
data class SyncResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("externalRouteId") val externalRouteId: String? = null
)

// User Status
@Serializable
data class UserStatusResponse(
    @SerialName("connectedGpsServices") val connectedGpsServices: List<String>
) {
    fun isHammerheadConnected(): Boolean = connectedGpsServices.contains("HAMMERHEAD")
}

// Error
@Serializable
data class ErrorResponse(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null
)
