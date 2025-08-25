package com.seth.pitstopparadise.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.seth.pitstopparadise.data.BookingRequest
import com.seth.pitstopparadise.data.BookingResponse
import com.seth.pitstopparadise.domain.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class BookingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val bookingRepository: BookingRepository = mock()
    private lateinit var viewModel: BookingsViewModel

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = BookingsViewModel(bookingRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `confirmBooking sets Success when booking succeeds`() = runTest {
        val successResponse = Response.success(
            BookingResponse(message = "Booking confirmed")
        )
        doReturn(successResponse)
            .whenever(bookingRepository)
            .createBooking(any())

        viewModel.confirmBooking(
            name = "John Doe",
            phone = "1234567890",
            date = "2025-08-20",
            time = "10:00 AM",
            productId = "prod123"
        )

        assertTrue(viewModel.bookingState.value is BookingsViewModel.BookingUiState.Success)
    }

    @Test
    fun `confirmBooking sets Error when fields are empty`() = runTest {
        viewModel.confirmBooking(
            name = "",
            phone = "1234567890",
            date = "2025-08-20",
            time = "10:00 AM",
            productId = "prod123"
        )

        assertTrue(viewModel.bookingState.value is BookingsViewModel.BookingUiState.Error)
    }

    @Test
    fun `confirmBooking sets Error when API fails`() = runTest {
        val errorResponse = Response.error<BookingResponse>(
            500,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "Internal Server Error")
        )
        doReturn(errorResponse)
            .whenever(bookingRepository)
            .createBooking(any())

        viewModel.confirmBooking(
            name = "John Doe",
            phone = "1234567890",
            date = "2025-08-20",
            time = "10:00 AM",
            productId = "prod123"
        )

        assertTrue(viewModel.bookingState.value is BookingsViewModel.BookingUiState.Error)
    }

}
