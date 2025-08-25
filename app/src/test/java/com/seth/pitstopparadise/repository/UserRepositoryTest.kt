package com.seth.pitstopparadise.repository

import com.seth.pitstopparadise.domain.repository.UserRepository
import com.seth.pitstopparadise.retrofit.ApiService
import com.seth.pitstopparadise.utils.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Response

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        apiService = mock(ApiService::class.java)
        sessionManager = mock(SessionManager::class.java)
        userRepository = UserRepository(apiService, sessionManager)
    }

    @Test
    fun `getUserInfo returns User when API succeeds`() = runTest {
        // Arrange
        `when`(sessionManager.token).thenReturn(flowOf("fake_token"))
        val fakeProfile = com.seth.pitstopparadise.data.ProfileResponse(
            _id = "123", username = "John", email = "john@email.com"
        )
        `when`(apiService.getProfile("Bearer fake_token"))
            .thenReturn(Response.success(fakeProfile))

        // Act
        val result = userRepository.getUserInfo()

        // Assert
        assertEquals("123", result.id)
        assertEquals("John", result.username)
        assertEquals("john@email.com", result.email)
    }

    @Test(expected = Exception::class)
    fun `getUserInfo throws when no token`() = runTest {
        `when`(sessionManager.token).thenReturn(flowOf(null))
        userRepository.getUserInfo()
    }

    @Test
    fun `getUserInfo clears token and throws when unauthorized`() = runTest {
        `when`(sessionManager.token).thenReturn(flowOf("expired_token"))
        val errorResponse = Response.error<com.seth.pitstopparadise.data.ProfileResponse>(
            401, "Unauthorized".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(apiService.getProfile("Bearer expired_token")).thenReturn(errorResponse)

        try {
            userRepository.getUserInfo()
            fail("Expected Exception for 401")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Session expired"))
            verify(sessionManager).clearToken()
        }
    }

    @Test
    fun `getUserInfo throws generic error on non-200`() = runTest {
        `when`(sessionManager.token).thenReturn(flowOf("bad_token"))
        val errorResponse = Response.error<com.seth.pitstopparadise.data.ProfileResponse>(
            500, "Server error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(apiService.getProfile("Bearer bad_token")).thenReturn(errorResponse)

        try {
            userRepository.getUserInfo()
            fail("Expected Exception for 500")
        } catch (e: Exception) {
            assertTrue(e.message!!.contains("Failed to load user profile"))
        }
    }
}
