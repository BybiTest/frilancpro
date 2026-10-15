package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ClientProject
import com.example.model.ProjectStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FreelanceRepositoryPersistenceTest {
    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    @Test
    fun resumeEditsSurviveRepositoryRecreation() {
        context.getSharedPreferences("freelancepro_resume_storage", Context.MODE_PRIVATE).edit().clear().commit()
        val first = FreelanceRepository(context)
        val edited = first.activeResume.value.copy(fullName = "رزومه آزمایشی", jobTitle = "توسعه‌دهنده اندروید")
        first.updateActiveResume(edited)

        val restored = FreelanceRepository(context)
        assertEquals("رزومه آزمایشی", restored.activeResume.value.fullName)
        assertEquals("توسعه‌دهنده اندروید", restored.activeResume.value.jobTitle)
        assertTrue(restored.resumes.value.any { it.id == edited.id })
    }

    @Test
    fun projectStatusSurvivesRepositoryRecreation() {
        context.getSharedPreferences("freelancepro_client_projects", Context.MODE_PRIVATE).edit().clear().commit()
        val first = FreelanceRepository(context)
        val project = ClientProject(
            clientName = "کارفرمای آزمایشی",
            projectTitle = "پروژه آزمایشی",
            budgetToman = 1250000L,
            deadlineDate = "۱۴۰۵/۱۲/۲۹",
            status = ProjectStatus.PROPOSAL_SENT
        )
        first.addClientProject(project)
        first.updateProjectStatus(project.id, ProjectStatus.COMPLETED)

        val restored = FreelanceRepository(context).clientProjects.value.single()
        assertEquals(project.id, restored.id)
        assertEquals(1250000L, restored.budgetToman)
        assertEquals(ProjectStatus.COMPLETED, restored.status)
    }
}
