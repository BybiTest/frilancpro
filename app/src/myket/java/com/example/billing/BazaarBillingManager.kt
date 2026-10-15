package com.example.billing

import android.util.Log
import androidx.activity.ComponentActivity
import com.example.BuildConfig
import ir.myket.billingclient.IabHelper
import ir.myket.billingclient.util.IabResult
import ir.myket.billingclient.util.Inventory
import ir.myket.billingclient.util.Purchase

/**
 * Myket-only implementation backed by Myket's official IAB client.
 *
 * Myket currently treats these VIP options as in-app products. The caller must
 * grant a time-limited entitlement after receiving a verified product ID.
 */
class BazaarBillingManager(
    private val activity: ComponentActivity,
    private val onMessage: (String) -> Unit,
    private val onConnectionChanged: (Boolean) -> Unit
) {
    private val rsaKey = BuildConfig.MYKET_RSA_PUBLIC_KEY.trim()
    private var helper: IabHelper? = null
    private var connected = false
    private var disposed = false
    private var operationInProgress = false

    private fun notifyIfActive(message: String) {
        if (disposed || activity.isFinishing || activity.isDestroyed) return
        activity.runOnUiThread {
            if (!disposed && !activity.isFinishing && !activity.isDestroyed) onMessage(message)
        }
    }

    private val validVipProducts = setOf(
        "vip_monthly",
        "vip_quarterly",
        "vip_yearly"
    )

    fun connect() {
        if (disposed) return
        if (rsaKey.isBlank()) {
            connected = false
            onConnectionChanged(false)
            onMessage("کلید عمومی مایکت در تنظیمات Build موجود نیست؛ خرید فعال نشد.")
            return
        }
        if (helper != null) return

        try {
            val client = IabHelper(activity, rsaKey)
            helper = client
            client.startSetup { result ->
                activity.runOnUiThread {
                    if (disposed) return@runOnUiThread
                    connected = result.isSuccess()
                    onConnectionChanged(connected)
                    if (connected) {
                        // MainActivity observes this connection state and starts exactly one
                        // restore request with the UI callback; don't race it with an
                        // automatic no-op restore here.
                        onMessage("اتصال پرداخت مایکت برقرار شد.")
                    } else {
                        // Clear the failed client so a later connect() can retry.
                        if (helper === client) helper = null
                        runCatching { client.dispose() }
                            .onFailure { error -> Log.w(TAG, "Error disposing failed Myket client", error) }
                        onMessage("اتصال پرداخت مایکت برقرار نشد: ${result.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Myket billing setup failed", e)
            helper = null
            connected = false
            onConnectionChanged(false)
            onMessage("راه‌اندازی پرداخت مایکت ناموفق بود؛ لطفاً دوباره تلاش کنید.")
        }
    }

    fun subscribe(productId: String, onVerifiedPurchase: (String) -> Unit) {
        if (productId !in validVipProducts) {
            notifyIfActive("شناسهٔ محصول VIP معتبر نیست.")
            return
        }
        val client = helper
        if (!connected || client == null || disposed) {
            notifyIfActive("اتصال پرداخت مایکت آماده نیست؛ چند لحظه بعد دوباره تلاش کنید.")
            return
        }
        if (operationInProgress) {
            notifyIfActive("یک عملیات پرداخت در حال انجام است؛ لطفاً صبر کنید.")
            return
        }

        operationInProgress = true
        val payload = "freelancepro:$productId:${System.currentTimeMillis()}"
        try {
            client.launchPurchaseFlow(
                activity,
                productId,
                object : IabHelper.OnIabPurchaseFinishedListener {
                    override fun onIabPurchaseFinished(
                        result: IabResult,
                        purchase: Purchase?
                    ) {
                        operationInProgress = false
                        if (disposed) return
                        if (result.isFailure() || purchase == null) {
                            onMessage(
                                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {
                                    "پرداخت لغو شد؛ VIP فعال نشد."
                                } else {
                                    "خرید مایکت تأیید نشد: ${result.message}"
                                }
                            )
                            return
                        }

                        // IabHelper verifies the purchase signature with the configured RSA key
                        // before reporting a successful purchase.
                        if (
                            purchase.getSku() != productId ||
                            purchase.getPurchaseState() != 0 ||
                            purchase.getPackageName() != activity.packageName ||
                            purchase.getDeveloperPayload() != payload
                        ) {
                            notifyIfActive("اطلاعات خرید با درخواست مطابقت ندارد؛ VIP فعال نشد.")
                            return
                        }

                        onVerifiedPurchase(purchase.getSku())
                        notifyIfActive("خرید مایکت تأیید شد.")
                    }
                },
                payload
            )
        } catch (e: Exception) {
            operationInProgress = false
            Log.e(TAG, "Myket purchase flow failed", e)
            notifyIfActive("شروع پرداخت مایکت ناموفق بود؛ لطفاً دوباره تلاش کنید.")
        }
    }

    fun restoreSubscriptions(onVerifiedSubscription: (String) -> Unit = {}) {
        val client = helper
        if (!connected || client == null || disposed || operationInProgress) return

        operationInProgress = true
        try {
            client.queryInventoryAsync(
                true,
                validVipProducts.toList(),
                object : IabHelper.QueryInventoryFinishedListener {
                    override fun onQueryInventoryFinished(
                        result: IabResult,
                        inventory: Inventory?
                    ) {
                        operationInProgress = false
                        if (disposed) return
                        if (result.isFailure() || inventory == null) {
                            notifyIfActive("بازیابی خریدهای قبلی مایکت انجام نشد.")
                            return
                        }

                        inventory.getAllPurchases()
                            .filter { purchase ->
                                purchase.getSku() in validVipProducts &&
                                    purchase.getPurchaseState() == 0 &&
                                    purchase.getPackageName() == activity.packageName
                            }
                            .map { it.getSku() }
                            .distinct()
                            .forEach(onVerifiedSubscription)
                    }
                }
            )
        } catch (e: Exception) {
            operationInProgress = false
            Log.e(TAG, "Myket purchase restore failed", e)
            notifyIfActive("بازیابی خریدهای قبلی مایکت با خطا مواجه شد.")
        }
    }

    fun disconnect() {
        connected = false
        if (!activity.isFinishing && !activity.isDestroyed) onConnectionChanged(false)
        val client = helper
        helper = null
        if (client != null) {
            try {
                client.dispose()
            } catch (e: Exception) {
                Log.w(TAG, "Error disposing Myket billing client", e)
            }
        }
    }

    fun release() {
        disposed = true
        disconnect()
    }

    companion object {
        private const val TAG = "MyketBillingManager"
    }
}
