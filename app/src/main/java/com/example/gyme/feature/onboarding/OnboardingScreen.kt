package com.example.gyme.feature.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gyme.R
import com.example.gyme.theme.GymeBackground
import com.example.gyme.theme.GymeButtonBg
import com.example.gyme.theme.GymeButtonText
import com.example.gyme.theme.GymeCardBg
import com.example.gyme.theme.GymeIconBg
import com.example.gyme.theme.GymeIconTint
import com.example.gyme.theme.GymeTextPrimary
import com.example.gyme.theme.GymeTextSecondary
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val cardOffsetY = remember { Animatable(120f) }
    val logoAlpha   = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { logoAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing)) }
        launch { cardOffsetY.animateTo(0f, tween(700, delayMillis = 200, easing = FastOutSlowInEasing)) }
    }

    Box(modifier = Modifier.fillMaxSize().background(GymeBackground)) {

        Image(
            painter = painterResource(id = R.drawable.gym_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .graphicsLayer(alpha = logoAlpha.value)
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = stringResource(R.string.logo),
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.63f)
                .graphicsLayer(translationY = cardOffsetY.value)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(GymeCardBg)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_title),
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = GymeTextPrimary,
                lineHeight = 44.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.onboarding_subtitle),
                fontSize = 15.sp,
                color = GymeTextSecondary,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(32.dp))

            FeatureRow(
                icon  = Icons.Default.GridView,
                title = stringResource(R.string.feature_dashboard_title),
                desc  = stringResource(R.string.feature_dashboard_desc)
            )
            Spacer(Modifier.height(24.dp))
            FeatureRow(
                icon  = Icons.Default.QrCodeScanner,
                title = stringResource(R.string.feature_access_title),
                desc  = stringResource(R.string.feature_access_desc)
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick    = onGetStarted,
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GymeButtonBg)
            ) {
                Text(
                    text          = stringResource(R.string.get_started_action),
                    color         = GymeButtonText,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize      = 13.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(GymeIconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector    = icon,
                contentDescription = null,
                tint           = GymeIconTint,
                modifier       = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text       = title,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
                color      = GymeTextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = desc,
                fontSize   = 14.sp,
                color      = GymeTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
