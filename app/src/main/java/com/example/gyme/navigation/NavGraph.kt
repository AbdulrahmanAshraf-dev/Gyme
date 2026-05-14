package com.example.gyme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.feature.login.LoginScreen
import com.example.gyme.feature.onboarding.OnboardingScreen
import com.example.gyme.feature.members.add.AddMemberScreen
import com.example.gyme.feature.members.add.AddMemberViewModel
import com.example.gyme.feature.members.update.UpdateMemberScreen
import com.example.gyme.feature.members.update.UpdateMemberViewModel
import com.example.gyme.feature.attendance.view.AttendanceScreen
import com.example.gyme.feature.attendance.viewmodel.AttendanceViewModel
import com.example.gyme.feature.notifications.NotificationsViewModel
import com.example.gyme.feature.more.MoreScreen
import com.example.gyme.feature.staff.StaffScreen
import com.example.gyme.feature.staff.StaffViewModel
import com.example.gyme.feature.plans.PlansScreen
import com.example.gyme.feature.plans.PlansViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            com.example.gyme.feature.home.HomeScreen(
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToMore = {
                    navController.navigate(Screen.More.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance.route)
                },
                onNavigateToAddMember = {
                    navController.navigate(Screen.AddMember.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                }
            )
        }

        composable(Screen.Members.route) {
            com.example.gyme.feature.members.MembersScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToMore = {
                    navController.navigate(Screen.More.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance.route)
                },
                onNavigateToAddMember = {
                    navController.navigate(Screen.AddMember.route)
                },
                onNavigateToUpdateMember = { memberId ->
                    navController.navigate(Screen.UpdateMember.createRoute(memberId))
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                }
            )
        }

        composable(Screen.Finance.route) {
            com.example.gyme.feature.finance.FinanceScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToMore = {
                    navController.navigate(Screen.More.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.More.route) {
            MoreScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToStaff = {
                    navController.navigate(Screen.StaffManagement.route)
                },
                onNavigateToPlans = {
                    navController.navigate(Screen.Plans.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StaffManagement.route) {
            val viewModel: StaffViewModel = viewModel()

            StaffScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.AddMember.route) {
            val viewModel: AddMemberViewModel = viewModel()
            AddMemberScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.UpdateMember.route,
            arguments = listOf(navArgument("memberId") { type = NavType.StringType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId") ?: ""
            // We use a custom factory or just instantiate it here simply
            val viewModel: UpdateMemberViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return UpdateMemberViewModel(memberId) as T
                    }
                }
            )
            
            UpdateMemberScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Attendance.route) {
            val viewModel: AttendanceViewModel = viewModel()

            AttendanceScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToMore = {
                    navController.navigate(Screen.More.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Notifications.route) {
            val viewModel: NotificationsViewModel = viewModel()

            com.example.gyme.feature.notifications.NotificationsScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToMembers = {
                    navController.navigate(Screen.Members.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToAttendance = {
                    navController.navigate(Screen.Attendance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToFinance = {
                    navController.navigate(Screen.Finance.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToMore = {
                    navController.navigate(Screen.More.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Plans.route) {
            val viewModel: PlansViewModel = viewModel()
            PlansScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
