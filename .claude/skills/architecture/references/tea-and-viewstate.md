# TEA and ViewState

## Modified TEA pattern

Core model:

- `State`: immutable feature state.
- `Command`: user intent or feedback event.
- `SideEffect`: async work or one-off UI action.
- `Reducer`: pure function from `(State, Command)` to `(State, List<SideEffect>)`.
- `EffectHandler`: executes side effects and emits feedback commands.

```kotlin
interface Reducer<State, Command, SideEffect> {
    fun reduce(state: State, command: Command): ReduceResult<State, SideEffect>
}

data class ReduceResult<State, SideEffect>(
    val state: State,
    val sideEffects: List<SideEffect> = emptyList(),
)
```

## Store safety rules

- `Store.send()` must run on Main thread.
- Effect handlers may run on background dispatchers, then switch to Main before `send`.
- Store supports cancellation and closes handlers.

```kotlin
interface EffectHandler<SideEffect, Command> : Closeable {
    suspend fun handle(effect: SideEffect, onCommand: suspend (Command) -> Unit)
    override fun close() = Unit
}
```

## Side effect split

- Business side effects: network, persistence, observation.
- UI side effects: navigation, snackbar, dialogs.
- UI handles only UI side effects; effect handlers handle business side effects.

## ViewState is mandatory

Every feature has both:

- Domain-facing `State` (store-managed).
- UI-facing `ViewState` (derived).

Mapping occurs in ViewModel through `ViewStateMapper`.

```kotlin
interface ViewStateMapper<State, ViewState> {
    fun map(state: State): ViewState
}
```

Rules:

- Mapper is pure and side-effect free.
- Mapper can run on configurable dispatcher.
- Fragments/Composables do not inject mappers directly.

## BaseStoreViewModel contract

Use `BaseStoreViewModel<ViewState, State, Command, SideEffect>`.

- Expose `StateFlow<ViewState>`.
- Expose `SharedFlow<SideEffect>`.
- Accept initial side effects when feature requires startup actions.
- Cancel store in `onCleared()`.

For expensive mapping, pass `Dispatchers.Default` (or another suitable dispatcher) to mapping pipeline.
