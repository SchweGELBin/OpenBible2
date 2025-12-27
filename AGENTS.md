# OpenBible - AGENTS.md

## Build, Lint, and Test Commands

### Build
```bash
./gradlew build                    # Build entire project (debug and release)
./gradlew assembleDebug            # Build debug APK
./gradlew assembleRelease          # Build release APK (with ProGuard/R8 minification)
./gradlew clean                    # Clean build artifacts
```

### Test
```bash
./gradlew test                     # Run unit tests
./gradlew connectedAndroidTest     # Run instrumentation tests (requires emulator/device)
./gradlew connectedDebugAndroidTest # Run debug instrumentation tests
```

### Run Single Test
```bash
# Unit test
./gradlew test --tests com.schwegelbin.openbible.ExampleTest

# Instrumentation test
./gradlew connectedAndroidTest --tests com.schwegelbin.openbible.ExampleInstrumentedTest

# All tests in a class
./gradlew test --tests com.schwegelbin.openbible.logic.*
```

### Install
```bash
./gradlew installDebug             # Install debug APK to connected device
./gradlew installRelease           # Install release APK to connected device
```

## Code Style Guidelines

### Imports
- Organize alphabetically (standard Kotlin convention)
- Group in order: AndroidX, Kotlin standard library, kotlinx, third-party, project-specific
- Use wildcards sparingly (only for constants, large enum sets)
- Example:
  ```kotlin
  import androidx.compose.foundation.layout.padding
  import androidx.compose.material3.Text
  import androidx.compose.runtime.Composable
  import com.schwegelbin.openbible.logic.someFunction
  ```

### Formatting
- Use official Kotlin code style (configured in `gradle.properties`)
- 4-space indentation (no tabs)
- Max line length: typically 120 characters
- No trailing semicolons
- No trailing whitespace
- Opening braces on same line for most constructs
- Single-line expressions preferred for simple one-liners

### Types
- Prefer `val` over `var` for immutability
- Use type inference where type is obvious: `val name = "text"`
- Explicit types when type is not clear: `val bible: Bible = deserialize(...)`
- Nullable types with `?` for optional values
- Safe calls (`?.`) and Elvis operator (`?:`) for null safety
- Data classes for data models with automatic equals/hashCode/toString
- Enum classes for fixed sets of values
- Use `@Serializable` annotation for data classes that need JSON serialization

### Naming Conventions
- **Functions**: camelCase starting with lowercase (`downloadFile`, `getBible`)
- **Variables**: camelCase starting with lowercase (`darkTheme`, `bookCount`)
- **Classes/Objects**: PascalCase (`MainActivity`, `Bible`, `ThemeOption`)
- **Composables**: PascalCase functions, PascalCase parameters for callbacks
- **Constants**: ALL_CAPS with underscores (`DARK_THEME`, `MAX_CHAPTERS`)
- **Package names**: lowercase with dots (`com.schwegelbin.openbible.logic`)
- **Private properties**: can use `_` prefix only when needed (rare)
- **Lambda parameters**: descriptive names (`onNavigateToRead`, `onThemeChange`)

### Error Handling
- Use try-catch blocks for expected failures (file I/O, network)
- Use `_` for unused exception variables in catch blocks when appropriate
- Return null or empty collections for non-critical errors: `catch (_: Exception) { null }`
- Use `run` for single-expression returns with nullability checks
- Use `?.let` for null-safe operations that need multiple steps
- For file operations, check file existence before operations
- Prefer fail-fast with early returns rather than deeply nested conditionals

### Compose Guidelines
- **State Management**: Hoist state to callers, use `remember { mutableStateOf() }`
- **Composables**: Should be stateless where possible, accept state and callbacks as parameters
- **Modifiers**: Chain modifiers in order: sizing, padding, then others
- **Previews**: Include `@Preview` for composables, add `showBackground = true` for root composables
- **Experimental APIs**: Use `@OptIn(ExperimentalMaterial3Api::class)` at function level
- **Navigation**: Use type-safe navigation with `@Serializable` routes
- **Material3**: Use Material3 components (Scaffold, TopAppBar, etc.)
- **Theme**: Always wrap root composables with OpenBibleTheme for proper styling

### File Structure
- `MainActivity.kt` - App entry point, sets up Compose content
- `logic/` - Business logic, data fetching, file operations
- `logic/Getters.kt` - Data retrieval functions
- `logic/SharedPrefs.kt` - SharedPreferences wrappers
- `logic/Serialization.kt` - JSON data models and deserialization
- `logic/Main.kt` - Main business logic functions
- `ui/screens/` - Compose screen composables
- `ui/theme/` - Theme configuration (Color.kt, Theme.kt, Type.kt)
- Navigation routes defined in screen files with `@Serializable` objects

### Android-Specific
- Target SDK 36, Min SDK 27
- Use Context parameter for Android APIs
- File paths via `context.getExternalFilesDir()` for app-specific storage
- SharedPreferences via `context.getSharedPreferences()`
- Use `stringResource()` for all user-facing strings
- Deep links configured in NavHost with `navDeepLink`

### Dependencies
- Managed via `gradle/libs.versions.toml`
- Use version catalogs for all dependencies
- Jetpack Compose for UI (BOM for version alignment)
- Kotlinx Serialization for JSON
- AndroidX Navigation for navigation

### Testing Notes
- Currently no unit tests exist in the codebase
- JUnit 4.13.2 configured for unit tests
- AndroidX JUnit and Espresso for instrumentation tests
- Compose testing with `androidx.ui.test.junit4`
- Tests should be placed in `app/src/test/` (unit) or `app/src/androidTest/` (instrumentation)

### Performance
- Lazy loading for long lists
- Use `remember` for expensive computations
- Minimize recomposition by passing stable parameters
- Use `derivedStateOf` for computed state that shouldn't trigger recomposition
