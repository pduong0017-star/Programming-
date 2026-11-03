package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.repository.VibeCutRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOut
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.CapCutHomeScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DataUiState
import com.example.ui.viewmodel.VibeCutViewModel
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser
    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        VibeCutMainApp(
            currentUserId = user.uid,
            currentUserName = user.displayName ?: user.email?.substringBefore("@") ?: "Creator",
            currentUserEmail = user.email.orEmpty()
        )
    }
}

// 4 Streamlined CapCut Main Navigation Tabs
private enum class NavDestination(
    val index: Int,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    CAPCUT_HOME(0, "Chỉnh sửa", Icons.Filled.ContentCut, Icons.Outlined.ContentCut, "nav_capcut_home"),
    TEMPLATES(1, "Mẫu AI", Icons.Filled.ViewCarousel, Icons.Outlined.ViewCarousel, "nav_templates"),
    FEED(2, "Xu hướng", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_feed"),
    PROFILE(3, "Hồ sơ", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

@Composable
fun VibeCutMainApp(
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val viewModel: VibeCutViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                VibeCutViewModel(
                    repository = VibeCutRepository(db),
                    currentUserId = currentUserId,
                    currentUserName = currentUserName
                )
            }
        }
    )

    val projectsState by viewModel.userProjectsState.collectAsStateWithLifecycle()
    val templatesState by viewModel.userTemplatesState.collectAsStateWithLifecycle()
    val editorState by viewModel.editorState.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val likedPostIds by viewModel.likedPostIds.collectAsStateWithLifecycle()

    val userProjects = (projectsState as? DataUiState.Success)?.data.orEmpty()
    val userTemplates = (templatesState as? DataUiState.Success)?.data.orEmpty()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    var isStudioOpen by rememberSaveable { mutableStateOf(false) }
    var studioInitialDrawer by rememberSaveable { mutableStateOf<Int?>(null) }

    val openStudioWithDrawer: (Int?) -> Unit = { drawerIdx ->
        studioInitialDrawer = drawerIdx
        isStudioOpen = true
    }

    BackHandler(enabled = isStudioOpen || currentTab != 0) {
        if (isStudioOpen) {
            isStudioOpen = false
        } else {
            currentTab = 0
        }
    }

    LaunchedEffect(statusBanner) {
        if (statusBanner != null) {
            delay(3000L)
            viewModel.dismissBanner()
        }
    }

    val density = LocalDensity.current
    var isWideScreen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .onSizeChanged { size ->
                val widthDp = with(density) { size.width.toDp() }
                isWideScreen = widthDp >= 600.dp
            }
    ) {

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = ObsidianBg,
            bottomBar = {
                // Hide Bottom NavigationBar when Full-Screen Studio Editor is open
                if (!isWideScreen && !isStudioOpen) {
                    NavigationBar(
                        containerColor = StudioSurface,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        NavDestination.entries.forEach { dest ->
                            val selected = currentTab == dest.index
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = dest.index },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF04201B),
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isStudioOpen) {
                    // Dedicated Full-Screen CapCut Studio Workspace
                    AiStudioScreen(
                        editorState = editorState,
                        aiState = aiState,
                        initialBottomTab = studioInitialDrawer,
                        onCloseStudio = { isStudioOpen = false },
                        onUpdateEditor = { viewModel.updateEditorField(it) },
                        onSelectClip = { idx -> viewModel.selectClip(idx) },
                        onSplitClip = { viewModel.splitClipAtPlayhead() },
                        onTrimClip = { delta -> viewModel.trimSelectedClip(delta) },
                        onDuplicateClip = { viewModel.duplicateSelectedClip() },
                        onDeleteClip = { viewModel.deleteSelectedClip() },
                        onUpdateSelectedClip = { viewModel.updateSelectedClip(it) },
                        onToggleKeyframe = { pos -> viewModel.toggleKeyframeAtPlayhead(pos) },
                        onToggleSticker = { emoji -> viewModel.toggleSticker(emoji) },
                        onEnhance4k = { viewModel.applyAi4kEnhance() },
                        onUpdateCaptionCue = { idx, cue -> viewModel.updateCaptionCue(idx, cue) },
                        onAddCaptionCue = { viewModel.addCaptionCue() },
                        onGenerateAiEdit = { prompt -> viewModel.generateAiVideoEdit(prompt) },
                        onPublishProject = { viewModel.saveCurrentProjectToCloud(true) },
                        onSaveAsTemplate = { viewModel.saveCurrentAsAiTemplate() }
                    )
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (isWideScreen) {
                            NavigationRail(
                                containerColor = StudioSurface,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                NavDestination.entries.forEach { dest ->
                                    val selected = currentTab == dest.index
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { currentTab = dest.index },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = dest.label
                                            )
                                        },
                                        label = { Text(dest.label) },
                                        colors = NavigationRailItemDefaults.colors(
                                            selectedIconColor = Color(0xFF04201B),
                                            selectedTextColor = ElectricCyan,
                                            indicatorColor = ElectricCyan,
                                            unselectedIconColor = TextSecondary,
                                            unselectedTextColor = TextSecondary
                                        ),
                                        modifier = Modifier.testTag(dest.testTag)
                                    )
                                }
                            }
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                0 -> CapCutHomeScreen(
                                    userProjects = userProjects,
                                    onStartNewProject = {
                                        viewModel.startFreshProject()
                                        openStudioWithDrawer(null)
                                    },
                                    onOpenAutoCutAi = {
                                        openStudioWithDrawer(5)
                                    },
                                    onOpenAutoCaptions = {
                                        openStudioWithDrawer(3)
                                    },
                                    onOpenTeleprompter = {
                                        viewModel.updateEditorField { it.copy(teleprompterActive = true) }
                                        openStudioWithDrawer(3)
                                        viewModel.showToast("📜 Đã bật Máy nhắc chữ trên màn hình Preview!")
                                    },
                                    onOpen4kEnhancer = {
                                        viewModel.applyAi4kEnhance()
                                        openStudioWithDrawer(4)
                                    },
                                    onOpenAiCutout = {
                                        viewModel.updateEditorField { it.copy(aiCutoutEnabled = true) }
                                        openStudioWithDrawer(0)
                                        viewModel.showToast("🧍 Đã bật Xóa Nền AI & Viền Sáng Chủ Thể!")
                                    },
                                    onOpenScriptGenerator = {
                                        currentTab = 1
                                    },
                                    onOpenExistingProject = { proj ->
                                        viewModel.loadProjectIntoStudio(proj)
                                        openStudioWithDrawer(null)
                                    },
                                    onDeleteProject = { id -> viewModel.deleteUserProject(id) }
                                )

                                1 -> TemplatesScreen(
                                    userTemplates = userTemplates,
                                    aiState = aiState,
                                    onStreamScript = { topic -> viewModel.streamViralScriptIdea(topic) },
                                    onApplyTemplate = { tpl ->
                                        viewModel.applyTemplateToStudio(tpl)
                                        openStudioWithDrawer(null)
                                    },
                                    onDeleteTemplate = { id -> viewModel.deleteUserTemplate(id) }
                                )

                                2 -> FeedScreen(
                                    userProjects = userProjects,
                                    likedPostIds = likedPostIds,
                                    onToggleLike = { viewModel.toggleLikeOnProject(it) },
                                    onRemixInStudio = { post ->
                                        viewModel.loadProjectIntoStudio(post)
                                        openStudioWithDrawer(null)
                                    },
                                    onNavigateToStudio = { openStudioWithDrawer(null) }
                                )

                                3 -> ProfileScreen(
                                    userName = currentUserName,
                                    userEmail = currentUserEmail,
                                    userProjects = userProjects,
                                    onEditProject = { proj ->
                                        viewModel.loadProjectIntoStudio(proj)
                                        openStudioWithDrawer(null)
                                    },
                                    onDeleteProject = { id -> viewModel.deleteUserProject(id) },
                                    onOpenStudio = { openStudioWithDrawer(null) },
                                    onSignOut = {
                                        signOut(
                                            context = context,
                                            credentialManager = credentialManager,
                                            onSignOutComplete = {},
                                            scope = scope
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = statusBanner != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                ) {
                    Surface(
                        color = StudioSurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp))
                            .clickable { viewModel.dismissBanner() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = statusBanner.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
