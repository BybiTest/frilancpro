package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FreelanceRepository
import com.example.data.GeminiAiService
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab(val titleFa: String, val titleEn: String) {
    RESUME_BUILDER("رزومه", "Resume"),
    PUBLIC_PORTFOLIO("پورتفولیو", "Portfolio"),
    AI_CAREER("دستیار AI", "AI Career"),
    RATE_PRICING("نرخ‌گذاری", "Pricing"),
    CLIENT_CRM("پروژه‌ها", "Projects"),
    SETTINGS("تنظیمات", "Settings")
}

class FreelanceViewModel(
    application: Application,
    private val repository: FreelanceRepository = FreelanceRepository(application),
    private val aiService: GeminiAiService = GeminiAiService()
) : AndroidViewModel(application) {

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val resumes: StateFlow<List<ResumeData>> = repository.resumes
    val activeResume: StateFlow<ResumeData> = repository.activeResume
    val portfolioProjects: StateFlow<List<PortfolioProject>> = repository.portfolioProjects
    val clientProjects: StateFlow<List<ClientProject>> = repository.clientProjects
    val interviewScenarios = repository.interviewScenarios

    private val _currentTab = MutableStateFlow(AppTab.RESUME_BUILDER)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Settings are stored locally so user preferences survive app restarts.
    private val settingsPreferences = application.getSharedPreferences("freelancepro_settings", android.content.Context.MODE_PRIVATE)

    private fun loadAppSettings(): AppSettings {
        val defaults = AppSettings()
        return AppSettings(
            language = runCatching { AppLanguage.valueOf(settingsPreferences.getString("language", defaults.language.name) ?: defaults.language.name) }.getOrDefault(defaults.language),
            fontScale = runCatching { FontScale.valueOf(settingsPreferences.getString("font_scale", defaults.fontScale.name) ?: defaults.fontScale.name) }.getOrDefault(defaults.fontScale),
            themeColor = runCatching { ThemeColorChoice.valueOf(settingsPreferences.getString("theme_color", defaults.themeColor.name) ?: defaults.themeColor.name) }.getOrDefault(defaults.themeColor),
            isDarkMode = settingsPreferences.getBoolean("dark_mode", defaults.isDarkMode),
            paymentGateway = runCatching { PaymentGateway.valueOf(settingsPreferences.getString("payment_gateway", defaults.paymentGateway.name) ?: defaults.paymentGateway.name) }.getOrDefault(defaults.paymentGateway)
        )
    }

    private fun persistAppSettings(settings: AppSettings) {
        settingsPreferences.edit()
            .putString("language", settings.language.name)
            .putString("font_scale", settings.fontScale.name)
            .putString("theme_color", settings.themeColor.name)
            .putBoolean("dark_mode", settings.isDarkMode)
            .putString("payment_gateway", settings.paymentGateway.name)
            .apply()
    }

    private val _appSettings = MutableStateFlow(loadAppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _showVipModal = MutableStateFlow(false)
    val showVipModal: StateFlow<Boolean> = _showVipModal.asStateFlow()

    // ATS Scanner State
    private val _isAtsScanning = MutableStateFlow(false)
    val isAtsScanning: StateFlow<Boolean> = _isAtsScanning.asStateFlow()

    private val _atsResult = MutableStateFlow<AtsScanResult?>(null)
    val atsResult: StateFlow<AtsScanResult?> = _atsResult.asStateFlow()

    // Proposal Generator State
    private val _isGeneratingProposal = MutableStateFlow(false)
    val isGeneratingProposal: StateFlow<Boolean> = _isGeneratingProposal.asStateFlow()

    private val _generatedProposal = MutableStateFlow<GeneratedProposal?>(null)
    val generatedProposal: StateFlow<GeneratedProposal?> = _generatedProposal.asStateFlow()

    // Rate Calculator State
    private val _rateInput = MutableStateFlow(RateCalculatorInput())
    val rateInput: StateFlow<RateCalculatorInput> = _rateInput.asStateFlow()

    private val _rateOutput = MutableStateFlow(repository.calculateFreelanceRate(RateCalculatorInput()))
    val rateOutput: StateFlow<RateCalculationOutput> = _rateOutput.asStateFlow()

    // Ad Simulation Status
    private val _activeAdMessage = MutableStateFlow<String?>(null)
    val activeAdMessage: StateFlow<String?> = _activeAdMessage.asStateFlow()

    // UI Message/Snackbar
    private val _uiNotice = MutableStateFlow<String?>(null)
    val uiNotice: StateFlow<String?> = _uiNotice.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setShowVipModal(show: Boolean) {
        _showVipModal.value = show
    }

    fun dismissNotice() {
        _uiNotice.value = null
    }

    fun dismissAdMessage() {
        _activeAdMessage.value = null
    }

    // Settings actions
    fun setLanguage(lang: AppLanguage) {
        _appSettings.update { it.copy(language = lang) }
        persistAppSettings(_appSettings.value)
        val msg = if (lang == AppLanguage.PERSIAN) "زبان برنامه به فارسی تغییر کرد." else "App language set to English."
        _uiNotice.value = msg
    }

    fun setFontScale(scale: FontScale) {
        _appSettings.update { it.copy(fontScale = scale) }
        persistAppSettings(_appSettings.value)
        val msg = if (_appSettings.value.language == AppLanguage.PERSIAN)
            "اندازه قلم به «${scale.titleFa}» تنظیم شد."
        else
            "Font size set to ${scale.titleEn}."
        _uiNotice.value = msg
    }

    fun setThemeColor(color: ThemeColorChoice) {
        _appSettings.update { it.copy(themeColor = color) }
        persistAppSettings(_appSettings.value)
        val msg = if (_appSettings.value.language == AppLanguage.PERSIAN)
            "تم رنگی برنامه به «${color.titleFa}» تغییر یافت."
        else
            "Theme color changed to ${color.titleEn}."
        _uiNotice.value = msg
    }

    fun toggleDarkMode(isDark: Boolean) {
        _appSettings.update { it.copy(isDarkMode = isDark) }
        persistAppSettings(_appSettings.value)
        val msg = if (_appSettings.value.language == AppLanguage.PERSIAN)
            if (isDark) "حالت شب (Dark Mode) فعال شد." else "حالت روز (Light Mode) فعال شد."
        else
            if (isDark) "Dark mode enabled." else "Light mode enabled."
        _uiNotice.value = msg
    }

    fun setPaymentGateway(gateway: PaymentGateway) {
        _appSettings.update { it.copy(paymentGateway = gateway) }
        persistAppSettings(_appSettings.value)
    }

    fun simulateShowInterstitialAd() {
        _activeAdMessage.value = "تبلیغات هنوز به SDK واقعی تپسل متصل نشده‌اند؛ در حال حاضر تبلیغی نمایش داده نمی‌شود."
    }

    fun simulateShowRewardedAd() {
        // Never grant credits without a real rewarded-ad completion callback.
        _activeAdMessage.value = "تبلیغ جایزه‌ای هنوز به SDK واقعی تپسل متصل نیست؛ اعتباری اضافه نشد."
        _uiNotice.value = "برای دریافت اعتبار رایگان، اتصال تبلیغ جایزه‌ای باید تکمیل شود."
    }

    fun simulatePurchaseSubscription(gateway: PaymentGateway, planName: String) {
        val storeName = if (gateway == PaymentGateway.CAFE_BAZAAR) "کافه بازار" else "مایکت"
        _uiNotice.value = "پرداخت $planName از طریق $storeName هنوز پیاده‌سازی نشده است؛ اشتراک فعال نشده."
    }

    fun updateActiveResume(updated: ResumeData) {
        repository.updateActiveResume(updated)
        _uiNotice.value = "تغییرات رزومه با موفقیت ذخیره شد."
    }

    fun switchResume(resume: ResumeData) {
        repository.selectResume(resume)
        _uiNotice.value = "رزومه فعال به «${resume.title}» تغییر کرد."
    }

    fun createNewResume(title: String) {
        repository.addResume(title)
        _uiNotice.value = "رزومه جدید «$title» ساخته شد."
    }

    fun runAtsAnalysis(jobDescription: String) {
        if (_isAtsScanning.value) return
        if (jobDescription.isBlank()) {
            _uiNotice.value = "ابتدا شرح موقعیت شغلی را وارد کنید."
            return
        }
        if (!repository.consumeAiCredit()) {
            _showVipModal.value = true
            return
        }

        viewModelScope.launch {
            _isAtsScanning.value = true
            try {
                val result = aiService.analyzeResumeWithAts(activeResume.value, jobDescription)
                _atsResult.value = result
            } catch (error: Exception) {
                repository.refundAiCredit()
                _uiNotice.value = "تحلیل رزومه انجام نشد؛ اعتبار مصرف‌شده بازگردانده شد."
            } finally {
                _isAtsScanning.value = false
            }
        }
    }

    fun generateProposal(projectTitle: String, projectDetails: String, tone: String) {
        if (_isGeneratingProposal.value) return
        if (projectTitle.isBlank() || projectDetails.isBlank()) {
            _uiNotice.value = "عنوان پروژه و شرح نیاز کارفرما را وارد کنید."
            return
        }
        if (!repository.consumeAiCredit()) {
            _showVipModal.value = true
            return
        }

        viewModelScope.launch {
            _isGeneratingProposal.value = true
            try {
                val proposal = aiService.generateProposal(
                    projectTitle = projectTitle,
                    projectDetails = projectDetails,
                    freelancerRole = activeResume.value.jobTitle,
                    tone = tone
                )
                _generatedProposal.value = proposal
            } catch (error: Exception) {
                repository.refundAiCredit()
                _uiNotice.value = "ساخت پیشنهاد انجام نشد؛ اعتبار مصرف‌شده بازگردانده شد."
            } finally {
                _isGeneratingProposal.value = false
            }
        }
    }

    fun updateRateInput(newInput: RateCalculatorInput) {
        _rateInput.value = newInput
        _rateOutput.value = repository.calculateFreelanceRate(newInput)
    }

    fun addClientProject(project: ClientProject) {
        repository.addClientProject(project)
        _uiNotice.value = "پروژه جدید کارفرما افزوده شد."
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        repository.updateProjectStatus(projectId, newStatus)
    }

    fun addPortfolioProject(item: PortfolioProject) {
        repository.addPortfolioProject(item)
        _uiNotice.value = "نمونه کار جدید به پورتفولیو اضافه شد."
    }

    fun activateVerifiedSubscription(productId: String) {
        if (productId !in setOf("vip_monthly", "vip_quarterly", "vip_yearly")) {
            _uiNotice.value = "شناسه اشتراک معتبر نیست؛ VIP فعال نشد."
            return
        }
        // Called after the selected store's billing flow verifies the purchase.
        repository.upgradeToVip()
        val storeName = if (com.example.BuildConfig.STORE_FLAVOR == "myket") "مایکت" else "کافه‌بازار"
        _uiNotice.value = when (productId) {
            "vip_monthly" -> "اشتراک یک‌ماهه VIP توسط $storeName تأیید شد."
            "vip_quarterly" -> "اشتراک سه‌ماهه VIP توسط $storeName تأیید شد."
            else -> "اشتراک یک‌ساله VIP توسط $storeName تأیید شد."
        }
    }
    fun grantRewardedAdCredits() {
        repository.addRewardAiCredits(3)
        _showVipModal.value = false
        _uiNotice.value = "تبلیغ با موفقیت تکمیل شد؛ ۳ اعتبار به حساب شما اضافه شد."
    }
}
