package fr.pedalons.karoo

import io.hammerhead.karooext.extension.KarooExtension

/** Pédalons Karoo Extension service. Provides integration with Pédalons for route syncing. */
class PedalonsExtension : KarooExtension("pedalons", "1.0.0") {

    override fun onCreate() {
        super.onCreate()
        // Extension created
    }

    override fun onDestroy() {
        super.onDestroy()
        // Extension destroyed
    }
}
