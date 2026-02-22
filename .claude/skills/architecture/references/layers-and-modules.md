# Layers and Modules

## Clean architecture layers

Layer roles:

- Presentation: ViewModel, UI containers, passive views.
- Domain (`api`): use case interfaces, repository interfaces, domain models.
- Data (`impl`): repository implementations, data sources, DTOs, mappers.

Dependency direction:

```
presentation -> api
impl -> api
app -> presentation + impl
```

Rules:

- Presentation never imports `impl`.
- Data never leaks DTOs outside `impl`.
- Repository interfaces stay in `api`.

## Use cases and repositories

Use case conventions:

- Use `operator fun invoke`.
- For failable operations, return `Result<T>`.
- Wrap repository exceptions in use case layer.

Repository conventions:

- Failable repository methods may throw.
- Expose reactive state as `Flow<T>`.

## Data source boundaries

- Data source interfaces and implementations are `internal` in `impl`.
- DTOs are `internal` and mapped to domain models in repository implementation.
- Shared mutable state in data sources requires singleton scope.

## Modularization in LogFox

Typical feature structure:

```
feature/<name>/
  api/           # required
  impl/          # required
  presentation/  # optional
```

Module dependency rules:

- `presentation` depends only on `api` modules.
- `impl` depends on its own `api`, and optionally other modules' `api`.
- Only `:app` may aggregate many `impl` modules.

## Package and naming conventions

Package roots should include module role when split:

- `com.f0x1d.logfox.feature.auth.api`
- `com.f0x1d.logfox.feature.auth.impl`
- `com.f0x1d.logfox.feature.auth.presentation`

`android.namespace` should match package root.

Naming guidance:

- Predictable feature-prefixed names (`AuthViewModel`, `AuthReducer`, `AuthViewStateMapper`).
- Interfaces without `Impl`; concrete implementations with `Impl`.
- Exception: reducers/effect handlers may stay role-specific without forced `Impl` suffix.

## Convention plugins and Gradle constraints

Plugin mapping:

- `libs.plugins.logfox.kotlin.jvm` for pure Kotlin modules.
- `libs.plugins.logfox.android.library` for Android libraries.
- `libs.plugins.logfox.android.feature` for feature implementations.
- `libs.plugins.logfox.android.feature.compose` for Compose presentation modules.

Project constraints:

- Access plugins and dependencies through `libs`.
- Use type-safe project accessors (`projects...`).
- Run all Gradle tasks with `--quiet`.
