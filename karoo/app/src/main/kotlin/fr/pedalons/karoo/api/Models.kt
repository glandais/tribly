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

// Routes
@Serializable
data class RoutesResponse(
    @SerialName("routes") val routes: List<Route>
)

@Serializable
data class Route(
    @SerialName("teamSlug") val teamSlug: String,
    @SerialName("routeSlug") val routeSlug: String,
    @SerialName("routeName") val routeName: String,
    @SerialName("rideName") val rideName: String? = null,
    @SerialName("groupName") val groupName: String? = null,
    @SerialName("startDateTime") val startDateTime: String? = null,
    @SerialName("distance") val distance: Float,
    @SerialName("elevationGain") val elevationGain: Float,
    @SerialName("startLat") val startLat: Double? = null,
    @SerialName("startLon") val startLon: Double? = null
) {
    /** True if this route is associated with a group event */
    val hasGroup: Boolean get() = !groupName.isNullOrBlank()

    /** True if this route is associated with a ride */
    val hasRide: Boolean get() = !rideName.isNullOrBlank()
}

@Serializable
data class SyncResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String? = null,
    @SerialName("externalRouteId") val externalRouteId: String? = null
)

// Error
@Serializable
data class ErrorResponse(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null
)
