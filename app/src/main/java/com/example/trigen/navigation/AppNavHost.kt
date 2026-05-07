package com.example.trigen.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trigen.screens.academy.AcademyScreen
import com.example.trigen.screens.academy.BadgeScreen
import com.example.trigen.screens.academy.LessonScreen
import com.example.trigen.screens.academy.ModuleDetailScreen
import com.example.trigen.screens.academy.QuizScreen
import com.example.trigen.screens.aftermath.AftermathScreen
import com.example.trigen.screens.about.AboutScreen
import com.example.trigen.screens.auth.AuthViewModel
import com.example.trigen.screens.auth.LoginScreen
import com.example.trigen.screens.auth.SignUpScreen
import com.example.trigen.screens.cpr.CprScreen
import com.example.trigen.screens.drsabcd.DrsabcdScreen
import com.example.trigen.screens.drsabcd_alternatives.CprAlternativeScreen
import com.example.trigen.screens.drsabcd_alternatives.CprWithoutAedScreen
import com.example.trigen.screens.drsabcd_alternatives.SendHelpAlternative
import com.example.trigen.screens.drsabcd_alternatives.UnsafeSceneScreen
import com.example.trigen.screens.home.HomeScreen
import com.example.trigen.screens.incidents.IncidentViewModel
import com.example.trigen.screens.incidents.IncidentsScreen
import com.example.trigen.screens.incidents.ViewIncidentScreen
import com.example.trigen.screens.profile.ProfileScreen
import com.example.trigen.screens.profile.ProfileViewModel
import com.example.trigen.screens.protocol.ProtocolScreen
import com.example.trigen.screens.privacy.PrivacyScreen
import com.example.trigen.screens.scan.ScanScreen
import com.example.trigen.screens.secondary.SecondarySurveyScreen
import com.example.trigen.screens.splash.SplashScreen
import com.example.trigen.screens.support.SupportScreen
import com.example.trigen.screens.secondary.SecondarySurveyViewModel
import com.example.trigen.ui.theme.TriGenTheme

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH
) {
    val incidentViewModel: IncidentViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()
    
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState by profileViewModel.uiState.collectAsState()
    
    val darkTheme = when (profileState.isDarkMode) {
        true -> true
        false -> false
        null -> isSystemInDarkTheme()
    }

    TriGenTheme(darkTheme = darkTheme) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onSplashComplete = {
                        val destination = if (authState.user != null) Routes.HOME else Routes.LOGIN
                        navController.navigate(destination) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SIGN_UP) { inclusive = true }
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onStartScan = { navController.navigate(Routes.SCAN) },
                    onOpenAcademy = { 
                        if (navController.currentDestination?.route != Routes.ACADEMY) {
                            navController.navigate(Routes.ACADEMY)
                        }
                    },
                    onOpenCpr = { navController.navigate(Routes.CPR) },
                    onOpenProtocol = { injuryType ->
                        navController.navigate(Routes.protocol(injuryType))
                    },
                    onStartEmergency = { navController.navigate(Routes.DRSABCD) },
                    onOpenAftermath = {
                        navController.navigate(Routes.aftermath("new"))
                    },
                    onOpenSecondary = { navController.navigate(Routes.SECONDARY_SURVEY) },
                    onOpenIncidents = { navController.navigate(Routes.INCIDENTS) },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onOpenProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToAbout = { navController.navigate(Routes.ABOUT) },
                    onNavigateToSupport = { navController.navigate(Routes.SUPPORT) },
                    onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY) }
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SUPPORT) {
                SupportScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                    )
            }

            composable(Routes.PRIVACY) {
                PrivacyScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SCAN) {
                ScanScreen(
                    onInjuryDetected = { injuryType ->
                        navController.navigate(Routes.protocol(injuryType))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.PROTOCOL,
                arguments = listOf(navArgument("injuryType") { type = NavType.StringType })
            ) { backStackEntry ->
                val injuryType = backStackEntry.arguments?.getString("injuryType") ?: ""
                ProtocolScreen(
                    injuryType = injuryType,
                    onBack = { navController.popBackStack() },
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                )
            }

            composable(Routes.CPR) {
                CprScreen(onBack = { navController.popBackStack() },
                    onNavigateToHome = { navController.navigate(Routes.HOME) })

            }

            composable(Routes.DRSABCD) {
                DrsabcdScreen(
                    onNavigateToCpr = { navController.navigate(Routes.CPR) },
                    onNavigateToAedUnavailable = { navController.navigate(Routes.AED_UNAVAILABLE) },
                    onNavigateToHome = { navController.navigate(Routes.HOME) },
                    onBack = { navController.popBackStack() },
                    onNavigateToUnsafeScene = { navController.navigate(Routes.UNSAFE_SCENE) },
                    onNavigateToSecondarySurvay = { navController.navigate(Routes.SECONDARY_SURVEY) }
                )
            }

            composable(Routes.SECONDARY_SURVEY) {
                SecondarySurveyScreen(
                    onNavigateToProtocol = { injuryType ->
                        navController.navigate(Routes.protocol(injuryType))
                    },
                    onNavigateToHome = { navController.navigate(Routes.HOME) },
                    onNavigateToAftermath = {
                        navController.navigate(Routes.aftermath("new"))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.AFTERMATH,
                arguments = listOf(navArgument("sessionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: "new"
                
                LaunchedEffect(sessionId) {
                    if (sessionId == "new") {
                        incidentViewModel.startNewIncident()
                    } else {
                        incidentViewModel.selectIncident(sessionId)
                    }
                }

                AftermathScreen(
                    onBack = { navController.popBackStack() },
                    onViewIncidents = { navController.navigate(Routes.INCIDENTS) },
                    viewModel = incidentViewModel,
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                )
            }

            composable(Routes.ACADEMY) {
                AcademyScreen(
                    onBack = { navController.popBackStack() },
                    onModuleClick = { moduleId -> 
                        navController.navigate(Routes.moduleDetail(moduleId))
                    }
                )
            }

            composable(
                route = Routes.MODULE_DETAIL,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                ModuleDetailScreen(
                    moduleId = moduleId,
                    onBack = { navController.popBackStack() },
                    onLessonClick = { lessonId ->
                        navController.navigate(Routes.lesson(lessonId))
                    },
                    onQuizClick = { id ->
                        navController.navigate(Routes.quiz(id))
                    }
                )
            }

            composable(
                route = Routes.LESSON,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                LessonScreen(
                    lessonId = lessonId,
                    onBack = { navController.popBackStack() },
                    onLessonCompleted = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.QUIZ,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                QuizScreen(
                    moduleId = moduleId,
                    onBack = { navController.popBackStack() },
                    onViewBadge = {
                        navController.navigate(Routes.badge(moduleId)) {
                            popUpTo(Routes.QUIZ) { inclusive = true }
                        }
                    },
                    onQuizFinished = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Routes.BADGE,
                arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
                BadgeScreen(
                    moduleId = moduleId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.INCIDENTS) {
                IncidentsScreen(
                    viewModel = incidentViewModel,
                    onNavigateToView = { id -> navController.navigate(Routes.viewIncident(id)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.VIEW_INCIDENT,
                arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val incidentId = backStackEntry.arguments?.getString("incidentId") ?: ""
                
                LaunchedEffect(incidentId) {
                    incidentViewModel.selectIncident(incidentId)
                }

                ViewIncidentScreen(
                    viewModel = incidentViewModel,
                    onEdit = { id -> navController.navigate(Routes.aftermath(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SEND_HELP) {
                SendHelpAlternative(
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                )
            }

            composable(Routes.UNSAFE_SCENE) {
                UnsafeSceneScreen(
                    onCallEmergency = { navController.navigate(Routes.SEND_HELP) }
                )
            }

            composable(Routes.AED_UNAVAILABLE) {
                CprWithoutAedScreen(
                    onCallEmergency = { navController.navigate(Routes.SEND_HELP) },
                    onNavigateToCprScreen = { navController.navigate(Routes.CPR_ALTERNATIVE) }
                )
            }
            composable(Routes.CPR_ALTERNATIVE) {
                CprAlternativeScreen(
                    onNavigateToCpr = { navController.navigate(Routes.CPR) }
                )
            }
        }
    }
}
