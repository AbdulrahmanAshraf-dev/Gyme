package com.example.gyme.feature.login.repo

import com.example.gyme.feature.members.model.*
import com.example.gyme.feature.staff.model.*
import com.example.gyme.feature.attendance.model.*
import com.example.gyme.feature.finance.model.*
import com.example.gyme.feature.notifications.model.*
import com.example.gyme.feature.more.model.*
import com.example.gyme.feature.home.model.*
import com.example.gyme.util.ApiResult

interface AuthRepository {
    suspend fun signIn(email: String, password: String): ApiResult<User>
    suspend fun signUp(email: String, password: String): ApiResult<User>
}

