# Pit Stop Paradise
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/Brave6/pit-stop-paradise)

Pit Stop Paradise is a native Android application designed for a recreational vehicle service center, such as a go-kart track or car wash. The app allows users to browse services, book appointments, view special offers, and manage their profiles. It is built with modern Android development practices, including MVVM architecture, Hilt for dependency injection, and Retrofit for network communication.

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
