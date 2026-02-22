# AGENTS.md

This file provides guidance when working with code in this repository.

## Project Overview

LogFox is an Android LogCat reader supporting Shizuku, root, and ADB access. It monitors logs, detects crashes (Java/JNI/ANR), records log sessions, and supports powerful filtering. Built with Material You design.

## Build Commands

Run ALL Gradle tasks with `--quiet` flag.

```bash
./gradlew :app:assembleDebug --quiet          # Build debug APK
./gradlew testDebugUnitTest --quiet      # Run unit tests
./gradlew verifyRoborazziDebug --quiet   # Run snapshot tests (CI uses this)
```

## Architecture

### Module Structure

The app uses clean architecture with multi-module organization:

```
feature/<name>/
  api/            # Domain interfaces, models, repository interfaces
  impl/           # Repository implementations, data sources (internal), DTOs (internal)
  presentation/   # ViewModel, Fragments/Composables, ViewState
```

```
core/
  tea/base        # Pure Kotlin TEA primitives (Store, Reducer, EffectHandler)
  tea/android     # BaseStoreViewModel, BaseStoreFragment, ViewStateMapper
  ui/compose      # Compose utilities
  ui/view         # View-based UI utilities
  di, io, preferences, context, ...
```

**Dependency rules**:
- `presentation -> api` only (never import `impl`)
- `impl -> api` (its own module)
- Only `:app` aggregates `presentation` + `impl` modules

### UI Pattern

- **Container** (Fragment): owns ViewModel lifecycle, collects state/side effects, handles navigation
- **Passive views/composables**: render ViewState, expose callbacks, no business logic
- Navigation is side-effect driven: reducer emits `SideEffect.Navigate*`, container fragment handles it via Navigation Component

### Conventions

- One top-level type per file; file name matches type name
- Use cases expose `operator fun invoke`; failable operations return `Result<T>` in use cases
- Hilt bindings return interfaces (`@Binds`), not implementation types
- Predictable naming: `<Feature>ViewModel`, `<Feature>Reducer`, `<Feature>EffectHandler`, `<Feature>ViewStateMapper`

## Gradle & Dependencies

- **Version catalog**: ALL dependencies and plugins accessed via `libs` (see `gradle/libs.versions.toml`)
- **Type-safe project accessors**: enabled via `TYPESAFE_PROJECT_ACCESSORS`
- **Convention plugins** in `build-logic/conventions/`:
  - `logfox.android.feature` — feature modules (Android Library + Hilt)
  - `logfox.android.feature.compose` — Compose-enabled feature modules
  - `logfox.android.library` — standard Android library modules
  - `logfox.kotlin.jvm` — pure Kotlin modules
  - `logfox.android.compose` — Compose configuration
  - `logfox.android.room` — Room database configuration

## Key Tech Stack

- Kotlin, Coroutines/Flow, Hilt (DI), Room (DB), Navigation Component (fragments)
- Compose for newer UI, Fragments + XML for existing features
- Shizuku + libsu for privileged system access
- Roborazzi for snapshot testing
