---
name: Android Architecture
description: This skill defines the only right architecture for android projects. Activate when doing big tasks (related to several files and their interconnections) in android projects
---

# Architecture Skill: Android Application Architecture

This skill defines the architectural rules and patterns for building Android applications in this project. It establishes conventions for state management, layer separation, dependency injection, modularization, and UI patterns using both Views and Jetpack Compose.

---

## 1. TEA (The Elm Architecture) - Modified

### Overview
The architecture uses a modified TEA pattern with **State**, **Command**, and **SideEffect**. The system must be fully cancellable. The Store is lifecycle-aware via ViewModel. **Store.send() must be called only from Main thread.**

### Components

#### State
Represents the current state of the feature. Must be immutable.

```kotlin
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
)
```

#### Command
Represents user actions or system events that can modify state.

```kotlin
sealed interface AuthCommand {
    data class LoginTapped(val email: String, val password: String) : AuthCommand
    data object LogoutTapped : AuthCommand
    data class UserLoaded(val user: User) : AuthCommand
    data class ErrorOccurred(val message: String) : AuthCommand
}
```

#### SideEffect
Represents side effects that need to be executed. SideEffects serve two purposes:
1. **Business logic** - handled by EffectHandlers (network calls, persistence, etc.)
2. **UI actions** - handled by Fragment/Composable (navigation, toasts, etc.)

```kotlin
sealed interface AuthSideEffect {
    // Business logic side effects
    data class Login(val email: String, val password: String) : AuthSideEffect
    data object Logout : AuthSideEffect
    data object LoadUser : AuthSideEffect
    
    // UI side effects
    data object NavigateToHome : AuthSideEffect
    data class ShowError(val message: String) : AuthSideEffect
}
```

### Reducer Interface
The reducer takes state and a command, returning new state and a list of side effects. Reducer is a **pure function** - no side effects allowed.

```kotlin
// Interface - in core/tea/base module, public
interface Reducer<State, Command, SideEffect> {
    fun reduce(state: State, command: Command): ReduceResult<State, SideEffect>
}

data class ReduceResult<State, SideEffect>(
    val state: State,
    val sideEffects: List<SideEffect> = emptyList(),
)

// Helper function for cleaner syntax
fun <State, SideEffect> State.withSideEffects(
    vararg sideEffects: SideEffect,
): ReduceResult<State, SideEffect> = ReduceResult(this, sideEffects.toList())

fun <State, SideEffect> State.noSideEffects(): ReduceResult<State, SideEffect> = 
    ReduceResult(this, emptyList())

// Implementation - feature name prefix, internal visibility (NO Impl suffix for reducers)
internal class AuthReducer : Reducer<AuthState, AuthCommand, AuthSideEffect> {

    override fun reduce(
        state: AuthState,
        command: AuthCommand,
    ): ReduceResult<AuthState, AuthSideEffect> = when (command) {
        is AuthCommand.LoginTapped -> {
            state.copy(isLoading = true, error = null)
                .withSideEffects(AuthSideEffect.Login(command.email, command.password))
        }

        is AuthCommand.LogoutTapped -> {
            state.withSideEffects(AuthSideEffect.Logout)
        }

        is AuthCommand.UserLoaded -> {
            state.copy(isLoading = false, user = command.user)
                .withSideEffects(AuthSideEffect.NavigateToHome)
        }

        is AuthCommand.ErrorOccurred -> {
            state.copy(isLoading = false, error = command.message)
                .withSideEffects(AuthSideEffect.ShowError(command.message))
        }
    }
}
```

### EffectHandler Interface
Effect handlers process side effects and send feedback via `onCommand` suspend function. The `onCommand` **MUST be suspend** and internally uses `withContext(Dispatchers.Main.immediate)` to ensure thread safety since `Store.send()` must be called from Main thread.

Multiple effect handlers can be specified, each with its own role.

EffectHandler extends `Closeable`. The default `close()` is a no-op, but it can be overridden to release resources (e.g., cancel internal jobs). `Store.cancel()` calls `close()` on all effect handlers.

```kotlin
// Interface - in core/tea/base module, public
interface EffectHandler<SideEffect, Command>: Closeable {
    suspend fun handle(effect: SideEffect, onCommand: suspend (Command) -> Unit)

    override fun close() = Unit
}

// Network-related effect handler - feature name prefix, internal (NO Impl suffix)
internal class AuthNetworkEffectHandler @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
) : EffectHandler<AuthSideEffect, AuthCommand> {

    override suspend fun handle(
        effect: AuthSideEffect,
        onCommand: suspend (AuthCommand) -> Unit,
    ) {
        when (effect) {
            is AuthSideEffect.Login -> {
                loginUseCase(effect.email, effect.password)
                    .onSuccess { user -> onCommand(AuthCommand.UserLoaded(user)) }
                    .onFailure { error -> onCommand(AuthCommand.ErrorOccurred(error.message ?: "Unknown error")) }
            }

            is AuthSideEffect.Logout -> {
                logoutUseCase()
            }

            // Handled by different effect handler or UI
            else -> Unit
        }
    }
}

// Persistence-related effect handler
internal class AuthPersistenceEffectHandler @Inject constructor(
    private val loadUserUseCase: LoadUserUseCase,
) : EffectHandler<AuthSideEffect, AuthCommand> {

    override suspend fun handle(
        effect: AuthSideEffect,
        onCommand: suspend (AuthCommand) -> Unit,
    ) {
        when (effect) {
            is AuthSideEffect.LoadUser -> {
                loadUserUseCase()?.let { user ->
                    onCommand(AuthCommand.UserLoaded(user))
                }
            }

            // Handled by different effect handler or UI
            else -> Unit
        }
    }
}
```

### Store
The store orchestrates state, reducer, and effect handlers. Must support cancellation via coroutine job management. **`send()` must be called only from Main thread.**

```kotlin
// In core/tea/base module, public
class Store<State, Command, SideEffect>(
    initialState: State,
    private val reducer: Reducer<State, Command, SideEffect>,
    private val effectHandlers: List<EffectHandler<SideEffect, Command>>,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffects = MutableSharedFlow<SideEffect>()
    val sideEffects: SharedFlow<SideEffect> = _sideEffects.asSharedFlow()

    private val jobs = mutableMapOf<String, Job>()

    /**
     * Send a command to the store. MUST be called from Main thread.
     */
    fun send(command: Command) {
        val result = reducer.reduce(_state.value, command)
        _state.value = result.state

        result.sideEffects.forEach { sideEffect ->
            // Emit side effect for UI observation
            scope.launch {
                _sideEffects.emit(sideEffect)
            }

            // Process side effect with handlers
            effectHandlers.forEach { handler ->
                val jobId = UUID.randomUUID().toString()
                val job = scope.launch {
                    handler.handle(sideEffect) { cmd ->
                        // Switch to Main thread before calling send
                        withContext(Dispatchers.Main) {
                            send(cmd)
                        }
                    }
                    jobs.remove(jobId)
                }
                jobs[jobId] = job
            }
        }
    }

    fun cancel() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()

        effectHandlers.forEach { it.close() }
    }
}
```

### ViewStateMapper Interface
ViewStateMapper is a **mandatory** interface that transforms internal domain State into a presentation-ready ViewState. Every feature MUST have a ViewState and a ViewStateMapper - there is no opt-out.

```kotlin
// In core/tea/base module, public
interface ViewStateMapper<State, ViewState> {
    fun map(state: State): ViewState
}
```

Even for simple features where State maps 1:1 to ViewState, the mapper must exist. The mapper keeps the boundary clean and makes it trivial to add derived fields later.

### BaseStoreViewModel
Base ViewModel that integrates Store with Android lifecycle. The ViewModel maps internal State to ViewState via the ViewStateMapper and exposes the mapped `StateFlow<ViewState>`.

```kotlin
// In core/tea/android module, public
abstract class BaseStoreViewModel<ViewState, State, Command, SideEffect>(
    initialState: State,
    reducer: Reducer<State, Command, SideEffect>,
    effectHandlers: List<EffectHandler<SideEffect, Command>>,
    viewStateMapper: ViewStateMapper<State, ViewState>,
    initialSideEffects: List<SideEffect> = emptyList(),
    viewStateMappingDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : ViewModel() {

    private val store = Store(
        initialState = initialState,
        reducer = reducer,
        effectHandlers = effectHandlers,
        scope = viewModelScope,
    )

    val state: StateFlow<ViewState> = store.state
        .map { viewStateMapper.map(it) }
        .flowOn(viewStateMappingDispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewStateMapper.map(initialState),
        )
    val sideEffects: SharedFlow<SideEffect> = store.sideEffects

    init {
        initialSideEffects.forEach { effect ->
            effectHandlers.forEach { handler ->
                viewModelScope.launch {
                    handler.handle(effect) { cmd ->
                        withContext(Dispatchers.Main.immediate) {
                            send(cmd)
                        }
                    }
                }
            }
        }
    }

    fun send(command: Command) {
        store.send(command)
    }

    override fun onCleared() {
        super.onCleared()
        store.cancel()
    }
}
```

**Key points:**
- `ViewState` is the **first** type parameter
- `state` exposes `StateFlow<ViewState>`, not `StateFlow<State>` - the mapping happens inside the ViewModel
- `viewStateMappingDispatcher` defaults to `Dispatchers.Main.immediate` but can be overridden (e.g., to `Dispatchers.Default`) for expensive mapping operations
- Fragments/Composables render `ViewState` directly - they do NOT inject the mapper

### Feature ViewModel
Feature-specific ViewModel that extends BaseStoreViewModel. The first type parameter is always ViewState.

```kotlin
// Feature ViewModel - internal visibility
@HiltViewModel
internal class AuthViewModel @Inject constructor(
    reducer: AuthReducer,
    networkEffectHandler: AuthNetworkEffectHandler,
    persistenceEffectHandler: AuthPersistenceEffectHandler,
    viewStateMapper: AuthViewStateMapper,
) : BaseStoreViewModel<AuthViewState, AuthState, AuthCommand, AuthSideEffect>(
    initialState = AuthState(),
    reducer = reducer,
    effectHandlers = listOf(networkEffectHandler, persistenceEffectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(
        AuthSideEffect.LoadUser,
        AuthSideEffect.ObservePreferences,
    ),
)
```

### ViewState Pattern (MANDATORY)

Every feature MUST have both a **State** (internal domain state managed by the Reducer) and a **ViewState** (presentation-ready state consumed by the UI). The **ViewStateMapper** transforms State into ViewState inside the ViewModel — the Fragment/Composable only sees ViewState.

#### Components

**State** holds domain-centric data managed by the Reducer:

```kotlin
internal data class ItemsState(
    val items: List<FormattedItem>? = null,
    val selectedIds: Set<Long> = emptySet(),
    val expandedOverrides: Map<Long, Boolean> = emptyMap(),
    val defaultExpanded: Boolean = false,
    val textSize: Int = 14,
    val itemsChanged: Boolean = true,
    // ... other domain fields
)
```

**ViewState** holds presentation-ready data for the Fragment:

```kotlin
internal data class ItemsViewState(
    val items: List<ItemPresentationModel>? = null,
    val itemsChanged: Boolean = true,
    val selecting: Boolean = false,
    val selectedCount: Int = 0,
    // ... other UI fields
)
```

**ViewStateMapper** implements the `ViewStateMapper<State, ViewState>` interface from `core/tea`, `@Inject`-constructed, `internal` visibility:

```kotlin
internal class ItemsViewStateMapper @Inject constructor() : ViewStateMapper<ItemsState, ItemsViewState> {

    override fun map(state: ItemsState): ItemsViewState = ItemsViewState(
        items = state.items?.map { formatted ->
            formatted.toPresentationModel(
                expanded = state.expandedOverrides.getOrElse(formatted.id) { state.defaultExpanded },
                selected = formatted.id in state.selectedIds,
                textSize = state.textSize.toFloat(),
            )
        },
        itemsChanged = state.itemsChanged,
        selecting = state.selectedIds.isNotEmpty(),
        selectedCount = state.selectedIds.size,
    )
}
```

For simple features where State maps nearly 1:1 to ViewState, the mapper is still required but trivial:

```kotlin
internal class SearchLogsViewStateMapper @Inject constructor() : ViewStateMapper<SearchLogsState, SearchLogsViewState> {
    override fun map(state: SearchLogsState): SearchLogsViewState = SearchLogsViewState(
        query = state.query.orEmpty(),
        caseSensitive = state.caseSensitive,
    )
}
```

#### Integration

The **ViewModel** takes `ViewState` as its first type parameter and accepts `viewStateMapper` in the constructor. The ViewModel maps State -> ViewState internally and exposes `StateFlow<ViewState>`:

```kotlin
@HiltViewModel
internal class ItemsViewModel @Inject constructor(
    reducer: ItemsReducer,
    effectHandler: ItemsEffectHandler,
    viewStateMapper: ItemsViewStateMapper,
) : BaseStoreViewModel<ItemsViewState, ItemsState, ItemsCommand, ItemsSideEffect>(
    initialState = ItemsState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
    initialSideEffects = listOf(ItemsSideEffect.LoadItems),
)
```

The **Fragment** renders `ViewState` directly — it does NOT inject the mapper:

```kotlin
@AndroidEntryPoint
internal class ItemsFragment : BaseStoreFragment<
    FragmentItemsBinding,
    ItemsViewState,
    ItemsState,
    ItemsCommand,
    ItemsSideEffect,
    ItemsViewModel,
    >() {

    override val viewModel by viewModels<ItemsViewModel>()

    override fun render(state: ItemsViewState) {
        binding.processList(state.items, state.itemsChanged)
        binding.processSelection(state.selecting, state.selectedCount)
    }
}
```

#### Key Rules
- **State** MUST NOT contain presentation models — use domain models or intermediate formatted models
- **ViewState** MUST NOT be the TEA State type — it is derived, not managed by the Store
- **ViewStateMapper** implements `ViewStateMapper<State, ViewState>` interface from `core/tea`
- **ViewStateMapper** MUST be a pure function (no side effects, no mutable state) — mapping runs on `viewStateMappingDispatcher` (defaults to `Dispatchers.Main.immediate`)
- For expensive mapping operations, override `viewStateMappingDispatcher` in the ViewModel constructor (e.g., pass `Dispatchers.Default`)
- The expensive work (filtering, formatting, IO) stays in the **EffectHandler** on background dispatchers
- The cheap work (applying selection/expanded/textSize to pre-formatted items) happens in the **mapper**
- Selection, expansion, and similar UI-local state lives **directly in State**, managed by the **Reducer** as pure functions — no presentation-layer repositories or use cases
- When the Reducer modifies selection state that needs external sync, it emits a SideEffect carrying the data (e.g., `SyncSelectedLines(selectedIds)`) rather than relying on an internal repository

### Type Alias for Convenience
```kotlin
typealias AuthStore = Store<AuthState, AuthCommand, AuthSideEffect>
typealias AuthStoreViewModel = BaseStoreViewModel<AuthViewState, AuthState, AuthCommand, AuthSideEffect>
```

---

## 2. Clean Architecture

### Layer Structure
Each feature/module contains three layers:
- **Presentation**: UI components (Views/Composables) and ViewModels
- **Domain**: Use cases and repository interfaces
- **Data**: Data sources and repository implementations

### Dependency Rules
```
Presentation ──────► Domain (api module)
                      ▲
Data         ─────────┘
(impl module)
```

**Critical Rules:**
- Presentation layer MUST NOT know or access anything but its Domain layer (api module)
- Presentation layer MUST NOT directly access Data layer (impl module)
- Data layer depends on Domain layer (implements repository interfaces)
- Presentation only triggers actions via ViewModel

### Naming Conventions
| Type | Name | Visibility |
|------|------|------------|
| Interface | Normal name (e.g., `LoginUseCase`) | `public` (in api module) |
| Implementation | `Impl` suffix (e.g., `LoginUseCaseImpl`) | `internal` (in impl module) |
| Data Source Interface | Normal name (e.g., `AuthRemoteDataSource`) | `internal` |
| Data Source Implementation | `Impl` suffix (e.g., `AuthRemoteDataSourceImpl`) | `internal` |
| ViewModel | Feature name + `ViewModel` (e.g., `AuthViewModel`) | `internal` |
| Repository Interface | Normal name (e.g., `AuthRepository`) | `public` (in api module) |
| Repository Implementation | `Impl` suffix (e.g., `AuthRepositoryImpl`) | `internal` (in impl module) |
| Reducer | Feature name + `Reducer` (e.g., `AuthReducer`) | `internal` |
| EffectHandler | Feature name + role + `EffectHandler` (e.g., `AuthNetworkEffectHandler`) | `internal` |
| State | Feature name + `State` (e.g., `AuthState`) | `internal` |
| ViewState | Feature name + `ViewState` (e.g., `AuthViewState`) | `internal` |
| ViewStateMapper | Feature name + `ViewStateMapper` (e.g., `AuthViewStateMapper`) | `internal` |

**Exception for Reducers and Effect Handlers:**
Reducers and Effect Handlers do NOT use the `Impl` suffix because they already include the feature name in their title (e.g., `AuthReducer`, `AuthNetworkEffectHandler`). This makes the naming more concise while still being clear.

### Domain Layer

#### Use Cases
**Critical Rules:**
- Use cases MUST use `invoke` operator function (allows `useCase()` syntax)
- Use case methods MUST NOT throw - return `Result<T>` type instead for failable operations
- Use cases catch repository exceptions and convert them to `Result`

```kotlin
// Interface - in api module, public
interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}

// Implementation - in impl module, internal
internal class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
) : LoginUseCase {

    override suspend fun invoke(email: String, password: String): Result<User> {
        return runCatching {
            authRepository.login(email, password)
        }
    }
}

// Non-failable use case example
interface MarkOnboardingCompletedUseCase {
    suspend operator fun invoke()
}

internal class MarkOnboardingCompletedUseCaseImpl @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : MarkOnboardingCompletedUseCase {

    override suspend fun invoke() {
        onboardingRepository.markOnboardingCompleted()
    }
}
```

#### Repository Interfaces
**Critical Rules:**
- Repository methods MUST use `throws` (suspend functions that can throw) for failable operations, not `Result<*>`
- Use `Flow<T>` for reactive properties

```kotlin
// In api module
interface AuthRepository {
    suspend fun login(email: String, password: String): User
    suspend fun logout()
    val isAuthenticated: Flow<Boolean>
}
```

#### Domain Models
```kotlin
// In api module
data class User(
    val id: String,
    val email: String,
    val name: String,
)
```

### Data Layer

#### Data Sources
Data sources operate on data models (DTOs). Data source interfaces are defined in the Data layer and are `internal`.

**Critical Rules:**
- Data source interfaces MUST be defined in the **impl module**, NOT in api
- Data source interfaces MUST be `internal` visibility
- Data sources are ONLY accessible within the impl module
- Data sources MUST NOT be accessed from Domain or Presentation layers

```kotlin
// DTOs - internal, in impl module
internal data class UserDTO(
    val id: String,
    val email: String,
    val name: String,
)

internal data class LoginRequestDTO(
    val email: String,
    val password: String,
)

// Remote Data Source Interface - INTERNAL, defined in impl module
internal interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequestDTO): UserDTO
    suspend fun logout()
}

// Implementation - internal
internal class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRemoteDataSource {

    override suspend fun login(request: LoginRequestDTO): UserDTO {
        return api.login(request)
    }

    override suspend fun logout() {
        api.logout()
    }
}

// Local Data Source Interface - INTERNAL
internal interface AuthLocalDataSource {
    suspend fun saveUser(user: UserDTO)
    suspend fun getUser(): UserDTO?
    suspend fun clearUser()
    val isAuthenticated: Flow<Boolean>
}

// Implementation - internal
internal class AuthLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AuthLocalDataSource {
    // Implementation...
}
```

#### Repository Implementation
Repositories combine data sources and map DTOs to domain models.

```kotlin
// Implementation - in impl module, internal
internal class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) : AuthRepository {

    override suspend fun login(email: String, password: String): User {
        val dto = remoteDataSource.login(
            LoginRequestDTO(email = email, password = password)
        )
        localDataSource.saveUser(dto)
        return dto.toDomain()
    }

    override suspend fun logout() {
        runCatching { remoteDataSource.logout() }
        localDataSource.clearUser()
    }

    override val isAuthenticated: Flow<Boolean>
        get() = localDataSource.isAuthenticated
}

// Mapping extension - in impl module
internal fun UserDTO.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
)
```

---

## 3. Passive/Container View Pattern

### Rules
1. **NO views can handle business logic state** - they only display data provided to them
2. Views accept lambdas to trigger events
3. Views must be **idempotent** (same input = same output)
4. Only **Container** screens (Activity/Fragment/Composable with ViewModel) can:
   - Hold and observe a ViewModel
   - Handle navigation
   - Register lifecycle callbacks
5. **Containers are independent** - they must not know about each other
6. ViewModel lifecycle is bound to Container lifecycle
7. **Containers MUST NOT build ViewModels manually** - they use Hilt injection
8. **NO callbacks between containers** - use reactive data observation instead

### Container Communication

```kotlin
// WRONG: Using callbacks between containers
class ParentFragment : BaseFragment<...>() {
    fun showChild() {
        ChildFragment(onItemSelected = { item ->
            viewModel.send(AuthCommand.ItemSelected(item))  // WRONG: callback
        })
    }
}

// CORRECT: Child updates shared data, parent observes changes
class ParentFragment : BaseFragment<...>() {
    // Parent's effect handler observes shared data changes via use case
    // Child updates shared data through use case
    // Parent automatically receives the update via its observation
}
```

### Base Fragment Classes
Base classes abstract away Flow collection boilerplate. Feature fragments only need to implement `render()` and `handleSideEffect()`. There are three base fragment variants in `core/tea/android`:

- **BaseStoreFragment** — for regular fragments with ViewBinding
- **BaseStoreBottomSheetFragment** — for bottom sheet dialog fragments with ViewBinding
- **BaseStorePreferenceFragment** — for preference fragments (no ViewBinding)

All three follow the same type parameter pattern and API.

```kotlin
// In core/tea/android module - Base Fragment for TEA architecture
abstract class BaseStoreFragment<
    VB : ViewBinding,
    ViewState,
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<ViewState, State, Command, SideEffect>,
    > : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract val viewModel: VM

    /**
     * Create the ViewBinding for this fragment.
     */
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Render state to UI. Called on every state change.
     * Must be idempotent - same state = same UI.
     * Receives ViewState (already mapped from domain State by the ViewModel).
     */
    abstract fun render(state: ViewState)

    /**
     * Handle side effects (navigation, snackbars, etc.)
     * Called for ALL side effects - ignore those not relevant to UI.
     */
    abstract fun handleSideEffect(sideEffect: SideEffect)

    /**
     * Called after view is created but before state collection starts.
     * Override to set up views, click listeners, etc.
     * The receiver is the ViewBinding - no need to prefix with `binding.`.
     */
    protected open fun VB.onViewCreated(view: View, savedInstanceState: Bundle?) = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { state -> render(state) } }
                launch { viewModel.sideEffects.collect { effect -> handleSideEffect(effect) } }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Convenience method to send commands to ViewModel
     */
    protected fun send(command: Command) {
        viewModel.send(command)
    }
}
```

#### BaseStoreBottomSheetFragment
Same API as BaseStoreFragment but extends `BottomSheetDialogFragment`:

```kotlin
// In core/tea/android module
abstract class BaseStoreBottomSheetFragment<
    VB : ViewBinding,
    ViewState,
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<ViewState, State, Command, SideEffect>,
    > : BottomSheetDialogFragment() {
    // Same API: inflateBinding, render, handleSideEffect, VB.onViewCreated, send
}
```

#### BaseStorePreferenceFragment
For preference screens. No ViewBinding — uses PreferenceFragmentCompat's built-in preference XML inflation.

```kotlin
// In core/tea/android module
abstract class BaseStorePreferenceFragment<
    ViewState,
    State,
    Command,
    SideEffect,
    VM : BaseStoreViewModel<ViewState, State, Command, SideEffect>,
    > : PreferenceFragmentCompat() {

    protected abstract val viewModel: VM

    abstract fun render(state: ViewState)
    abstract fun handleSideEffect(sideEffect: SideEffect)

    // send() convenience method available
}
```

**Key differences from the old API:**
- **6 type parameters** (added `ViewState` as second): `<VB, ViewState, State, Command, SideEffect, VM>`
- `render()` receives **ViewState**, not State — the mapping is done in the ViewModel
- `inflateBinding()` replaces `createBinding()`
- `VB.onViewCreated()` is an extension function on the binding — override it instead of overriding `onViewCreated()` directly. The binding is the receiver so you can access views directly without `binding.` prefix

### Feature Fragment Example
```kotlin
// Container Fragment - extends BaseStoreFragment with 6 type params
@AndroidEntryPoint
internal class AuthFragment :
    BaseStoreFragment<
        FragmentAuthBinding,
        AuthViewState,
        AuthState,
        AuthCommand,
        AuthSideEffect,
        AuthViewModel,
        >() {

    override val viewModel by viewModels<AuthViewModel>()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentAuthBinding.inflate(inflater, container, false)

    // Setup event listeners in VB.onViewCreated - binding is the receiver
    override fun FragmentAuthBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        loginView.onEmailChanged = { send(AuthCommand.EmailChanged(it)) }
        loginView.onPasswordChanged = { send(AuthCommand.PasswordChanged(it)) }
        loginView.onLoginClick = { send(AuthCommand.LoginTapped) }
    }

    override fun render(state: AuthViewState) {
        // Pure rendering - same ViewState = same UI
        binding.loginView.bind(state)
    }

    override fun handleSideEffect(sideEffect: AuthSideEffect) {
        // Handle UI-related side effects, ignore business logic ones
        when (sideEffect) {
            is AuthSideEffect.NavigateToHome -> {
                findNavController().navigate(AuthFragmentDirections.actionAuthToHome())
            }
            is AuthSideEffect.ShowError -> {
                Snackbar.make(binding.root, sideEffect.message, Snackbar.LENGTH_SHORT).show()
            }
            // Business logic side effects - handled by EffectHandlers, ignore here
            else -> Unit
        }
    }
}
```

### Feature BottomSheet Fragment Example
```kotlin
@AndroidEntryPoint
internal class SearchLogsBottomSheetFragment :
    BaseStoreBottomSheetFragment<
        SheetSearchBinding,
        SearchLogsViewState,
        SearchLogsState,
        SearchLogsCommand,
        SearchLogsSideEffect,
        SearchLogsViewModel,
        >() {

    override val viewModel by viewModels<SearchLogsViewModel>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        SheetSearchBinding.inflate(inflater, container, false)

    override fun SheetSearchBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Setup click listeners using the binding receiver
        searchButton.setOnClickListener { send(SearchLogsCommand.UpdateQuery(queryText.text?.toString())) }
    }

    override fun render(state: SearchLogsViewState) {
        binding.queryText.setText(state.query)
        binding.caseSensitiveCheckbox.isChecked = state.caseSensitive
    }

    override fun handleSideEffect(sideEffect: SearchLogsSideEffect) {
        when (sideEffect) {
            is SearchLogsSideEffect.Dismiss -> dismiss()
            else -> Unit
        }
    }
}
```

### Views: Passive View with ViewBinding
```kotlin
// Passive View - only displays data via bind method, triggers events via lambdas
class LoginView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding = ViewLoginBinding.inflate(LayoutInflater.from(context), this)

    var onEmailChanged: ((String) -> Unit)? = null
    var onPasswordChanged: ((String) -> Unit)? = null
    var onLoginClick: (() -> Unit)? = null

    init {
        binding.emailEditText.doAfterTextChanged { onEmailChanged?.invoke(it.toString()) }
        binding.passwordEditText.doAfterTextChanged { onPasswordChanged?.invoke(it.toString()) }
        binding.loginButton.setOnClickListener { onLoginClick?.invoke() }
    }

    fun bind(state: AuthState) {
        binding.emailEditText.setTextIfDifferent(state.email)
        binding.passwordEditText.setTextIfDifferent(state.password)
        binding.loginButton.isEnabled = !state.isLoading
        binding.progressBar.isVisible = state.isLoading
        binding.errorText.text = state.error
        binding.errorText.isVisible = state.error != null
    }
}

// Helper extension to prevent cursor jumping
private fun EditText.setTextIfDifferent(text: String) {
    if (this.text.toString() != text) {
        this.setText(text)
    }
}
```

### Compose: Passive Composable Example
```kotlin
// CORRECT: Passive composable - only displays data, triggers events via lambdas
@Composable
fun LoginContent(
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TextField(
            value = email,
            onValueChange = onEmailChanged,
            label = { Text("Email") },
        )

        TextField(
            value = password,
            onValueChange = onPasswordChanged,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
        )

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Button(
            onClick = onLoginClick,
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Login")
            }
        }
    }
}

// WRONG: Composable handling its own state
@Composable
fun LoginContentWrong() {
    var email by remember { mutableStateOf("") }      // WRONG: Composable handling state
    var password by remember { mutableStateOf("") }   // WRONG: Composable handling state
    var isLoading by remember { mutableStateOf(false) } // WRONG: Composable handling state
    // ...
}
```

### Compose: Container Screen Example
```kotlin
// Container - the only composable that can hold ViewModel and handle lifecycle
// state is already ViewState (mapped by the ViewModel)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { sideEffect ->
            // Handle UI-related side effects, ignore business logic ones
            when (sideEffect) {
                is AuthSideEffect.NavigateToHome -> onNavigateToHome()
                is AuthSideEffect.ShowError -> { /* show snackbar */ }
                // Business logic side effects - handled by EffectHandlers, ignore here
                else -> Unit
            }
        }
    }

    LoginContent(
        email = state.email,
        password = state.password,
        isLoading = state.isLoading,
        error = state.error,
        onEmailChanged = { viewModel.send(AuthCommand.EmailChanged(it)) },
        onPasswordChanged = { viewModel.send(AuthCommand.PasswordChanged(it)) },
        onLoginClick = { viewModel.send(AuthCommand.LoginTapped) },
    )
}
```

### Nested Passive Views
```kotlin
// Parent passive view
@Composable
fun ProfileContent(
    user: User,
    settings: Settings,
    onEditTapped: () -> Unit,
    onSettingChanged: (Setting) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Child passive view - receives data, passes lambdas
        ProfileHeaderContent(
            name = user.name,
            email = user.email,
            onEditTapped = onEditTapped,
        )

        // Another child passive view
        SettingsListContent(
            settings = settings,
            onSettingChanged = onSettingChanged,
        )
    }
}

// Child passive view
@Composable
fun ProfileHeaderContent(
    name: String,
    email: String,
    onEditTapped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.headlineSmall)
            Text(text = email, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onEditTapped) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }
    }
}
```

---

## 4. Reactive Data Flow

### Overview
Data drives the application reactively. Use `StateFlow` and `SharedFlow` from kotlinx.coroutines to expose reactive state.

### Repository Reactive Streams
```kotlin
// Repository interfaces with Flow - in api module
interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    suspend fun login(email: String, password: String): User
    suspend fun logout()
}

interface OnboardingRepository {
    val wasOnboardingCompleted: Flow<Boolean>
    suspend fun markOnboardingCompleted()
}

interface SettingsRepository {
    val selectedServer: Flow<Server?>
    suspend fun selectServer(server: Server)
}
```

### StateFlow Implementation in Data Layer

**Critical Rules:**
- Use `MutableStateFlow` privately in data sources
- Expose as `Flow` (read-only)
- Use `StateFlow` for state that needs an initial value

```kotlin
// Implementation - in impl module, internal
internal class AuthLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AuthLocalDataSource {

    private val _isAuthenticated = MutableStateFlow(false)
    
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        // Initialize
        val hasToken = getAccessToken() != null
        _isAuthenticated.value = hasToken
    }

    override suspend fun saveTokens(tokens: AuthTokens) {
        // ... save tokens ...
        _isAuthenticated.value = true
    }

    override suspend fun clearTokens() {
        // ... clear tokens ...
        _isAuthenticated.value = false
    }
}
```

### Combining Flows
Use `combine` from kotlinx.coroutines to combine multiple flows.

```kotlin
// Use case that combines multiple flows
interface ObserveAppStateUseCase {
    operator fun invoke(): Flow<AppScreen>
}

internal class ObserveAppStateUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
) : ObserveAppStateUseCase {

    override fun invoke(): Flow<AppScreen> = combine(
        onboardingRepository.wasOnboardingCompleted,
        authRepository.isAuthenticated,
    ) { isOnboardingCompleted, isAuthenticated ->
        when {
            !isOnboardingCompleted -> AppScreen.Onboarding
            !isAuthenticated -> AppScreen.Auth
            else -> AppScreen.Main
        }
    }
}

// Usage in ViewModel
@HiltViewModel
class ContentViewModel @Inject constructor(
    private val observeAppStateUseCase: ObserveAppStateUseCase,
) : ViewModel() {

    val appScreen: StateFlow<AppScreen> = observeAppStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppScreen.Loading,
        )
}
```

---

## 5. Modularization

### Overview
The project follows a modular architecture with two types of modules: **core** and **feature**.

### Module Types

#### Core Modules
- **Purpose**: Reusable components across ANY application
- **Examples**: Base classes, networking, persistence, common UI components
- **Characteristics**: 
  - Generic, not app-specific
  - Can be extracted as a library
  - Examples: `BaseFragment`, `BaseViewModel`, networking utilities

#### Feature Modules
- **Purpose**: App-specific functionality
- **Examples**: Authentication, profile, settings, specific domain models
- **Characteristics**: 
  - Contains business logic specific to this app
  - Domain-specific models and use cases

### Module Structure

Each module can consist of up to 3 Gradle modules:

```
feature/auth/
├── api/                    # MANDATORY - Interfaces and domain models
│   └── build.gradle.kts    # Uses: logfox.kotlin.jvm (preferably) or logfox.android.library
├── impl/                   # MANDATORY - Implementations and DI
│   └── build.gradle.kts    # Uses: logfox.kotlin.jvm (preferably) or logfox.android.feature + depends on api
└── presentation/           # OPTIONAL - UI layer (only for features with UI)
    └── build.gradle.kts    # Uses: logfox.android.feature.compose + depends on api ONLY
```

#### api module (MANDATORY)
- Contains interfaces and domain models
- Pure Kotlin when possible (`logfox.kotlin.jvm`)
- Use `logfox.android.library` only when Android-specific types needed
- **NO implementations, NO DI annotations**

```kotlin
// feature/auth/api/src/main/kotlin/com/f0x1d/logfox/feature/auth/api/AuthRepository.kt
interface AuthRepository {
    suspend fun login(email: String, password: String): User
    val isAuthenticated: Flow<Boolean>
}

// feature/auth/api/src/main/kotlin/com/f0x1d/logfox/feature/auth/api/LoginUseCase.kt
interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}

// feature/auth/api/src/main/kotlin/com/f0x1d/logfox/feature/auth/api/User.kt
data class User(
    val id: String,
    val email: String,
    val name: String,
)
```

#### impl module (MANDATORY)
- Contains implementations of api interfaces
- Contains data sources, DTOs, mappers
- Contains Hilt DI module
- Depends on api module
- Pure Kotlin when possible, Android when needed

```kotlin
// feature/auth/impl/src/main/kotlin/com/f0x1d/logfox/feature/auth/impl/AuthRepositoryImpl.kt
internal class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) : AuthRepository {
    // Implementation...
}

// feature/auth/impl/src/main/kotlin/com/f0x1d/logfox/feature/auth/impl/di/AuthModule.kt
@Module
@InstallIn(SingletonComponent::class)
internal interface AuthModule {
    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase
}
```

#### presentation module (OPTIONAL)
- Contains UI components (Views/Composables) and ViewModels
- Contains TEA components: State, ViewState, ViewStateMapper, Command, SideEffect, Reducer, EffectHandler
- **Depends ONLY on api module** - NEVER on impl
- Android library module (required for UI components)
- Uses `logfox.android.feature.compose` for Compose UI

```kotlin
// feature/auth/presentation/src/main/kotlin/com/f0x1d/logfox/feature/auth/presentation/AuthViewModel.kt
@HiltViewModel
internal class AuthViewModel @Inject constructor(
    reducer: AuthReducer,
    effectHandler: AuthEffectHandler,
    viewStateMapper: AuthViewStateMapper,
) : BaseStoreViewModel<AuthViewState, AuthState, AuthCommand, AuthSideEffect>(
    initialState = AuthState(),
    reducer = reducer,
    effectHandlers = listOf(effectHandler),
    viewStateMapper = viewStateMapper,
)

// feature/auth/presentation/src/main/kotlin/com/f0x1d/logfox/feature/auth/presentation/AuthScreen.kt
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle() // Already ViewState
    // Implementation...
}
```

### Simplified Modules
Sometimes modules don't need the full 3-module structure:

#### Core utility modules (no interfaces to expose)
```
core/tea/
├── base/
│   └── build.gradle.kts    # Pure Kotlin JVM - Store, Reducer, ReduceResult, EffectHandler, ViewStateMapper
└── android/
    └── build.gradle.kts    # Android - BaseStoreViewModel, BaseStoreFragment, BaseStoreBottomSheetFragment, BaseStorePreferenceFragment

core/ui/
└── build.gradle.kts    # Contains BaseActivity, theme

core/common/
└── build.gradle.kts    # Contains common extensions, utilities
```

#### Common data models used everywhere
```
feature/common/
└── build.gradle.kts    # Contains common data classes, no api/impl split needed
```

### Dependency Rules Between Modules

```
                    :app
 (depends on all presentation and impl modules)
                     │
     ┌───────────────┼───────────────┐
     ▼               ▼               ▼
:feature:       :feature:       :core:
auth:           profile:        network:
presentation    presentation    impl
     │               │               │
     ▼               ▼               ▼
:feature:       :feature:       :core:
auth:api        profile:api     network:api
```

**Critical Rules:**
- `presentation` modules depend ONLY on `api` modules
- `impl` modules depend on their `api` module
- `impl` modules can depend on other modules' `api` modules
- `:app` module depends on all `presentation` and `impl` modules
- NEVER depend on another module's `impl` module (except `:app`)

### build.gradle.kts Examples

```kotlin
// feature/auth/api/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.kotlin.jvm)
}

dependencies {
    api(libs.kotlinx.coroutines.core) // For Flow
}

// feature/auth/impl/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.feature)
}

dependencies {
    api(projects.feature.auth.api)
    
    implementation(projects.core.network.api)
    implementation(projects.core.persistence.api)
}

// feature/auth/presentation/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

dependencies {
    implementation(projects.feature.auth.api) // ONLY api, never impl
    
    implementation(projects.core.ui.api)
}
```

---

## 6. Dependency Injection (Hilt)

### Overview
Use Hilt for dependency injection. Each feature's impl module contains its DI module.

**Critical Rules:**
- Hilt module return types MUST be interfaces, NEVER implementations
- Implementations (`*Impl`) must NEVER be used directly outside of impl modules
- Use `@Binds` for binding interface to implementation
- Use `@Provides` for providing instances that require configuration

### Module Structure

```kotlin
// In feature/auth/impl module
@Module
@InstallIn(SingletonComponent::class)
internal interface AuthBindsModule {

    @Binds
    fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase

    @Binds
    fun bindLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase
}

@Module
@InstallIn(SingletonComponent::class)
internal object AuthProvidesModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}
```

### Data Source Binding
```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataSourceModule {

    @Binds
    fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton // CRITICAL: shared state requires singleton
    fun bindAuthLocalDataSource(impl: AuthLocalDataSourceImpl): AuthLocalDataSource
}
```

### TEA Components Binding
Note: Reducers, EffectHandlers, and ViewStateMappers are `@Inject`-constructed and used directly in the ViewModel constructor — they do NOT need DI module bindings in most cases. Hilt can construct them directly.

DI module bindings are only needed when:
- You need to provide a `List<EffectHandler<...>>` for multiple effect handlers
- You want to bind to the generic interface type (e.g., `Reducer<State, Command, SideEffect>`)

```kotlin
// For multiple effect handlers, provide as List
@Module
@InstallIn(ViewModelComponent::class)
internal object AuthEffectHandlersModule {

    @Provides
    fun provideEffectHandlers(
        networkHandler: AuthNetworkEffectHandler,
        persistenceHandler: AuthPersistenceEffectHandler,
    ): List<EffectHandler<AuthSideEffect, AuthCommand>> = listOf(
        networkHandler,
        persistenceHandler,
    )
}
```

In practice, most ViewModels inject the concrete Reducer, EffectHandler, and ViewStateMapper types directly:

```kotlin
@HiltViewModel
internal class AuthViewModel @Inject constructor(
    reducer: AuthReducer,                    // concrete type, no binding needed
    effectHandler: AuthEffectHandler,        // concrete type, no binding needed
    viewStateMapper: AuthViewStateMapper,    // concrete type, no binding needed
) : BaseStoreViewModel<AuthViewState, AuthState, AuthCommand, AuthSideEffect>(...)
```

### Scopes and Lifecycle
```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface AuthModule {

    // Singleton - lives for app lifetime (shared state)
    @Binds
    @Singleton
    fun bindTokenStore(impl: TokenStoreImpl): TokenStore

    // Unscoped - new instance each time (stateless)
    @Binds
    fun bindLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase
}
```

### Singleton Data Sources

**Critical Rule:** Data sources that maintain shared state (e.g., `StateFlow`, in-memory caches) and are used by multiple repositories **MUST be singletons**.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal interface AuthDataSourceModule {

    // SINGLETON: Local data source with shared StateFlow
    @Binds
    @Singleton
    fun bindAuthLocalDataSource(impl: AuthLocalDataSourceImpl): AuthLocalDataSource
}

// Example: Data source with shared state
internal class AuthLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AuthLocalDataSource {

    private val _isAuthenticated = MutableStateFlow(false)
    
    override val isAuthenticated: Flow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun saveToken(token: String) {
        // All observers receive this update
        _isAuthenticated.value = true
    }
}
```

---

## 7. Navigation

### Overview
Use Jetpack Navigation for navigation. Support both Fragment-based navigation (legacy) and Compose navigation.

### Navigation with Fragments
```kotlin
// Use Safe Args for type-safe navigation
// Navigation graph defines destinations

// In Fragment
class AuthFragment : BaseFragment<FragmentAuthBinding>() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToHome -> {
                        findNavController().navigate(AuthFragmentDirections.actionAuthToHome())
                    }
                    is AuthEffect.NavigateToRegister -> {
                        findNavController().navigate(AuthFragmentDirections.actionAuthToRegister())
                    }
                }
            }
        }
    }
}
```

### SideEffect-based Navigation
Navigation should be triggered by SideEffects from Reducer, NOT directly from UI events.

```kotlin
// SideEffect includes navigation variants
sealed interface AuthSideEffect {
    // Business logic
    data class Login(val email: String, val password: String) : AuthSideEffect
    data object Logout : AuthSideEffect
    
    // UI/Navigation
    data object NavigateToHome : AuthSideEffect
    data object NavigateToRegister : AuthSideEffect
    data class NavigateToForgotPassword(val email: String?) : AuthSideEffect
    data class ShowError(val message: String) : AuthSideEffect
}

// Container handles navigation (Compose)
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                AuthSideEffect.NavigateToHome -> onNavigateToHome()
                AuthSideEffect.NavigateToRegister -> onNavigateToRegister()
                is AuthSideEffect.NavigateToForgotPassword -> { /* navigate */ }
                is AuthSideEffect.ShowError -> { /* show snackbar */ }
                else -> Unit // Business logic handled by EffectHandlers
            }
        }
    }
    // ...
}

// Container handles navigation (Fragment with BaseStoreFragment)
override fun handleSideEffect(sideEffect: AuthSideEffect) {
    when (sideEffect) {
        AuthSideEffect.NavigateToHome -> {
            findNavController().navigate(AuthFragmentDirections.actionAuthToHome())
        }
        AuthSideEffect.NavigateToRegister -> {
            findNavController().navigate(AuthFragmentDirections.actionAuthToRegister())
        }
        is AuthSideEffect.NavigateToForgotPassword -> {
            findNavController().navigate(
                AuthFragmentDirections.actionAuthToForgotPassword(sideEffect.email)
            )
        }
        is AuthSideEffect.ShowError -> {
            Snackbar.make(binding.root, sideEffect.message, Snackbar.LENGTH_SHORT).show()
        }
        else -> Unit // Business logic handled by EffectHandlers
    }
}
```

---

## 8. Convention Plugins

### Available Plugins

| Plugin ID | Description | Use Case |
|-----------|-------------|----------|
| `logfox.kotlin.jvm` | Pure Kotlin JVM module | api modules without Android dependencies |
| `logfox.android.library` | Android library module | Android-specific api or standalone modules |
| `logfox.android.feature` | Feature module with Hilt | impl modules |
| `logfox.android.feature.compose` | Feature + Compose + Tests | presentation modules with Compose UI |
| `logfox.android.hilt` | Adds Hilt DI | Added automatically by feature plugins |
| `logfox.android.compose` | Adds Compose | Added automatically by compose feature plugin |
| `logfox.android.room` | Adds Room database | Modules using Room |
| `logfox.android.parcelize` | Adds Parcelize | Modules with Parcelable classes |

### Plugin Hierarchy
```
logfox.android.feature.compose
    └── logfox.android.feature
        └── logfox.android.library
        └── logfox.android.hilt
    └── logfox.android.compose
```

### Usage Examples

```kotlin
// Pure Kotlin api module
// feature/auth/api/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.kotlin.jvm)
}

// Android api module (when Android types needed)
// core/context/api/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.library)
}

// impl module
// feature/auth/impl/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.feature)
}

// presentation module with Compose
// feature/auth/presentation/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.feature.compose)
}

// Database module
// core/database/impl/build.gradle.kts
plugins {
    alias(libs.plugins.logfox.android.feature)
    alias(libs.plugins.logfox.android.room)
}
```

---

## 9. Project Structure

### One Type Per File Rule

**Critical Rule:** Each file MUST contain exactly ONE type (class, interface, object, enum, or typealias), regardless of visibility.

**Rules:**
- One file = One type (class/interface/object/enum/data class/typealias)
- File name MUST match the type name (e.g., `LoginUseCase.kt` for `interface LoginUseCase`)
- Extensions of the same type are allowed in the same file
- Private helper extensions of OTHER types are allowed in the same file
- NO private/internal helper types in the same file - extract them to separate files
- Group related files into appropriate packages

**Examples:**
```
// CORRECT: One type per file
domain/usecase/
├── LoginUseCase.kt           // interface LoginUseCase
├── LoginUseCaseImpl.kt       // class LoginUseCaseImpl
├── LogoutUseCase.kt          // interface LogoutUseCase
├── LogoutUseCaseImpl.kt      // class LogoutUseCaseImpl

// WRONG: Multiple types in one file
domain/usecase/
├── AuthUseCases.kt           // Contains LoginUseCase, LogoutUseCase, etc.
```

### Folder Organization

```
app/
├── src/main/kotlin/com/f0x1d/logfox/
│   ├── App.kt                    # Application class
│   ├── MainActivity.kt           # Main activity
│   └── navigation/
│       └── AppNavigation.kt      # Root navigation

core/
├── tea/
│   ├── base/
│   │   └── src/main/kotlin/com/f0x1d/logfox/core/tea/
│   │       ├── Store.kt                  # TEA Store implementation
│   │       ├── Reducer.kt                # Reducer interface
│   │       ├── ReduceResult.kt           # ReduceResult data class
│   │       ├── EffectHandler.kt          # EffectHandler interface (extends Closeable)
│   │       └── ViewStateMapper.kt        # ViewStateMapper interface
│   └── android/
│       └── src/main/kotlin/com/f0x1d/logfox/core/tea/
│           ├── BaseStoreViewModel.kt             # Base ViewModel for TEA
│           ├── BaseStoreFragment.kt              # Base Fragment for TEA
│           ├── BaseStoreBottomSheetFragment.kt   # Base BottomSheet for TEA
│           └── BaseStorePreferenceFragment.kt    # Base PreferenceFragment for TEA
├── ui/
│   └── src/main/kotlin/com/f0x1d/logfox/core/ui/
│       ├── BaseFragment.kt           # Simple base Fragment
│       └── theme/
│           ├── Theme.kt
│           └── Color.kt
├── network/
│   ├── api/
│   │   └── src/main/kotlin/com/f0x1d/logfox/core/network/api/
│   │       ├── HttpClient.kt         # Interface
│   │       └── NetworkError.kt       # Sealed class
│   └── impl/
│       └── src/main/kotlin/com/f0x1d/logfox/core/network/impl/
│           ├── HttpClientImpl.kt     # Implementation
│           └── di/
│               └── NetworkModule.kt  # Hilt module
├── persistence/
│   ├── api/
│   │   └── src/main/kotlin/com/f0x1d/logfox/core/persistence/api/
│   │       └── DataStoreClient.kt    # Interface
│   └── impl/
│       └── src/main/kotlin/com/f0x1d/logfox/core/persistence/impl/
│           ├── DataStoreClientImpl.kt
│           └── di/
│               └── PersistenceModule.kt
└── common/
    └── src/main/kotlin/com/f0x1d/logfox/core/common/
        └── extensions/
            ├── FlowExtensions.kt
            └── ContextExtensions.kt

feature/
├── auth/
│   ├── api/
│   │   └── src/main/kotlin/com/f0x1d/logfox/feature/auth/api/
│   │       ├── AuthRepository.kt
│   │       ├── LoginUseCase.kt
│   │       ├── LogoutUseCase.kt
│   │       └── User.kt
│   ├── impl/
│   │   └── src/main/kotlin/com/f0x1d/logfox/feature/auth/impl/
│   │       ├── AuthRepositoryImpl.kt
│   │       ├── LoginUseCaseImpl.kt
│   │       ├── LogoutUseCaseImpl.kt
│   │       ├── datasource/
│   │       │   ├── AuthRemoteDataSource.kt
│   │       │   ├── AuthRemoteDataSourceImpl.kt
│   │       │   ├── AuthLocalDataSource.kt
│   │       │   └── AuthLocalDataSourceImpl.kt
│   │       ├── dto/
│   │       │   ├── UserDTO.kt
│   │       │   └── LoginRequestDTO.kt
│   │       ├── mapper/
│   │       │   └── UserMapper.kt
│   │       └── di/
│   │           └── AuthModule.kt
│   └── presentation/
│       └── src/main/kotlin/com/f0x1d/logfox/feature/auth/presentation/
│           ├── AuthViewModel.kt              # Feature ViewModel
│           ├── AuthState.kt                  # Internal domain State
│           ├── AuthViewState.kt              # Presentation-ready ViewState
│           ├── AuthViewStateMapper.kt        # State -> ViewState mapper (implements ViewStateMapper)
│           ├── AuthCommand.kt                # User actions / feedback commands
│           ├── AuthSideEffect.kt             # Side effects (business + UI)
│           ├── AuthReducer.kt                # Pure reducer function
│           ├── AuthNetworkEffectHandler.kt   # Network side effect handler
│           ├── AuthPersistenceEffectHandler.kt # Persistence side effect handler
│           ├── AuthScreen.kt                 # Container composable
│           ├── AuthFragment.kt               # Container fragment (if using Views)
│           └── component/
│               ├── LoginContent.kt           # Passive composable
│               └── RegisterContent.kt
├── profile/
│   ├── api/
│   ├── impl/
│   └── presentation/
└── settings/
    ├── api/
    ├── impl/
    └── presentation/
```

### Package Naming

**Critical Rule:** Every api, impl, and presentation Gradle module MUST include its module type as a package segment. The package pattern is:

```
com.f0x1d.logfox.<module-type>.<module-name>.<api|impl|presentation>[.subpackage]
```

Where:
- `<module-type>` is `feature` or `core`
- `<module-name>` is the feature/core name (e.g., `auth`, `logging`, `preferences`)
- `<api|impl|presentation>` corresponds to the Gradle sub-module

**Examples:**

| Gradle module | Package root |
|---|---|
| `:app` | `com.f0x1d.logfox` |
| `:feature:auth:api` | `com.f0x1d.logfox.feature.auth.api` |
| `:feature:auth:impl` | `com.f0x1d.logfox.feature.auth.impl` |
| `:feature:auth:presentation` | `com.f0x1d.logfox.feature.auth.presentation` |
| `:core:preferences:api` | `com.f0x1d.logfox.core.preferences.api` |
| `:core:preferences:impl` | `com.f0x1d.logfox.core.preferences.impl` |
| `:core:ui:base` | `com.f0x1d.logfox.core.ui` (standalone, no api/impl split) |

Sub-packages within each module follow naturally:
```
com.f0x1d.logfox.feature.auth.api.data          # repository interfaces
com.f0x1d.logfox.feature.auth.api.domain         # use case interfaces
com.f0x1d.logfox.feature.auth.api.model          # domain models
com.f0x1d.logfox.feature.auth.impl.data          # repository implementations, data sources
com.f0x1d.logfox.feature.auth.impl.di            # Hilt modules
com.f0x1d.logfox.feature.auth.impl.domain        # use case implementations
com.f0x1d.logfox.feature.auth.presentation.ui    # fragments, screens
```

**Why this matters:**
- Prevents package collisions between api and impl modules (e.g., both having a `data` sub-package)
- Makes it immediately obvious from an import which module a class belongs to
- The `android.namespace` in `build.gradle.kts` MUST match the package root (e.g., `com.f0x1d.logfox.feature.auth.api`)

---

## Summary of Critical Rules

1. **TEA Pattern**: State is immutable, Commands trigger state changes via Reducer, SideEffects handled by both EffectHandlers (business) and UI (navigation/toast)
2. **Reducer**: Pure function, takes State + Command, returns new State + SideEffects. NO side effects allowed in reducer
3. **EffectHandler**: Handles SideEffects asynchronously, `onCommand` is **suspend** and uses `withContext(Dispatchers.Main.immediate)` to call `Store.send()`. Extends `Closeable` for resource cleanup
4. **Store.send()**: MUST be called only from Main thread
5. **SideEffects**: Serve dual purpose - business logic (handled by EffectHandlers) and UI actions (handled by Fragment/Composable)
6. **ViewState is MANDATORY**: Every feature has State (domain, managed by Reducer) and ViewState (presentation, derived by ViewStateMapper). ViewStateMapper implements `ViewStateMapper<State, ViewState>` interface from `core/tea`
7. **BaseStoreViewModel**: 4 type params `<ViewState, State, Command, SideEffect>`, maps State -> ViewState internally, exposes `StateFlow<ViewState>`
8. **BaseStoreFragment**: 6 type params `<VB, ViewState, State, Command, SideEffect, VM>`, renders ViewState, uses `inflateBinding()` and `VB.onViewCreated()`
9. **Base Fragment Variants**: `BaseStoreFragment`, `BaseStoreBottomSheetFragment`, `BaseStorePreferenceFragment` - all in `core/tea/android`
10. **core/tea module split**: `core/tea/base/` (pure Kotlin JVM - Store, Reducer, ReduceResult, EffectHandler, ViewStateMapper) and `core/tea/android/` (Android - BaseStoreViewModel, BaseStoreFragment, etc.)
11. **Use Cases**: Must use `invoke` operator, return `Result<T>` for failable operations
12. **Repositories**: Methods can throw, expose `Flow` for reactive data
13. **Data Sources**: Internal to impl module, never exposed outside
14. **Views**: Passive, only display data and trigger events via lambdas
15. **Modularization**: api/impl/presentation structure, presentation depends ONLY on api
16. **DI**: Use Hilt `@Binds` for interfaces, singleton for shared state. TEA components (Reducer, EffectHandler, ViewStateMapper) are `@Inject`-constructed and used directly - no DI binding needed unless providing a list
17. **Navigation**: SideEffect-based, handled in container components
18. **File Structure**: One type per file, file name matches type name
19. **Convention Plugins**: Use appropriate plugin for each module type
20. **Package Naming**: Every api/impl/presentation module MUST include its module type as a package segment: `com.f0x1d.logfox.<feature|core>.<name>.<api|impl|presentation>`. The `android.namespace` in `build.gradle.kts` MUST match this package root
