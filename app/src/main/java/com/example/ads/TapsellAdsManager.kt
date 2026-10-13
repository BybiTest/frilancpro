package com.example.ads

import android.util.Log
import androidx.activity.ComponentActivity
import com.example.BuildConfig
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import java.util.concurrent.atomic.AtomicBoolean

/** Tapsell Plus wrapper. Credits are awarded only from the SDK's onRewarded callback. */
class TapsellAdsManager(
    private val activity: ComponentActivity,
    private val onMessage: (String) -> Unit
) {
    private val appKey = BuildConfig.TAPSELL_APP_KEY.trim()
    @Volatile private var initialized = false

    fun initialize() {
        if (appKey.isBlank()) {
            onMessage("کلید برنامه تپسل در GitHub Actions تنظیم نشده است؛ تبلیغات فعال نشد.")
            return
        }
        TapsellPlus.initialize(activity, appKey, object : TapsellPlusInitListener {
            override fun onInitializeSuccess(adNetworks: AdNetworks) {
                initialized = true
                onMessage("اتصال تپسل برقرار شد.")
            }

            override fun onInitializeFailed(adNetworks: AdNetworks, error: AdNetworkError) {
                initialized = false
                onMessage("راه‌اندازی تپسل ناموفق بود: ${error.errorMessage}")
            }
        })
    }

    fun showRewarded(onRewarded: () -> Unit) {
        if (!initialized) {
            onMessage("تبلیغ جایزه‌ای آماده نیست؛ اعتباری اضافه نشد.")
            return
        }
        TapsellPlus.requestRewardedVideoAd(
            activity,
            REWARDED_ZONE_ID,
            object : AdRequestCallback() {
                override fun response(ad: TapsellPlusAdModel) {
                    super.response(ad)
                    val rewardDelivered = AtomicBoolean(false)
                    TapsellPlus.showRewardedVideoAd(
                        activity,
                        ad.responseId,
                        object : AdShowListener() {
                            override fun onRewarded(adModel: TapsellPlusAdModel) {
                                super.onRewarded(adModel)
                                if (rewardDelivered.compareAndSet(false, true)) {
                                    activity.runOnUiThread { onRewarded() }
                                }
                            }

                            override fun onError(error: TapsellPlusErrorModel) {
                                super.onError(error)
                                activity.runOnUiThread {
                                    onMessage("نمایش تبلیغ با خطا روبه‌رو شد؛ اعتباری اضافه نشد.")
                                }
                            }
                        }
                    )
                }

                override fun error(message: String) {
                    activity.runOnUiThread {
                        onMessage("تبلیغ جایزه‌ای دریافت نشد: $message")
                    }
                }
            }
        )
    }

    fun showInterstitial() {
        if (!initialized) {
            onMessage("تبلیغات تپسل هنوز آماده نیست.")
            return
        }
        TapsellPlus.requestInterstitialAd(
            activity,
            INTERSTITIAL_ZONE_ID,
            object : AdRequestCallback() {
                override fun response(ad: TapsellPlusAdModel) {
                    super.response(ad)
                    TapsellPlus.showInterstitialAd(
                        activity,
                        ad.responseId,
                        object : AdShowListener() {
                            override fun onError(error: TapsellPlusErrorModel) {
                                super.onError(error)
                                activity.runOnUiThread { onMessage("نمایش تبلیغ بین‌صفحه‌ای ناموفق بود.") }
                            }
                        }
                    )
                }

                override fun error(message: String) {
                    activity.runOnUiThread {
                        onMessage("تبلیغ بین‌صفحه‌ای دریافت نشد: $message")
                    }
                }
            }
        )
    }

    companion object {
        private const val REWARDED_ZONE_ID = "6acd1cf7b6da30468f7f5f82"
        private const val INTERSTITIAL_ZONE_ID = "6acd1d35b6da30468f7f5f83"
    }
}
