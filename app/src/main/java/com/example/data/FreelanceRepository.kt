package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FreelanceRepository(context: Context? = null) {

    private val resumePreferences = context?.applicationContext
        ?.getSharedPreferences("freelancepro_resume_storage", Context.MODE_PRIVATE)
    private val resumeListAdapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter<List<ResumeData>>(
            Types.newParameterizedType(List::class.java, ResumeData::class.java)
        )

    private fun loadSavedResumes(): List<ResumeData>? {
        val json = resumePreferences?.getString("resumes_json", null) ?: return null
        return runCatching { resumeListAdapter.fromJson(json) }.getOrNull()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun persistResumes(resumes: List<ResumeData>, activeId: String) {
        val preferences = resumePreferences ?: return
        runCatching {
            val json = resumeListAdapter.toJson(resumes)
            preferences.edit()
                .putString("resumes_json", json)
                .putString("active_resume_id", activeId)
                .commit()
        }
    }


    private val profilePreferences = context?.applicationContext
        ?.getSharedPreferences("freelancepro_user_profile", Context.MODE_PRIVATE)

    private fun loadUserProfile(): UserProfile {
        val preferences = profilePreferences ?: return UserProfile()
        val defaults = UserProfile()
        return UserProfile(
            username = preferences.getString("username", defaults.username) ?: defaults.username,
            displayName = preferences.getString("display_name", defaults.displayName) ?: defaults.displayName,
            email = preferences.getString("email", defaults.email) ?: defaults.email,
            // VIP access is restored only after the store verifies an active subscription.
            tier = defaults.tier,
            aiCredits = preferences.getInt("ai_credits", defaults.aiCredits).coerceAtLeast(0),
            publicPortfolioSlug = preferences.getString("portfolio_slug", defaults.publicPortfolioSlug) ?: defaults.publicPortfolioSlug,
            isPortfolioPublic = preferences.getBoolean("portfolio_public", defaults.isPortfolioPublic),
            portfolioViewsCount = preferences.getInt("portfolio_views", defaults.portfolioViewsCount).coerceAtLeast(0),
            clientContactCount = preferences.getInt("client_contacts", defaults.clientContactCount).coerceAtLeast(0)
        )
    }

    private fun persistUserProfile(profile: UserProfile) {
        profilePreferences?.edit()
            ?.putString("username", profile.username)
            ?.putString("display_name", profile.displayName)
            ?.putString("email", profile.email)
            ?.putString("tier", profile.tier.name)
            ?.putInt("ai_credits", profile.aiCredits.coerceAtLeast(0))
            ?.putString("portfolio_slug", profile.publicPortfolioSlug)
            ?.putBoolean("portfolio_public", profile.isPortfolioPublic)
            ?.putInt("portfolio_views", profile.portfolioViewsCount.coerceAtLeast(0))
            ?.putInt("client_contacts", profile.clientContactCount.coerceAtLeast(0))
            ?.apply()
    }

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _resumes = MutableStateFlow(
        loadSavedResumes() ?: listOf(
            ResumeData(
                id = "default_1",
                title = "رزومه تخصصی دیزاین سیستم و UI/UX",
                fullName = "علی رضایی",
                jobTitle = "طراح ارشد محصول و رابط کاربری (UI/UX)",
                bio = "متخصص طراحی سیستم‌های طراحی منسجم و بهینه‌سازی تجربه کاربر برای محصولات مقیاس‌پذیر با بیش از ۵ سال سابقه دورکاری بین‌المللی."
            ),
            ResumeData(
                id = "default_2",
                title = "رزومه فریلنسری بین‌المللی (انگلیسی)",
                fullName = "Ali Rezaei",
                jobTitle = "Senior Product & UI/UX Designer",
                bio = "Crafting high-converting digital products, mobile apps, and robust design systems for global clients on Upwork and Contra."
            )
        )
    )
    val resumes: StateFlow<List<ResumeData>> = _resumes.asStateFlow()

    private val _activeResume = MutableStateFlow(
        _resumes.value.firstOrNull {
            it.id == resumePreferences?.getString("active_resume_id", null)
        } ?: _resumes.value.first()
    )
    val activeResume: StateFlow<ResumeData> = _activeResume.asStateFlow()

    private val _portfolioProjects = MutableStateFlow(
        listOf(
            PortfolioProject(
                title = "طراحی مجدد اپلیکیشن فین‌تک پی‌والت",
                category = "اپلیکیشن موبایل",
                clientName = "شرکت مالی آریا",
                description = "ساده‌سازی فرآیند انتقال وجه و افتتاح حساب آنلاین در ۳ مرحله ساده با بازخورد عالی کاربران.",
                metrics = "۴۰٪ افزایش تراکنش‌های روزانه",
                projectUrl = "https://dribbble.com/shots/example-1",
                tags = listOf("Figma", "Fintech", "UX Research", "Design System")
            ),
            PortfolioProject(
                title = "طراحی داشبورد تحلیلی ابری SaaS",
                category = "داشبورد و وب",
                clientName = "استارتاپ داده‌گستر",
                description = "داشبورد مدیریت کمپین‌های تبلیغاتی با نمودارهای تحلیلی و دسترسی تیمی زنده.",
                metrics = "کاهش ۶۰ درصدی زمان آموزش کاربران جدید",
                projectUrl = "https://dribbble.com/shots/example-2",
                tags = listOf("Dashboard", "Data Viz", "SaaS", "Tailwind UI")
            ),
            PortfolioProject(
                title = "طراحی لندینگ پیج و هویت بصری گیم‌شاپ",
                category = "برندینگ و وب",
                clientName = "استودیو بازی نکسوس",
                description = "طراحی بصری تیره (Dark Mode) با انیمیشن‌های میکرو و تعاملی برای گیمرها.",
                metrics = "نرخ تبدیل ۲۸ درصدی خرید بازی",
                projectUrl = "https://dribbble.com/shots/example-3",
                tags = listOf("Gaming", "Dark Mode", "Micro-interactions")
            )
        )
    )
    val portfolioProjects: StateFlow<List<PortfolioProject>> = _portfolioProjects.asStateFlow()

    private val portfolioPreferences = context?.applicationContext
        ?.getSharedPreferences("freelancepro_portfolio", Context.MODE_PRIVATE)
    private val portfolioListAdapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter<List<PortfolioProject>>(
            Types.newParameterizedType(List::class.java, PortfolioProject::class.java)
        )

    init {
        val savedPortfolio = portfolioPreferences?.getString("portfolio_projects_json", null)
        if (savedPortfolio != null) {
            runCatching { portfolioListAdapter.fromJson(savedPortfolio) }
                .getOrNull()
                ?.let { _portfolioProjects.value = it }
        }
    }

    private fun persistPortfolioProjects(projects: List<PortfolioProject>) {
        runCatching {
            portfolioPreferences?.edit()
                ?.putString("portfolio_projects_json", portfolioListAdapter.toJson(projects))
                ?.apply()
        }
    }

    private val clientProjectPreferences = context?.applicationContext
        ?.getSharedPreferences("freelancepro_client_projects", Context.MODE_PRIVATE)
    private val clientProjectListAdapter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter<List<ClientProject>>(
            Types.newParameterizedType(List::class.java, ClientProject::class.java)
        )

    private fun loadSavedClientProjects(): List<ClientProject>? {
        val json = clientProjectPreferences?.getString("client_projects_json", null) ?: return null
        return runCatching { clientProjectListAdapter.fromJson(json) }.getOrNull()
    }

    private fun persistClientProjects(projects: List<ClientProject>) {
        val preferences = clientProjectPreferences ?: return
        runCatching {
            preferences.edit()
                .putString("client_projects_json", clientProjectListAdapter.toJson(projects))
                .commit()
        }
    }

    private val _clientProjects = MutableStateFlow(loadSavedClientProjects() ?: defaultProjects)
    val clientProjects: StateFlow<List<ClientProject>> = _clientProjects.asStateFlow()

    val interviewScenarios = listOf(
        InterviewScenario(
            id = "q1",
            category = "مذاکره قیمت و تخفیف",
            question = "کارفرما می‌گوید: «قیمت شما نسبت به بقیه پیشنهادها خیلی بالاست، آیا می‌توانید ۵۰ درصد تخفیف بدهید تا کار را شروع کنیم؟» چطور پاسخ می‌دهید؟",
            intentBehindQuestion = "کارفرما می‌خواهد ببیند آیا شما روی ارزش کار خود تسلط دارید یا فوراً قیمت را می‌شکنید و کیفیت را فدا می‌کنید.",
            winningPoints = listOf(
                "هرگز قیمت را بدون کم کردن حجم کار (Scope) کاهش ندهید.",
                "تمرکز را از «هزینه» به «بازگشت سرمایه و درآمد کارفرما» هدایت کنید.",
                "یک نسخه کوچک‌تر یا MVP از پروژه با بودجه کمتر پیشنهاد دهید."
            ),
            sampleIdealAnswer = "کاملاً درک می‌کنم که مدیریت بودجه برای شما مهم است. قیمت من بر اساس تضمین کیفیت، پشتیبانی پس از تحویل و سودآوری مستقیم این محصول برای کسب‌وکار شما تنظیم شده است. اگر سقف بودجه شما محدود است، می‌توانیم امکانات نسخه اول را تفکیک کنیم و بخش‌های اساسی‌تر را با بودجه فعلی شما انجام دهیم."
        ),
        InterviewScenario(
            id = "q2",
            category = "مدیریت تغییرات بی‌پایان (Scope Creep)",
            question = "در میانه پروژه، کارفرما بدون پرداخت هزینه اضافه، ۵ ویژگی جدید و پیچیده درخواست می‌کند. برخورد شما چیست؟",
            intentBehindQuestion = "سنجش مرزهای حرفه‌ای شما و مدیریت انتظارات کارفرما بدون ایجاد دلخوری.",
            winningPoints = listOf(
                "با آرامش از ایده‌های جدید کارفرما استقبال کنید.",
                "قرارداد اولیه و سند تحویل (SOW) را یادآوری کنید.",
                "این امکانات جدید را به عنوان «فاز دوم یا الحاقیه قرارداد» قیمت‌گذاری کنید."
            ),
            sampleIdealAnswer = "این ایده‌های جدید واقعاً فوق‌العاده هستند و محصول را جذاب‌تر می‌کنند! از آنجا که در فاز اول روی موارد مشخصی توافق کرده بودیم، پیشنهاد می‌کنم ابتدا فاز اول را سر موعد تحویل دهیم و بلافاصله این ویژگی‌های جدید را در قالب فاز دوم با زمان‌بندی و برآورد مجزا پیاده‌سازی کنیم."
        ),
        InterviewScenario(
            id = "q3",
            category = "تاخیر و موعد تحویل (Deadlines)",
            question = "اگر به دلیل مشکل فنی یا بیماری پیش‌بینی نشده متوجه شدید پروژه با ۲ روز تاخیر تحویل داده می‌شود، چه اقدامی می‌کنید؟",
            intentBehindQuestion = "شفافیت و صداقت در ارتباطات فریلنسری قبل از به وقوع پیوستن فاجعه.",
            winningPoints = listOf(
                "حداقل ۲۴ تا ۴۸ ساعت قبل از ددلاین کارفرما را مطلع کنید، نه در لحظه آخر!",
                "دلیل را صادقانه اما حرفه‌ای توضیح دهید.",
                "برنامه جبرانی دقیق برای تحویل بخش‌های آماده ارائه دهید."
            ),
            sampleIdealAnswer = "بلافاصله با کارفرما تماس می‌گیرم یا پیام می‌دهم: «سلام، برای حفظ بالاترین استاندارد کیفی و تست نهایی، نیاز به ۴۸ ساعت زمان اضافه دارم. تا این لحظه ۸۰ درصد کار آماده است و پیش‌نمایش آن را ارسال می‌کنم تا در جریان پیشرفت قرار بگیرید.»"
        )
    )

    fun updateActiveResume(updated: ResumeData) {
        _activeResume.value = updated
        _resumes.update { list ->
            if (list.any { it.id == updated.id }) {
                list.map { if (it.id == updated.id) updated else it }
            } else {
                list + updated
            }
        }
        persistResumes(_resumes.value, updated.id)
    }

    fun selectResume(resume: ResumeData) {
        val matchingResume = _resumes.value.firstOrNull { it.id == resume.id } ?: resume
        _activeResume.value = matchingResume
        persistResumes(_resumes.value, matchingResume.id)
    }

    fun addResume(title: String) {
        val newResume = ResumeData(
            title = title,
            fullName = _activeResume.value.fullName,
            jobTitle = _activeResume.value.jobTitle,
            email = _activeResume.value.email,
            phone = _activeResume.value.phone,
            bio = _activeResume.value.bio
        )
        _resumes.update { it + newResume }
        _activeResume.value = newResume
        persistResumes(_resumes.value, newResume.id)
    }

    fun addClientProject(project: ClientProject) {
        _clientProjects.update { current ->
            (listOf(project) + current).also(::persistClientProjects)
        }
    }

    fun updateProjectStatus(projectId: String, newStatus: ProjectStatus) {
        _clientProjects.update { current ->
            current.map { if (it.id == projectId) it.copy(status = newStatus) else it }
                .also(::persistClientProjects)
        }
    }

    fun addPortfolioProject(item: PortfolioProject) {
        _portfolioProjects.update { (listOf(item) + it).also(::persistPortfolioProjects) }
    }

    fun upgradeToVip() {
        _userProfile.update { it.copy(tier = SubscriptionTier.VIP_PRO, aiCredits = 999) }
        persistUserProfile(_userProfile.value)
    }

    fun addRewardAiCredits(amount: Int = 3) {
        _userProfile.update { it.copy(aiCredits = (it.aiCredits.toLong() + amount.coerceAtLeast(0)).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()) }
        persistUserProfile(_userProfile.value)
    }

    fun consumeAiCredit(): Boolean {
        if (_userProfile.value.tier == SubscriptionTier.VIP_PRO) return true
        if (_userProfile.value.aiCredits > 0) {
            _userProfile.update { it.copy(aiCredits = it.aiCredits - 1) }
            persistUserProfile(_userProfile.value)
            return true
        }
        return false
    }

    /** Returns one credit when an AI operation fails after consuming it. */
    fun refundAiCredit() {
        if (_userProfile.value.tier == SubscriptionTier.VIP_PRO) return
        _userProfile.update {
            it.copy(aiCredits = (it.aiCredits.toLong() + 1L)
                .coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        }
        persistUserProfile(_userProfile.value)
    }

    fun calculateFreelanceRate(input: RateCalculatorInput): RateCalculationOutput {
        val totalMonthlyExpense = input.monthlyLivingExpenses + input.businessExpenses
        val annualExpense = totalMonthlyExpense * 12
        val annualTargetWithSavings = (annualExpense * (100 + input.desiredSavingsMarginPercent)) / 100

        val workingWeeks = (52 - input.vacationWeeksPerYear).coerceAtLeast(1)
        val annualBillableHours = (workingWeeks * input.billableHoursPerWeek).coerceAtLeast(1)

        val hourlyRate = annualTargetWithSavings / annualBillableHours
        val dailyRate = hourlyRate * 6 // Standard 6-hour intense freelance day
        val minimumProjectRate = hourlyRate * 15 // Smallest viable freelance project

        val advice = when {
            hourlyRate < 250_000 -> "نرخ ساعتی شما اقتصادی است. برای پوشش هزینه‌های غیرقابل‌پیش‌بینی، کم‌کم نرخ پروژه‌های جدید را ۲۰٪ افزایش دهید."
            hourlyRate < 600_000 -> "نرخ ساعتی شما در محدوده استاندارد بازار فریلنسری ایران قرار دارد و تعادل خوبی بین سود و جذب کارفرما ایجاد می‌کند."
            else -> "نرخ ساعتی شما ممتاز (پریمیوم) است. برای حفظ این نرخ، روی ارائه پورتفولیوی بی‌نقص و برندسازی شخصی تمرکز کنید."
        }

        return RateCalculationOutput(
            hourlyRateToman = hourlyRate,
            dailyRateToman = dailyRate,
            minimumProjectRateToman = minimumProjectRate,
            totalAnnualTargetToman = annualTargetWithSavings,
            adviceText = advice
        )
    }
}
