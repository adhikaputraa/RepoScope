# RepoScope

RepoScope is an Android app for searching GitHub users and repositories, viewing user details, and managing a list of favorite repositories. It uses modern Android development best practices, including Jetpack Compose, Paging 3, Room, Hilt, and Clean Architecture.

## Features
- Search GitHub users and view their repositories
- View user details and repository lists with paging
- Add or remove repositories from your favorites (saved locally with Room)
- View a dedicated screen for your favorite repositories
- Light and dark theme support, adapting to system settings
- Error handling, loading states, and modern UI

## Tech Stack
- Jetpack Compose
- Paging 3
- Room Database
- Hilt (Dependency Injection)
- Retrofit & OkHttp
- Kotlin Coroutines & Flow
- Clean Architecture

## Getting Started
1. Clone the repository
2. Add your GitHub token to `local.properties` as `GITHUB_TOKEN=your_token`
3. Open in Android Studio and build the project

## Project Structure
- `data/` - Data sources, Room, repository implementations
- `domain/` - Use cases, repository interfaces, models
- `presentation/` - UI screens, ViewModels, navigation
- `core/` - DI modules and app setup

## Testing
- Unit tests for ViewModels, use cases, and repositories are in `app/src/test/java`
- Run tests with `./gradlew testDebugUnitTest`

## License
MIT
