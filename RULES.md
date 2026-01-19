# Architecture Decision Record: Android Application Architecture

## Context
This document defines the architectural rules and patterns for building Android applications in this project. It establishes conventions for state management, layer separation, dependency injection, modularization, and UI patterns using both Views and Jetpack Compose.

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
// Interface - in core/tea module, public
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
Effect handlers process side effects and send feedback via `onCommand` suspend function. The `onCommand` **MUST be suspend** and internally uses `withContext(Dispatchers.Main)` to ensure thread safety since `Store.send()` must be called from Main thread.

Multiple effect handlers can be specified, each with its own role.

```kotlin
// Interface - in core/tea module, public
interface EffectHandler<SideEffect, Command> {
    suspend fun handle(effect: SideEffect, onCommand: suspend (Command) -> Unit)
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

            else -> {
                // Handled by different effect handler or UI
            }
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

            else -> {
                // Handled by different effect handler or UI
            }
        }
    }
}
```

### Store
The store orchestrates state, reducer, and effect handlers. Must support cancellation via coroutine job management. **`send()` must be called only from Main thread.**

```kotlin
// In core/tea module, public
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
    }
}
```

### BaseStoreViewModel
Base ViewModel that integrates Store with Android lifecycle.

```kotlin
// In core/tea module, public
abstract class BaseStoreViewModel<State, Command, SideEffect>(
    initialState: State,
    reducer: Reducer<State, Command, SideEffect>,
    effectHandlers: List<EffectHandler<SideEffect, Command>>,
    initialSideEffect: SideEffect? = null,
) : ViewModel() {

    private val store = Store(
        initialState = initialState,
        reducer = reducer,
        effectHandlers = effectHandlers,
        scope = viewModelScope,
    )

    val state: StateFlow<State> = store.state
    val sideEffects: SharedFlow<SideEffect> = store.sideEffects

    init {
        initialSideEffect?.let { effect ->
            effectHandlers.forEach { handler ->
                viewModelScope.launch {
                    handler.handle(effect) { cmd ->
                        withContext(Dispatchers.Main) {
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

### Feature ViewModel
Feature-specific ViewModel that extends BaseStoreViewModel.

```kotlin
// Feature ViewModel - internal visibility
@HiltViewModel
internal class AuthViewModel @Inject constructor(
    reducer: AuthReducer,
    networkEffectHandler: AuthNetworkEffectHandler,
    persistenceEffectHandler: AuthPersistenceEffectHandler,
) : BaseStoreViewModel<AuthState, AuthCommand, AuthSideEffect>(
    initialState = AuthState(),
    reducer = reducer,
    effectHandlers = listOf(networkEffectHandler, persistenceEffectHandler),
)
```

### Type Alias for Convenience
```kotlin
typealias AuthStore = Store<AuthState, AuthCommand, AuthSideEffect>
```

### Complete Data Flow
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              UI LAYER                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         Fragment / Composable                        │   │
│  │  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────────┐  │   │
│  │  │   render()  │◄───│    state    │    │   handleSideEffect()    │  │   │
│  │  │  (UI state) │    │  StateFlow  │    │  (navigation/toast)     │  │   │
│  │  └─────────────┘    └─────────────┘    └─────────────────────────┘  │   │
│  │         │                  ▲                       ▲                 │   │
│  │         │ send(Command)    │                       │ sideEffects     │   │
│  │         ▼                  │                       │ SharedFlow      │   │
│  │  ┌─────────────────────────┴───────────────────────┴─────────────┐  │   │
│  │  │                        ViewModel                               │  │   │
│  │  └───────────────────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                             STORE                                           │
│  ┌─────────────┐         ┌─────────────┐         ┌─────────────────────┐   │
│  │   Command   │ ──────► │   Reducer   │ ──────► │     SideEffect      │   │
│  │             │         │ (pure func) │         │                     │   │
│  └─────────────┘         └─────────────┘         └─────────────────────┘   │
│                                 │                     │         │           │
│                                 ▼                     │         │           │
│                          ┌───────────┐                │         │           │
│                          │   State   │                │         │           │
│                          │  (new)    │                │         │           │
│                          └───────────┘                │         │           │
│                                                       │         │           │
│                          ┌────────────────────────────┘         │           │
│                          │                                      │           │
│                          ▼                                      ▼           │
│               ┌───────────────────┐                   ┌─────────────────┐  │
│               │  EffectHandler(s) │                   │  UI (Fragment)  │  │
│               │  (network, etc.)  │                   │ (nav, toast)    │  │
│               └───────────────────┘                   └─────────────────┘  │
│                          │                                                  │
│                          │ onCommand (suspend)                              │
│                          │ withContext(Main)                                │
│                          ▼                                                  │
│                    ┌───────────┐                                            │
│                    │   Store   │                                            │
│                    │  (send)   │  ◄── Must be called from Main thread       │
│                    └───────────┘                                            │
└─────────────────────────────────────────────────────────────────────────────┘
                                  │
                                  │ via Use Cases
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          Use Cases                                   │   │
│  │              (invoke operator, returns Result<T>)                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           DATA LAYER                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                   Repository Implementations                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                    ┌─────────────┴─────────────┐                           │
│                    ▼                           ▼                            │
│  ┌─────────────────────────┐    ┌─────────────────────────┐                │
│  │   Remote Data Source    │    │    Local Data Source    │                │
│  └─────────────────────────┘    └─────────────────────────┘                │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Key Flow Steps
1. **User Action**: Fragment receives user input, calls `viewModel.send(Command)`
2. **Reducer**: Store passes Command to Reducer, which returns new State + SideEffects
3. **State Update**: Store updates StateFlow, Fragment receives new state via `render()`
4. **SideEffect Emission**: Store emits SideEffects to SharedFlow (for UI) AND passes to EffectHandlers
5. **UI SideEffects**: Fragment receives SideEffects via `handleSideEffect()`, performs navigation/toast
6. **Business SideEffects**: EffectHandlers process SideEffects asynchronously (network, persistence)
7. **Feedback**: EffectHandlers call `onCommand` (suspend) which uses `withContext(Main)` to call `Store.send()`

---

## 2. Clean Architecture

### Layer Structure
Each feature/module contains three layers:
- **Presentation**: UI components (Views/Composables) and ViewModels
- **Domain**: Use cases and repository interfaces
- **Data**: Data sources and repository implementations

### Dependency Rules
```
┌─────────────────┐
│  Presentation   │ ──────► Domain (api module)
└─────────────────┘
                              ▲
┌─────────────────┐           │
│      Data       │ ──────────┘
│  (impl module)  │
└─────────────────┘
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

// Usage in ViewModel - clean call-site
private fun login(email: String, password: String) {
    viewModelScope.launch {
        loginUseCase(email, password) // Clean call-site via invoke operator
            .onSuccess { user -> /* handle success */ }
            .onFailure { error -> /* handle error */ }
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
Base classes abstract away Flow collection boilerplate. Feature fragments only need to implement `render()` and `handleSideEffect()`.

```kotlin
// In core/ui module - Base Fragment for TEA architecture
abstract class BaseStoreFragment<VB : ViewBinding, State, Command, SideEffect, VM : BaseStoreViewModel<State, Command, SideEffect>> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract val viewModel: VM

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Render state to UI. Called on every state change.
     * Must be idempotent - same state = same UI.
     */
    abstract fun render(state: State)

    /**
     * Handle side effects (navigation, snackbars, etc.)
     * Called for ALL side effects - ignore those not relevant to UI.
     */
    abstract fun handleSideEffect(sideEffect: SideEffect)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

### Feature Fragment Example
```kotlin
// Container Fragment - extends BaseStoreFragment, only implements render and handleSideEffect
@AndroidEntryPoint
class AuthFragment : BaseStoreFragment<FragmentAuthBinding, AuthState, AuthCommand, AuthSideEffect, AuthViewModel>() {

    override val viewModel: AuthViewModel by viewModels()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentAuthBinding = FragmentAuthBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup event listeners - send commands to ViewModel
        binding.loginView.onEmailChanged = { send(AuthCommand.EmailChanged(it)) }
        binding.loginView.onPasswordChanged = { send(AuthCommand.PasswordChanged(it)) }
        binding.loginView.onLoginClick = { send(AuthCommand.LoginTapped) }
    }

    override fun render(state: AuthState) {
        // Pure rendering - same state = same UI
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
            else -> {}
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
                else -> {}
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
// feature/auth/api/src/main/kotlin/com/example/feature/auth/AuthRepository.kt
interface AuthRepository {
    suspend fun login(email: String, password: String): User
    val isAuthenticated: Flow<Boolean>
}

// feature/auth/api/src/main/kotlin/com/example/feature/auth/LoginUseCase.kt
interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): Result<User>
}

// feature/auth/api/src/main/kotlin/com/example/feature/auth/User.kt
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
// feature/auth/impl/src/main/kotlin/com/example/feature/auth/AuthRepositoryImpl.kt
internal class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) : AuthRepository {
    // Implementation...
}

// feature/auth/impl/src/main/kotlin/com/example/feature/auth/di/AuthModule.kt
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
- **Depends ONLY on api module** - NEVER on impl
- Android library module (required for UI components)
- Uses `logfox.android.feature.compose` for Compose UI

```kotlin
// feature/auth/presentation/src/main/kotlin/com/example/feature/auth/AuthViewModel.kt
@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase, // From api module
) : ViewModel() {
    // Implementation...
}

// feature/auth/presentation/src/main/kotlin/com/example/feature/auth/AuthScreen.kt
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
) {
    // Implementation...
}
```

### Simplified Modules
Sometimes modules don't need the full 3-module structure:

#### Core utility modules (no interfaces to expose)
```
core/tea/
└── build.gradle.kts    # Contains Store, Reducer, EffectHandler, BaseStoreViewModel

core/ui/
└── build.gradle.kts    # Contains BaseStoreFragment, BaseActivity, theme

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
┌─────────────────────────────────────────────────────┐
│                    :app                             │
│  (depends on all presentation and impl modules)     │
└─────────────────────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ :feature:   │   │ :feature:   │   │ :core:      │
│ auth:       │   │ profile:    │   │ network:    │
│ presentation│   │ presentation│   │ impl        │
└─────────────┘   └─────────────┘   └─────────────┘
      │                 │                 │
      ▼                 ▼                 ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ :feature:   │   │ :feature:   │   │ :core:      │
│ auth:api    │   │ profile:api │   │ network:api │
└─────────────┘   └─────────────┘   └─────────────┘
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
```kotlin
// In feature/auth/presentation module
@Module
@InstallIn(ViewModelComponent::class)
internal interface AuthTEAModule {

    // Reducer - unscoped, new instance per ViewModel
    @Binds
    fun bindAuthReducer(impl: AuthReducer): Reducer<AuthState, AuthCommand, AuthSideEffect>
}

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
                else -> {} // Business logic handled by EffectHandlers
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
        else -> {} // Business logic handled by EffectHandlers
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
├── src/main/kotlin/com/example/app/
│   ├── App.kt                    # Application class
│   ├── MainActivity.kt           # Main activity
│   └── navigation/
│       └── AppNavigation.kt      # Root navigation

core/
├── tea/
│   └── src/main/kotlin/com/example/core/tea/
│       ├── Store.kt                  # TEA Store implementation
│       ├── Reducer.kt                # Reducer interface
│       ├── ReduceResult.kt           # ReduceResult data class
│       ├── EffectHandler.kt          # EffectHandler interface
│       └── BaseStoreViewModel.kt     # Base ViewModel for TEA
├── ui/
│   └── src/main/kotlin/com/example/core/ui/
│       ├── BaseStoreFragment.kt      # Base Fragment for TEA
│       ├── BaseFragment.kt           # Simple base Fragment
│       └── theme/
│           ├── Theme.kt
│           └── Color.kt
├── network/
│   ├── api/
│   │   └── src/main/kotlin/com/example/core/network/
│   │       ├── HttpClient.kt         # Interface
│   │       └── NetworkError.kt       # Sealed class
│   └── impl/
│       └── src/main/kotlin/com/example/core/network/
│           ├── HttpClientImpl.kt     # Implementation
│           └── di/
│               └── NetworkModule.kt  # Hilt module
├── persistence/
│   ├── api/
│   │   └── src/main/kotlin/com/example/core/persistence/
│   │       └── DataStoreClient.kt    # Interface
│   └── impl/
│       └── src/main/kotlin/com/example/core/persistence/
│           ├── DataStoreClientImpl.kt
│           └── di/
│               └── PersistenceModule.kt
└── common/
    └── src/main/kotlin/com/example/core/common/
        └── extensions/
            ├── FlowExtensions.kt
            └── ContextExtensions.kt

feature/
├── auth/
│   ├── api/
│   │   └── src/main/kotlin/com/example/feature/auth/
│   │       ├── AuthRepository.kt
│   │       ├── LoginUseCase.kt
│   │       ├── LogoutUseCase.kt
│   │       └── User.kt
│   ├── impl/
│   │   └── src/main/kotlin/com/example/feature/auth/
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
│       └── src/main/kotlin/com/example/feature/auth/
│           ├── AuthViewModel.kt          # Feature ViewModel
│           ├── AuthState.kt              # UI State
│           ├── AuthCommand.kt            # User actions / feedback commands
│           ├── AuthSideEffect.kt         # Side effects (business + UI)
│           ├── AuthReducer.kt            # Pure reducer function
│           ├── AuthNetworkEffectHandler.kt    # Network side effect handler
│           ├── AuthPersistenceEffectHandler.kt # Persistence side effect handler
│           ├── AuthScreen.kt             # Container composable
│           ├── AuthFragment.kt           # Container fragment (if using Views)
│           └── component/
│               ├── LoginContent.kt       # Passive composable
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
```
com.example.app                         # :app module
com.example.core.network                # :core:network:api and :core:network:impl
com.example.core.persistence            # :core:persistence:api and :core:persistence:impl
com.example.core.ui                     # :core:ui
com.example.feature.auth                # :feature:auth:api, impl, and presentation
com.example.feature.profile             # :feature:profile:api, impl, and presentation
```

---

## Summary of Critical Rules

1. **TEA Pattern**: State is immutable, Commands trigger state changes via Reducer, SideEffects handled by both EffectHandlers (business) and UI (navigation/toast)
2. **Reducer**: Pure function, takes State + Command, returns new State + SideEffects. NO side effects allowed in reducer
3. **EffectHandler**: Handles SideEffects asynchronously, `onCommand` is **suspend** and uses `withContext(Main)` to call `Store.send()`
4. **Store.send()**: MUST be called only from Main thread
5. **SideEffects**: Serve dual purpose - business logic (handled by EffectHandlers) and UI actions (handled by Fragment/Composable)
6. **Use Cases**: Must use `invoke` operator, return `Result<T>` for failable operations
7. **Repositories**: Methods can throw, expose `Flow` for reactive data
8. **Data Sources**: Internal to impl module, never exposed outside
9. **Views**: Passive, only display data and trigger events via lambdas
10. **BaseStoreFragment**: Abstract away Flow collection, feature fragments only implement `render()` and `handleSideEffect()`
11. **Modularization**: api/impl/presentation structure, presentation depends ONLY on api
12. **DI**: Use Hilt `@Binds` for interfaces, singleton for shared state
13. **Navigation**: SideEffect-based, handled in container components
14. **File Structure**: One type per file, file name matches type name
15. **Convention Plugins**: Use appropriate plugin for each module type
