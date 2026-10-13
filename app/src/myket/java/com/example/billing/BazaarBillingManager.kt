package com.example.billing

import androidx.activity.ComponentActivity

/**
 * Myket-specific build placeholder. It deliberately contains no Cafe Bazaar SDK calls or intents.
 * Real Myket purchases remain disabled until the Myket billing SDK and product IDs are configured.
 */
class BazaarBillingManager(
    private val activity: ComponentActivity,
    private val onMessage: (String) -> Unit,
    private val onConnectionChanged: (Boolean) -> Unit
) {
    fun connect() {
        onConnectionChanged(false)
        onMessage("نسخه مایکت ساخته شده است؛ پرداخت مایکت هنوز پیکربندی نشده است.")
    }

    fun subscribe(productId: String, onVerifiedPurchase: (String) -> Unit) {
        onMessage("خرید انجام نشد؛ اتصال پرداخت مایکت هنوز فعال نشده است.")
    }

    fun restoreSubscriptions(onVerifiedSubscription: (String) -> Unit) {
        // Never grant VIP based on a local or unverified state.
    }

    fun disconnect() {
        onConnectionChanged(false)
    }
}
