package fr.pedalons.karoo

import android.content.Intent
import io.hammerhead.karooext.extension.KarooExtension

/**
 * Pédalons Karoo Extension service.
 * Provides integration with Pédalons for route syncing.
 */
class PedalonsExtension : KarooExtension("pedalons", "1.0.0") {

    override fun onCreate() {
        super.onCreate()
        // Extension created
    }

    override fun onDestroy() {
        super.onDestroy()
        // Extension destroyed
    }

    override fun onBonusAction(actionId: String) {
        when (actionId) {
            "sync-routes" -> {
                // Launch the main activity for route browsing
                val intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }
    }
}
