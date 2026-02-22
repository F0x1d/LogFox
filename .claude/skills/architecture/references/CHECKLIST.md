# Architecture Compliance Checklist

Use this checklist before finalizing a multi-file Android change.

## 1) TEA and state management

- Reducer is pure and deterministic.
- Reducer returns new `State` plus `SideEffect` list.
- `State` is immutable.
- `EffectHandler` handles async and feeds back through commands.
- `Store.send()` entrypoints are on Main thread.

## 2) ViewState boundary

- Feature defines both `State` and `ViewState`.
- `ViewStateMapper<State, ViewState>` exists and is pure.
- ViewModel exposes `StateFlow<ViewState>`.
- Fragment/Composable renders `ViewState` only.

## 3) UI container/passive split

- Container owns ViewModel lifecycle, side effect handling, and navigation.
- Passive views/composables only render data and expose callbacks.
- No business logic in custom views or leaf composables.

## 4) Clean architecture layering

- `presentation -> api` only.
- `impl -> api` for its own module.
- No cross-feature `impl` dependencies (except `:app`).
- Domain interfaces and models are in `api`.
- Data sources and DTOs are `internal` in `impl`.

## 5) DI and scoping

- Hilt bindings return interfaces (`@Binds`), not `*Impl`.
- `@Provides` is used only for configured construction.
- Stateful data sources are singleton-scoped.
- Reducers/effect handlers/mappers are injected directly unless list binding is needed.

## 6) Reactive data flow

- Reactive state exposed as `Flow`/`StateFlow` from repository interfaces.
- Mutable flow state is private in implementation.
- Multi-source state uses `combine` in use cases when needed.

## 7) Navigation and one-off UI actions

- Navigation emits from reducer as `SideEffect`.
- Containers consume UI side effects.
- Business side effects are ignored by UI and handled by effect handlers.

## 8) Naming and structure

- One top-level type per file.
- File name matches type name.
- Package roots include module role (`api`, `impl`, `presentation`) when split.
- `android.namespace` matches package root.

## 9) Build conventions

- Plugins and dependencies use `libs` aliases.
- Project dependencies use type-safe accessors (`projects...`).
- Gradle commands run with `--quiet`.

## 10) Report quality

- Mention rules applied and touched modules.
- Note any compromises and follow-up work.
