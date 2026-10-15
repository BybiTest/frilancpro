package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.FreelanceViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("فریلنس‌پرو", appName)
  }

  @Test
  fun `view model can be created by the Android factory used by Compose`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    val store = ViewModelStore()

    val viewModel = ViewModelProvider(store, factory)[FreelanceViewModel::class.java]

    assertNotNull(viewModel)
    store.clear()
  }
}
