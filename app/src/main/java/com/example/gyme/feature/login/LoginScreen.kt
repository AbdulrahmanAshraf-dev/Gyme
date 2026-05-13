package com.example.gyme.feature.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gyme.theme.GymeButtonBg
import com.example.gyme.theme.GymeButtonText
import com.example.gyme.theme.GymeIconTint
import com.example.gyme.util.SessionManager

private val FieldBg          = Color(0xFFF3F4F6)
private val LabelColor       = Color(0xFF9CA3AF)
private val IconColor        = Color(0xFFADB5BD)
private val HeadingColor     = Color(0xFF111827)
private val SubtitleColor    = Color(0xFF6B7280)
private val ForgotColor      = Color(0xFF111827)
private val ScreenBackground = Color(0xFFFFFFFF)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .statusBarsPadding()
        ) {
            Spacer(Modifier.height(48.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Logo",
                    tint = GymeIconTint,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Gyme",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = HeadingColor
                )
            }

            Spacer(Modifier.height(36.dp))


            Text(
                text = "Welcome back",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HeadingColor,
                lineHeight = 38.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sign in to access your facility dashboard.",
                fontSize = 15.sp,
                color = SubtitleColor,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            FieldLabel(text = "EMAIL ADDRESS")
            Spacer(Modifier.height(8.dp))
            GymeTextField(
                value = viewModel.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "owner@gymmanager.com",
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = IconColor,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FieldLabel(text = "PASSWORD")
                Text(
                    text = "Forgot Password?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForgotColor,
                    modifier = Modifier.clickable { /* TODO: forgot password */ }
                )
            }
            Spacer(Modifier.height(8.dp))
            GymeTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "••••••••",
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = IconColor,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = if (viewModel.isPasswordVisible)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = IconColor,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { viewModel.togglePasswordVisibility() }
                    )
                },
                visualTransformation = if (viewModel.isPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.signIn { user ->
                            SessionManager.saveSession(context, user)
                            onLoginSuccess()
                        }
                    }
                )
            )

            AnimatedVisibility(
                visible = viewModel.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                viewModel.errorMessage?.let { msg ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { 
                    viewModel.signIn { user ->
                        SessionManager.saveSession(context, user)
                        onLoginSuccess()
                    }
                },
                enabled = !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymeButtonBg,
                    disabledContainerColor = GymeButtonBg.copy(alpha = 0.6f)
                )
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = GymeButtonText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SIGN IN",
                        color = GymeButtonText,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Email, // arrow substitution — see note
                        contentDescription = null,
                        tint = GymeButtonText,
                        modifier = Modifier.size(0.dp) // hidden; replaced below
                    )
                    Text(
                        text = " →",
                        color = GymeButtonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = LabelColor,
        letterSpacing = 1.sp
    )
}

@Composable
private fun GymeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = LabelColor,
                fontSize = 14.sp
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedIndicatorColor   = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor  = Color.Transparent,
            focusedTextColor        = HeadingColor,
            unfocusedTextColor      = HeadingColor,
            cursorColor             = GymeIconTint
        )
    )
}
