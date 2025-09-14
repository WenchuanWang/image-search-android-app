# Image Search App

A modern Android application built with Clean Architecture, Jetpack Compose, and MVVM pattern that allows users to search and browse images from Flickr's public API. The app features a clean, intuitive interface with pagination support.

## Screenshots
| Loading | Loaded | Load More button |
| --- | --- | --- |
| ![Screenshot_20250914_173833_com example image_search_app](https://github.com/user-attachments/assets/619f67ce-12ae-43bb-9e91-fec12364f4eb) | ![Screenshot_20250914_230756_com example image_search_app](https://github.com/user-attachments/assets/45e3bd21-001c-43ea-a003-f6b3c6bfadef) | ![Screenshot_20250914_230800_com example image_search_app](https://github.com/user-attachments/assets/56f26230-8fd1-4ecb-8187-ca12ce351893) |


**User flow:** Search -> Loading -> Load More -> Clear Search

https://github.com/user-attachments/assets/393af8e8-ff26-4ef7-b43b-0f1fc2317478


## Features

- **Image Search**: Search for images using text queries
- **Modern UI**: Built with Jetpack Compose and Material 3 design
- **Pagination**: Load more images with infinite scroll functionality
- **Real-time Search**: Debounced search with loading states
- **Performance**: Efficient image loading with Coil
- **Dark Mode**: Full dark theme support following system preferences
- **Testing**: Comprehensive unit tests

## Tech Stack

### Core Technologies
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **Android Architecture Components** - Lifecycle, ViewModel
- **Kotlin Coroutines & Flow** - Asynchronous programming and reactive streams

### Networking & Data
- **Retrofit** - HTTP client for API calls
- **Moshi** - JSON serialization/deserialization
- **Coil** - Image loading and caching

### Dependency Injection
- **Hilt** - Dependency injection framework

### Testing
- **JUnit** - Unit testing framework
- **MockK** - Mocking library for Kotlin

## Architecture

```
app/src/main/java/com/example/image_search_app/
├── data/                        # Data layer
│   ├── mapper/                  # Data mapping utilities
│   │   └── PhotoMappers.kt
│   ├── remote/                  # API service and interceptors
│   │   ├── FlickrInterceptor.kt
│   │   ├── ImageSearchApiService.kt
│   │   └── PhotoResponse.kt
│   └── ImageSearchRepositoryImpl.kt
├── di/                          # Dependency injection modules
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── domain/                      # Domain layer (business logic)
│   ├── ImageSearchRepository.kt
│   ├── PaginatedPhotoResult.kt  # Contains Photo data class
│   └── usecase/                 # Use cases for business logic
│       └── GetImagesUseCase.kt
├── presentation/                # Presentation layer
│   ├── ui/
│   │   ├── components/          # Reusable UI components
│   │   │   └── CommonComponents.kt
│   │   ├── screens/             # Screen composables
│   │   │   └── MyPhotoScreen.kt
│   │   └── theme/               # App theming
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── ImageSearchViewModel.kt
├── MainActivity.kt              # Main activity entry point
└── ImageSearchApplication.kt    # Application class with Hilt setup
```

## Project Structure

### Key Components

#### Data Layer
- **`ImageSearchRepositoryImpl`**: Repository implementation with API integration
- **`ImageSearchApiService`**: Retrofit service for Flickr API
- **`PhotoMappers`**: Data transformation utilities

#### Domain Layer
- **`ImageSearchRepository`**: Repository interface
- **`PaginatedPhotoResult`**: Pagination wrapper
- **`GetImagesUseCase`**: Single use case for all image fetching operations

#### Presentation Layer
- **`ImageSearchViewModel`**: ViewModel with state management and pagination logic
- **`MyPhotoScreen`**: Main screen composable
- **`CommonComponents`**: Reusable UI components

### Architecture Benefits

- **Single Responsibility**: Each layer has a clear, focused purpose
- **Testability**: Easy to unit test each component in isolation
- **Maintainability**: Clean separation makes code easy to modify and extend
- **Scalability**: Simple architecture that can grow with the project
- **Use Case Pattern**: Encapsulates business logic in a single, reusable component

## Getting Started

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/WenchuanWang/image-search-android-app.git
   ```
2. Open in Android Studio
3. Sync Gradle files
4. Run the app on an emulator or device

## Usage

1. **Launch the app** - The main screen displays with a search bar
2. **Search for images** - Type your search query in the search bar
3. **Browse results** - View images in a responsive grid layout
4. **Load more** - Tap "Load More Photos" to see additional results
5. **Clear search** - Use the clear button to start a new search

## Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

## Future Improvements

With more development time, the following enhancements would significantly improve the app's functionality, user experience, and maintainability:

- **API Key Management**: Inject Flickr API keys via `BuildConfig` to avoid hard-coding secrets
- **Infinite Scroll**: Automatically load more images when user scrolls to the bottom
- **Paging 3 Integration**: Replace manual pagination with Jetpack Paging 3 for better performance
- **Retry Mechanism**: Allow users to retry failed operations with exponential backoff
- **Network State Awareness**: Detect network connectivity and show appropriate messages
- **Comprehensive UI Tests**: Add end-to-end UI tests covering complete user journeys
- **Accessibility Support**: Ensure full accessibility compliance and screen reader support
- **Modularization**: Split app into feature modules for better maintainability
