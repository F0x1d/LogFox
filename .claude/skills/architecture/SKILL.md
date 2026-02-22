---
name: architecture
description: This skill defines the only right architecture for android projects. Activate when doing big tasks (related to several files and their interconnections) in this project
---
# Android Architecture

This skill defines how to design and review architecture changes in LogFox Android modules.

## When to use this skill

Use this skill when at least one is true:

- The task touches multiple related Android files or modules.
- A new feature/module is added or existing module boundaries are changed.
- State management, ViewModel flow, reducers, effect handlers, or navigation patterns are updated.
- A PR needs architecture compliance review.

Skip this skill for tiny isolated edits (for example: text change, one-off constant rename).

## Fast workflow

1. Identify impacted modules (`api`, `impl`, `presentation`, and `core`).
2. Apply the non-negotiable rules in this file.
3. Load only the reference sections needed for the task:
   - `references/CHECKLIST.md` (quick compliance pass)
   - `references/REFERENCE_INDEX.md` (choose targeted rule groups)
4. Implement changes with minimal surface area while preserving boundaries.
5. Validate with the checklist before finalizing.

## Non-negotiable rules

1. Use modified TEA: immutable `State`, `Command`, `SideEffect`, pure `Reducer`, asynchronous `EffectHandler`.
2. `Store.send()` must be called from Main thread.
3. Every feature has both `State` and `ViewState`; mapping happens inside ViewModel via `ViewStateMapper`.
4. UI is passive: Fragments/Composables render state and emit events only; no business logic in views.
5. `presentation` depends only on `api`; never depend on another module's `impl` (except `:app`).
6. Use cases expose `operator fun invoke`; failable operations return `Result<T>`.
7. Repository interfaces live in `api`; data sources and implementations stay `internal` in `impl`.
8. Hilt bindings return interfaces, not implementation types.
9. Use SideEffects for navigation and one-off UI actions.
10. One file contains one top-level type; file name matches type name.

## Reference map

- Quick compliance checklist: `references/CHECKLIST.md`
- Rule-group index: `references/REFERENCE_INDEX.md`

## Expected output when applying this skill

When you perform architecture work, report:

1. Which rules were applied.
2. Which modules and layers were changed.
3. Any deliberate deviation and why.
4. Remaining risks or follow-up refactors.

## Edge-case guidance

- If existing code violates these rules and full refactor is out of scope, make the smallest safe change and document the gap.
- If two rules conflict, prefer dependency boundaries and thread-safety first, then naming/style.
- If a feature is hybrid (Fragment + Compose), keep a single source of truth in ViewModel and preserve passive UI on both sides.
