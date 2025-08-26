package com.seth.pitstopparadise.viewmodel

import android.util.Log
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
import org.mockito.MockedStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

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

        // Stub suspend function properly
        runTest {
            whenever(sessionManager.saveToken(any())).thenReturn(Unit)
        }

        // Mock Android Log
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
    fun `login success should save token and emit success state`() = runTest {
        val fakeResponse = retrofit2.Response.success(LoginResponse("fake_token"))

        // Use argument matcher to allow any LoginRequest
        `when`(apiService.login(any())).thenReturn(fakeResponse)

        val states = mutableListOf<LoginUiState>()
        val job = launch { authViewModel.loginState.toList(states) }

        authViewModel.login("test@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify suspend function called with correct token
        verify(sessionManager).saveToken("fake_token")
        assertTrue(states.last() is LoginUiState.Success)

        job.cancel()
    }

    @Test
    fun `login failure should emit error state`() = runTest {
        val fakeResponse = retrofit2.Response.error<LoginResponse>(
            401,
            "Unauthorized".toResponseBody("application/json".toMediaTypeOrNull())
        )

        `when`(apiService.login(any())).thenReturn(fakeResponse)

        val states = mutableListOf<LoginUiState>()
        val job = launch { authViewModel.loginState.toList(states) }

        authViewModel.login("fail@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastState = states.last()
        assertTrue(lastState is LoginUiState.Error && lastState.message.contains("401"))

        job.cancel()
    }

    @Test
    fun `network exception should emit error state`() = runTest {
        `when`(apiService.login(any())).thenThrow(RuntimeException("boom"))

        val states = mutableListOf<LoginUiState>()
        val job = launch { authViewModel.loginState.toList(states) }

        authViewModel.login("error@email.com", "password")
        testDispatcher.scheduler.advanceUntilIdle()

        val lastState = states.last()
        assertTrue(lastState is LoginUiState.Error && lastState.message.contains("boom"))

        job.cancel()
    }
}
