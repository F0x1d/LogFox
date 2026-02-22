# DI and Reactive Flow

## Hilt guidance

- Prefer `@Binds` for interface-to-implementation binding.
- Use `@Provides` for configured construction (Retrofit APIs, factories, etc).
- Return interface types from modules.
- Keep implementation types internal to `impl`.

TEA components are usually constructor-injected directly; bind list variants only when a ViewModel needs multiple effect handlers through a single dependency.

## Scoping rules

- Shared state holders (for example, data sources with `MutableStateFlow`) must be singleton-scoped.
- Stateless bindings can remain unscoped.
- Scope by lifecycle ownership, not by convenience.

## Reactive data flow

- Backing mutable flows are private in implementation.
- Expose read-only `Flow`/`StateFlow` from interfaces.
- Use `combine` in use cases for cross-source derived state.

```kotlin
override fun invoke(): Flow<AppScreen> = combine(
    onboardingRepository.wasOnboardingCompleted,
    authRepository.isAuthenticated,
) { onboardingDone, isAuthenticated ->
    when {
        !onboardingDone -> AppScreen.Onboarding
        !isAuthenticated -> AppScreen.Auth
        else -> AppScreen.Main
    }
}
```

## Review checks

- No `MutableStateFlow` leakage outside implementation class.
- Repository interfaces expose `Flow<T>` where UI/domain needs reactive updates.
- Hilt modules avoid returning concrete `*Impl` types.
