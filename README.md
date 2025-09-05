# Pit Stop Paradise
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/Brave6/pit-stop-paradise)

Pit Stop Paradise is a native Android application designed for a theme park specializing in racing and karting experiences. The app allows users to browse rides and services, such as Turbo Kart 3000 for karting, Classic Cruiser Ride for a classic experience, and the Racing Simulator Pod for realistic racing simulation. Users can book appointments, view special offers, and manage their profiles. The app is built with modern Android development practices, including MVVM architecture, Hilt for dependency injection, and Retrofit for network communication.

The project features a full-stack setup:

Android Frontend built with Kotlin, MVVM, and Jetpack libraries.

Custom Backend powered by Node.js + Express, deployed on Render, and connected to MongoDB Atlas.

## Features
- **User Authentication**: Secure user registration and login functionality.
- **Session Management**: Persistent login sessions using Jetpack DataStore.
- **Service Catalog**: Users can browse a list of available services (e.g., karting sessions, car washes) presented in an auto-scrolling carousel.
- **Special Offers**: A dedicated section to display current promotions and discounts. Users can book services directly from an offer.
- **Booking System**: An intuitive interface for users to book services by selecting a date and time.
- **User Profile**: A profile screen where users can view their details and booking history.
- **Secure Logout**: Functionality to securely log out and clear session data.

## Architecture
The application is built using the **MVVM (Model-View-ViewModel)** architecture pattern to separate UI logic from business logic, ensuring a scalable and maintainable codebase.

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: XML with ViewBinding, Fragments, and RecyclerView
- **Asynchronous Programming**: Kotlin Coroutines and Flow for managing background tasks and data streams.
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for managing dependencies and simplifying the object graph.
- **Networking**: [Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) for efficient communication with the backend REST API.
- **Navigation**: [Jetpack Navigation Component](https://developer.android.com/guide/navigation) for handling in-app navigation between fragments.
- **Local Storage**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for storing the user's authentication token.

## Backend & API  

Pit Stop Paradise is powered by a custom backend:  

- **Server:** Node.js (Express) for RESTful API development  
- **Database:** MongoDB Atlas for secure, cloud-hosted data storage  
- **Authentication:** JWT (JSON Web Token) for secure, stateless authentication  
- **Deployment:** API endpoints integrated with the Android app using Retrofit
- **Render:** for cloud deployment  

## Project Structure
The project is organized into modular packages, following clean architecture principles:

- **`data`**: Contains Data Transfer Objects (DTOs) like `LoginRequest`, `BookingRequest`, and API responses used for network communication.
- **`di`**: Hilt modules (`NetworkModule`, `SessionModule`) responsible for providing dependencies across the app, such as the Retrofit instance and `SessionManager`.
- **`domain`**: The core business logic layer.
  - **`model`**: Defines the application's primary data models (e.g., `Product`, `Booking`, `Offer`).
  - **`repository`**: Abstract data sources and provide a clean API for data access to the ViewModels.
- **`retrofit`**: Contains the `ApiService` interface defining all REST API endpoints.
- **`ui`**: The presentation layer, containing all UI-related components.
  - **`adapter`**: `RecyclerView.Adapter` implementations for displaying lists of products, offers, and booking history.
  - **`fragment`**: Individual screens implemented as Fragments (e.g., `HomeFragment`, `ProfileFragment`).
- **`utils`**: Utility classes, including `SessionManager` for handling user authentication tokens.
- **`viewmodel`**: `ViewModel` classes that hold and manage UI-related data, interacting with repositories to fetch and update data.

## Tech Stack  

- **Frontend (Android App):** Kotlin, XML, MVVM, Hilt, Retrofit, Jetpack Navigation, DataStore  
- **Backend (API):** Node.js, Express, JWT Authentication
- **Hosting:** Render (API)  
- **Database:** MongoDB Atlas (cloud-based, scalable storage)  

## Accessibility (TalkBack Support)

Pit Stop Paradise includes **accessibility features** to ensure usability for all users:

- **Screen Reader Support (TalkBack)**  
  - All products in the list are announced with their **title, price, and duration**.  
  - Booking confirmations trigger an **automatic spoken announcement** (e.g., “Booking confirmed”), so users receive immediate feedback.  
- **Form Accessibility**  
  - Input fields include clear hints for TalkBack users.  
  - The **Confirm Booking** button is only enabled when the form is valid, reducing confusion for users navigating via screen readers.

These improvements demonstrate attention to **inclusive design and accessibility best practices**, an important skill for modern Android development.


## Setup and Installation

To build and run the project, follow these steps:

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/brave6/pit-stop-paradise.git
    ```

2.  **Open in Android Studio:**
    Open the cloned directory in Android Studio.

3.  **Configure the Backend API:**
    The application is configured to connect to a local backend server. You must update the base URL to match your network configuration.
    -   Navigate to `app/src/main/java/com/seth/pitstopparadise/di/NetworkModule.kt`.
    -   Change the `BASE_URL` constant to the IP address and port of your running backend server:
        ```kotlin
        private const val BASE_URL = "http://YOUR_LOCAL_IP:5000/api/"
        ```
    -   Update the `network_security_config.xml` file in `app/src/main/res/xml/` with the same IP address to permit cleartext traffic in a development environment.
        ```xml
        <domain includeSubdomains="true">YOUR_LOCAL_IP</domain>
        ```

4.  **Build and Run:**
    Let Android Studio sync the Gradle files, then build and run the application on an emulator or a physical device.

## Testing

Pit Stop Paradise includes comprehensive **unit and UI tests** to ensure reliability and correctness.

### Unit Tests

#### **AuthViewModel**
- Validates login logic for both **successful and failed credentials**.
- Confirms **SessionManager** saves the token correctly after successful login.
- Verifies that **state transitions** (`Idle`, `Loading`, `Success`, `Error`) are emitted in the correct order.
- Handles **network exceptions** gracefully and emits appropriate error states.

#### **BookingsViewModel**
- Ensures `confirmBooking` handles:
  - **Successful bookings** → emits `Success` state.
  - **Incomplete or invalid input** → emits `Error` state.
  - **API failures** → emits `Error` state with meaningful messages.
- Uses **mocked BookingRepository** and **Retrofit responses** to simulate backend behavior.
- Confirms **all state changes** are correctly reflected to the UI.

#### **UserRepository**
- Tests `getUserInfo` behavior for fetching user profile data.
- Confirms correct **User object** is returned when API succeeds.
- Throws an **exception** if no token is available.
- Handles **unauthorized responses (401)** by clearing the session token and throwing a `Session expired` error.
- Throws **generic errors** for other non-success HTTP responses, e.g., 500 server errors.
- Uses **mocked ApiService** and **SessionManager** to simulate backend and session behavior.

### UI Tests (Espresso)

#### **LoginActivity**
- Tests **login flow** with valid credentials.
- Confirms **UI elements** (email, password, login button) are interactive.
- Checks **loading indicators** and button enable/disable behavior during login.
- Validates **proper error messages** are displayed for invalid credentials.

#### **MainActivity & Navigation**
- Verifies **BottomNavigationView** works correctly with **NavController**.
- Confirms **fragment navigation** works as expected when selecting bottom menu items.
- Ensures **UI states remain consistent** across navigation (e.g., back stack handling).

### Tools & Libraries
- **JUnit 4** – Unit testing framework.
- **Espresso** – UI testing framework for interactions and assertions.
- **MockWebServer** – Simulate backend API responses.
- **Mockito / Mockito-Kotlin** – Mock repositories and verify interactions.
- **Hilt Testing** – Provide dependency injection in test scenarios.
- **Kotlin Coroutines Test** – Test suspend functions and `Flow` emissions.

