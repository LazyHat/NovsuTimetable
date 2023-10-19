package com.lazyhat.novsuapp.ui.screens.other

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun LoadingPage(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "LoadingTransition")
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec =
        InfiniteRepeatableSpec(
            tween(2000, 0, LinearEasing),
            RepeatMode.Restart
        ), label = "LoadingRotation"
    )

    Box(modifier) {
        Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .rotate(rotation)
        )
    }
}