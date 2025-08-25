package com.seth.pitstopparadise.viewmodel

import android.util.Log
import com.seth.pitstopparadise.data.LoginRequest
import com.seth.pitstopparadise.data.LoginResponse
import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.MockedStatic
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var logMock: MockedStatic<Log>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mock(ApiService::class.java)
        sessionManager = mock(SessionManager::class.java)

        // Stub suspend function
        runTest {
            whenever(sessionManager.saveToken(any())).thenReturn(Unit)
        }

        // mock android.util.Log
        logMock = mockStatic(Log::class.java)
        logMock.`when`<Int> { Log.d(any(), any()) }.thenReturn(0)
        logMock.`when`<Int> { Log.e(any(), any()) }.thenReturn(0)

        authViewModel = AuthViewModel(sessionManager, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        logMock.close()
    }

    @Test
    fun `login success should save token and emit success message`() = runTest {
        val loginRequest = LoginRequest("test@email.com", "password")
        val fakeResponse = retrofit2.Response.success(LoginResponse("fake_token"))

        `when`(apiService.login(loginRequest)).thenReturn(fakeResponse)

        val messages = mutableListOf<String>()
        val job = launch { authViewModel.uiMessage.toList(messages) }

        authViewModel.login("test@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(sessionManager).saveToken("fake_token")
        assert(messages.contains("Login success!"))

        job.cancel()
    }

    @Test
    fun `login failure should emit error message`() = runTest {
        val loginRequest = LoginRequest("fail@email.com", "password")
        val fakeResponse = retrofit2.Response.error<LoginResponse>(
            401,
            "Unauthorized".toResponseBody("application/json".toMediaTypeOrNull())
        )

        `when`(apiService.login(loginRequest)).thenReturn(fakeResponse)

        val messages = mutableListOf<String>()
        val job = launch { authViewModel.uiMessage.toList(messages) }

        authViewModel.login("fail@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        assert(messages.any { it.contains("Login failed") })

        job.cancel()
    }

    @Test
    fun `network exception should emit network error`() = runTest {
        val loginRequest = LoginRequest("error@email.com", "password")

        `when`(apiService.login(loginRequest)).thenThrow(RuntimeException("boom"))

        val messages = mutableListOf<String>()
        val job = launch { authViewModel.uiMessage.toList(messages) }

        authViewModel.login("error@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        assert(messages.any { it.contains("Network error") })

        job.cancel()
    }
}
