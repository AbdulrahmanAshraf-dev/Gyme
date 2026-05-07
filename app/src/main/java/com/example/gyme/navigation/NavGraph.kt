package com.example.gyme.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.feature.login.LoginScreen
import com.example.gyme.feature.onboarding.OnboardingScreen
import com.example.gyme.feature.members.add.AddMemberScreen
import com.example.gyme.feature.members.add.AddMemberViewModel
import com.example.gyme.feature.members.update.UpdateMemberScreen
import com.example.gyme.feature.members.update.UpdateMemberViewModel
import com.example.gyme.domain.usecase.*
import com.example.gyme.data.repository.*
import com.example.gyme.feature.attendance.AttendanceScreen
import com.example.gyme.feature.attendance.AttendanceViewModel
import com.example.gyme.feature.notifications.NotificationsScreen
import com.example.gyme.feature.notifications.NotificationsViewModel
import com.example.gyme.feature.more.MoreScreen
import com.example.gyme.feature.staff.StaffScreen
import com.example.gyme.feature.staff.StaffViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
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
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
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
            val repository = MemberRepositoryImpl()
            val addMemberUseCase = AddMemberUseCase(repository)
            val getPlansUseCase = GetMembershipPlansUseCase(repository)
            val viewModel = AddMemberViewModel(addMemberUseCase, getPlansUseCase)
            
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
            val repository = MemberRepositoryImpl()
            
            val viewModel = UpdateMemberViewModel(
                memberId = memberId,
                getMemberDetails = GetMemberDetailsUseCase(repository),
                getMemberPaymentHistory = GetMemberPaymentHistoryUseCase(repository),
                getMembershipPlans = GetMembershipPlansUseCase(repository),
                updateMember = UpdateMemberUseCase(repository)
            )
            
            UpdateMemberScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Attendance.route) {
            val repository = AttendanceRepositoryImpl()
            val memberRepo = MembersRepositoryImpl()
            val checkInUseCase = CheckInMemberUseCase(memberRepo, repository)
            val viewModel = AttendanceViewModel(checkInUseCase)

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
            val repository = NotificationsRepositoryImpl()
            val viewModel = NotificationsViewModel(repository)

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
    }
}
