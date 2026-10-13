package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.billing.BazaarBillingManager
import com.example.ads.TapsellAdsManager
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.FreelanceViewModel

class MainActivity : ComponentActivity() {
    private var billingConnected by mutableStateOf(false)
    private lateinit var billingManager: BazaarBillingManager
    private lateinit var tapsellManager: TapsellAdsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        billingManager = BazaarBillingManager(
            activity = this,
            onMessage = { message -> runOnUiThread {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } },
            onConnectionChanged = { connected -> billingConnected = connected }
        )
        billingManager.connect()
        tapsellManager = TapsellAdsManager(
            activity = this,
            onMessage = { message -> runOnUiThread {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } }
        )
        tapsellManager.initialize()

        setContent {
            val viewModel: FreelanceViewModel = viewModel()
            val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

            MyApplicationTheme(appSettings = appSettings) {
                val layoutDirection = if (appSettings.language == AppLanguage.PERSIAN) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    FreelanceProApp(
                        viewModel = viewModel,
                        appSettings = appSettings,
                        billingConnected = billingConnected,
                        onPurchaseSubscription = { gateway, planName ->
                            if (BuildConfig.STORE_FLAVOR == "myket") {
                                Toast.makeText(this, "پرداخت مایکت هنوز به SDK و محصولات این برنامه متصل نشده است؛ خرید انجام نشد.", Toast.LENGTH_LONG).show()
                            } else if (gateway != PaymentGateway.CAFE_BAZAAR) {
                                Toast.makeText(this, "در نسخه کافه‌بازار، خرید فقط از درگاه کافه‌بازار پشتیبانی می‌شود.", Toast.LENGTH_LONG).show()
                            } else {
                                val productId = when (planName) {
                                    "۱ ماهه" -> "vip_monthly"
                                    "۳ ماهه" -> "vip_quarterly"
                                    else -> "vip_yearly"
                                }
                                billingManager.subscribe(productId) { verifiedId ->
                                    viewModel.activateVerifiedSubscription(verifiedId)
                                }
                            }
                        },
                        onRestoreSubscriptions = { onRestored ->
                            billingManager.restoreSubscriptions(onRestored)
                        },
                        onShowInterstitialAd = { tapsellManager.showInterstitial() },
                        onShowRewardedAd = {
                            tapsellManager.showRewarded { viewModel.grantRewardedAdCredits() }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        if (::billingManager.isInitialized) billingManager.disconnect()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelanceProApp(
    viewModel: FreelanceViewModel,
    appSettings: AppSettings,
    billingConnected: Boolean,
    onPurchaseSubscription: (PaymentGateway, String) -> Unit,
    onRestoreSubscriptions: ((String) -> Unit) -> Unit,
    onShowInterstitialAd: () -> Unit,
    onShowRewardedAd: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeResume by viewModel.activeResume.collectAsStateWithLifecycle()
    val allResumes by viewModel.resumes.collectAsStateWithLifecycle()
    val portfolioProjects by viewModel.portfolioProjects.collectAsStateWithLifecycle()
    val clientProjects by viewModel.clientProjects.collectAsStateWithLifecycle()
    val isAtsScanning by viewModel.isAtsScanning.collectAsStateWithLifecycle()
    val atsResult by viewModel.atsResult.collectAsStateWithLifecycle()
    val isGeneratingProposal by viewModel.isGeneratingProposal.collectAsStateWithLifecycle()
    val generatedProposal by viewModel.generatedProposal.collectAsStateWithLifecycle()
    val rateInput by viewModel.rateInput.collectAsStateWithLifecycle()
    val rateOutput by viewModel.rateOutput.collectAsStateWithLifecycle()
    val showVipModal by viewModel.showVipModal.collectAsStateWithLifecycle()
    val activeAdMessage by viewModel.activeAdMessage.collectAsStateWithLifecycle()
    val uiNotice by viewModel.uiNotice.collectAsStateWithLifecycle()

    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    val lang = appSettings.language
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiNotice) {
        uiNotice?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissNotice()
        }
    }

    LaunchedEffect(billingConnected) {
        if (billingConnected) {
            onRestoreSubscriptions { productId ->
                viewModel.activateVerifiedSubscription(productId)
            }
        }
    }

    if (currentTab != AppTab.RESUME_BUILDER) {
        BackHandler {
            viewModel.selectTab(AppTab.RESUME_BUILDER)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Atmospheric subtle background image layer with blur effect
        Image(
            painter = painterResource(id = R.drawable.img_app_background_1791818798692),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (colors.isDark) 0.18f else 0.08f),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(colors.primary, colors.secondary))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    tint = Navy900,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.appTitle(lang),
                                    color = colors.textPrimary,
                                    fontSize = (16 * fontScale).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppStrings.appSubtitle(lang),
                                    color = colors.textSecondary,
                                    fontSize = (10 * fontScale).sp
                                )
                            }
                        }
                    },
                    actions = {
                        // AI Credits / VIP badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(colors.surface)
                                .border(1.5.dp, colors.primary, RoundedCornerShape(20.dp))
                                .clickable { viewModel.setShowVipModal(true) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .testTag("vip_header_badge"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (userProfile.tier == SubscriptionTier.VIP_PRO) Icons.Default.WorkspacePremium else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (userProfile.tier == SubscriptionTier.VIP_PRO) "VIP" else "${userProfile.aiCredits} ${if (lang == AppLanguage.PERSIAN) "اعتبار" else "cr"}",
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (11 * fontScale).sp
                            )
                        }

                        // Quick settings button
                        IconButton(
                            onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                            modifier = Modifier.testTag("top_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "تنظیمات",
                                tint = if (currentTab == AppTab.SETTINGS) colors.primary else colors.textSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.headerBackground
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(listOf(colors.primary.copy(alpha = 0.3f), colors.secondary.copy(alpha = 0.3f))),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .testTag("main_bottom_nav"),
                    containerColor = colors.navBackground,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.RESUME_BUILDER,
                        onClick = { viewModel.selectTab(AppTab.RESUME_BUILDER) },
                        icon = { Icon(Icons.Default.Description, contentDescription = "رزومه") },
                        label = { Text(AppStrings.tabResume(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.PUBLIC_PORTFOLIO,
                        onClick = { viewModel.selectTab(AppTab.PUBLIC_PORTFOLIO) },
                        icon = { Icon(Icons.Default.Language, contentDescription = "پورتفولیو") },
                        label = { Text(AppStrings.tabPortfolio(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.AI_CAREER,
                        onClick = { viewModel.selectTab(AppTab.AI_CAREER) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "دستیار AI") },
                        label = { Text(AppStrings.tabAi(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.RATE_PRICING,
                        onClick = { viewModel.selectTab(AppTab.RATE_PRICING) },
                        icon = { Icon(Icons.Default.Calculate, contentDescription = "نرخ‌گذاری") },
                        label = { Text(AppStrings.tabRate(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.CLIENT_CRM,
                        onClick = { viewModel.selectTab(AppTab.CLIENT_CRM) },
                        icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "پروژه‌ها") },
                        label = { Text(AppStrings.tabCrm(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )

                    NavigationBarItem(
                        selected = currentTab == AppTab.SETTINGS,
                        onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "تنظیمات") },
                        label = { Text(AppStrings.tabSettings(lang), fontSize = (10 * fontScale).sp) },
                        colors = navigationItemColors(colors.primary)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.RESUME_BUILDER -> {
                        ResumeBuilderScreen(
                            activeResume = activeResume,
                            allResumes = allResumes,
                            onUpdateResume = { viewModel.updateActiveResume(it) },
                            onSelectResume = { viewModel.switchResume(it) },
                            onCreateResume = { viewModel.createNewResume(it) },
                            onRequestAiAnalysis = { viewModel.selectTab(AppTab.AI_CAREER) }
                        )
                    }

                    AppTab.PUBLIC_PORTFOLIO -> {
                        PublicPortfolioScreen(
                            userProfile = userProfile,
                            portfolioProjects = portfolioProjects,
                            onAddProjectClick = { viewModel.setShowVipModal(true) },
                            onCopyLink = {
                                clipboardManager.setText(AnnotatedString("https://freelancepro.me/@${userProfile.publicPortfolioSlug}"))
                            }
                        )
                    }

                    AppTab.AI_CAREER -> {
                        AiAssistantScreen(
                            userCredits = userProfile.aiCredits,
                            isVip = userProfile.tier == SubscriptionTier.VIP_PRO,
                            isAtsScanning = isAtsScanning,
                            atsResult = atsResult,
                            isGeneratingProposal = isGeneratingProposal,
                            generatedProposal = generatedProposal,
                            interviewScenarios = viewModel.interviewScenarios,
                            onRunAtsScan = { viewModel.runAtsAnalysis(it) },
                            onGenerateProposal = { t, d, tone -> viewModel.generateProposal(t, d, tone) },
                            onOpenVipModal = { viewModel.setShowVipModal(true) }
                        )
                    }

                    AppTab.RATE_PRICING -> {
                        RateCalculatorScreen(
                            rateInput = rateInput,
                            rateOutput = rateOutput,
                            onInputChange = { viewModel.updateRateInput(it) }
                        )
                    }

                    AppTab.CLIENT_CRM -> {
                        ClientCrmScreen(
                            clientProjects = clientProjects,
                            onAddProject = { viewModel.addClientProject(it) },
                            onStatusChange = { id, s -> viewModel.updateProjectStatus(id, s) }
                        )
                    }

                    AppTab.SETTINGS -> {
                        SettingsScreen(
                            appSettings = appSettings,
                            userCredits = userProfile.aiCredits,
                            isVip = userProfile.tier == SubscriptionTier.VIP_PRO,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            onFontScaleChange = { viewModel.setFontScale(it) },
                            onThemeColorChange = { viewModel.setThemeColor(it) },
                            onDarkModeToggle = { viewModel.toggleDarkMode(it) },
                            onPaymentGatewayChange = { viewModel.setPaymentGateway(it) },
                            onShowInterstitialAd = onShowInterstitialAd,
                            onShowRewardedAd = onShowRewardedAd,
                            onPurchasePlan = { gateway, plan -> onPurchaseSubscription(gateway, plan) }
                        )
                    }
                }
            }
        }
    }

    // Ad Simulation Dialog
    if (activeAdMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAdMessage() },
            icon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = colors.primary) },
            title = { Text(if (lang == AppLanguage.PERSIAN) "تبلیغات هوشمند تپسل / تپسی" else "Tapsell / Tapsi Ad Engine") },
            text = { Text(activeAdMessage ?: "") },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissAdMessage() },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                ) {
                    Text(if (lang == AppLanguage.PERSIAN) "بستن تبلیغ" else "Close Ad")
                }
            },
            containerColor = colors.surface
        )
    }

    if (showVipModal) {
        VipUpgradeDialog(
            currentCredits = userProfile.aiCredits,
            onDismiss = { viewModel.setShowVipModal(false) },
            onUpgrade = { onPurchaseSubscription(PaymentGateway.CAFE_BAZAAR, "سالانه") },
            onWatchAd = onShowRewardedAd
        )
    }
}

@Composable
private fun navigationItemColors(primaryColor: Color) = NavigationBarItemDefaults.colors(
    selectedIconColor = Navy900,
    selectedTextColor = primaryColor,
    indicatorColor = primaryColor,
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)
