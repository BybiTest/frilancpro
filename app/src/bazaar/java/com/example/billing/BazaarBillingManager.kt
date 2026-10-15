package com.example.billing

import androidx.activity.ComponentActivity
import com.example.BuildConfig
import ir.cafebazaar.poolakey.Connection
import ir.cafebazaar.poolakey.ConnectionState
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import ir.cafebazaar.poolakey.request.PurchaseRequest

/**
 * Thin Poolakey wrapper. VIP is granted only after Poolakey reports a verified purchase
 * or a currently subscribed product returned by Bazaar's subscription query.
 */
class BazaarBillingManager(
    private val activity: ComponentActivity,
    private val onMessage: (String) -> Unit,
    private val onConnectionChanged: (Boolean) -> Unit
) {
    private val rsaKey = BuildConfig.BAZAAR_RSA_PUBLIC_KEY.trim()
    private val payment: Payment? = if (rsaKey.isBlank()) {
        null
    } else {
        Payment(
            context = activity,
            config = PaymentConfiguration(
                localSecurityCheck = SecurityCheck.Enable(rsaPublicKey = rsaKey),
                shouldSupportSubscription = true
            )
        )
    }
    private var connection: Connection? = null
    @Volatile private var released = false
    private val validVipProducts = setOf("vip_monthly", "vip_quarterly", "vip_yearly")

    fun connect() {
        if (released || activity.isFinishing || activity.isDestroyed) return
        val client = payment
        if (client == null) {
            onConnectionChanged(false)
            notifyIfActive("کلید عمومی پرداخت کافه‌بازار در تنظیمات Build موجود نیست.")
            return
        }
        connection = client.connect {
            connectionSucceed {
                activity.runOnUiThread {
                    if (released || activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                    onConnectionChanged(true)
                    onMessage("اتصال پرداخت کافه‌بازار برقرار شد.")
                }
            }
            connectionFailed {
                activity.runOnUiThread {
                    if (released || activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                    onConnectionChanged(false)
                    onMessage("اتصال به پرداخت کافه‌بازار برقرار نشد. بازار را به‌روز کنید و دوباره تلاش کنید.")
                }
            }
            disconnected {
                activity.runOnUiThread {
                    if (!released && !activity.isFinishing && !activity.isDestroyed) onConnectionChanged(false)
                }
            }
        }
    }

    fun subscribe(productId: String, onVerifiedPurchase: (String) -> Unit) {
        if (productId !in validVipProducts) {
            notifyIfActive("شناسهٔ محصول VIP معتبر نیست؛ پرداخت آغاز نشد.")
            return
        }
        val client = payment
        if (released || activity.isFinishing || activity.isDestroyed || client == null || connection?.getState() != ConnectionState.Connected) {
            notifyIfActive("در حال حاضر اتصال پرداخت کافه‌بازار آماده نیست.")
            return
        }

        client.subscribeProduct(
            registry = activity.activityResultRegistry,
            request = PurchaseRequest(
                productId = productId,
                payload = "freelancepro-${productId}-${System.currentTimeMillis()}",
                dynamicPriceToken = null
            )
        ) {
            purchaseFlowBegan {
                notifyIfActive("در حال انتقال به صفحه پرداخت کافه‌بازار…")
            }
            failedToBeginFlow {
                notifyIfActive("شروع فرایند پرداخت ناموفق بود: ${it.message ?: "خطای نامشخص"}")
            }
            purchaseSucceed { purchaseInfo ->
                if (purchaseInfo.productId == productId) {
                    activity.runOnUiThread {
                        if (released || activity.isFinishing || activity.isDestroyed) return@runOnUiThread
                        onVerifiedPurchase(purchaseInfo.productId)
                        onMessage("خرید اشتراک در کافه‌بازار تأیید شد.")
                    }
                } else {
                    notifyIfActive("شناسه محصول خریداری‌شده با طرح انتخابی مطابقت ندارد؛ اشتراک فعال نشد.")
                }
            }
            purchaseCanceled {
                notifyIfActive("پرداخت لغو شد؛ اشتراکی فعال نشد.")
            }
            purchaseFailed { error ->
                notifyIfActive("پرداخت تأیید نشد: ${error.message ?: "خطای نامشخص"}")
            }
        }
    }

    fun restoreSubscriptions(onVerifiedSubscription: (String) -> Unit) {
        val client = payment
        if (released || activity.isFinishing || activity.isDestroyed || client == null || connection?.getState() != ConnectionState.Connected) return

        client.getSubscribedProducts {
            querySucceed { purchases ->
                val validIds = setOf("vip_monthly", "vip_quarterly", "vip_yearly")
                purchases
                    .map { it.productId }
                    .filter { it in validIds }
                    .distinct()
                    .forEach { productId ->
                        if (!released && !activity.isFinishing && !activity.isDestroyed) onVerifiedSubscription(productId)
                    }
            }
            queryFailed {
                notifyIfActive("بررسی اشتراک‌های قبلی انجام نشد؛ لطفاً بعداً دوباره تلاش کنید.")
            }
        }
    }

    fun disconnect() {
        connection?.disconnect()
        connection = null
        if (!released && !activity.isFinishing && !activity.isDestroyed) onConnectionChanged(false)
    }

    private fun notifyIfActive(message: String) {
        if (released || activity.isFinishing || activity.isDestroyed) return
        activity.runOnUiThread {
            if (!released && !activity.isFinishing && !activity.isDestroyed) onMessage(message)
        }
    }

    fun release() {
        released = true
        disconnect()
    }
}
