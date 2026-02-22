# UI Container Pattern

## Container responsibilities

- Own ViewModel lifecycle.
- Observe state and side effects.
- Handle navigation.
- Translate UI events to commands.

## Passive view/composable responsibilities

- Render provided state.
- Expose callbacks only.
- Remain idempotent (same input, same output).

Never place business rules in custom views or leaf composables.

## Practical constraints

- Fragments and Compose screens both follow the same container/passive split.
- Keep one source of truth in ViewModel, even in hybrid Fragment + Compose features.
- UI can ignore business side effects; effect handlers own business work.

## Quick anti-patterns

- Leaf composable owns business state (`remember` as feature state source of truth).
- Custom view performs repository/use case calls.
- Container-to-container callbacks for business state sync.

Use reactive shared data flow through repositories/use cases instead.
