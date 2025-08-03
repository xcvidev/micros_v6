package com.xcvi.micros.ui.core.comp


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp


@Composable
fun rememberShakeOffset(trigger: Boolean, onComplete: () -> Unit = {}): Dp {
    val offset = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            offset.snapTo(0f)
            val vibration = listOf(-12f, 12f, -8f, 8f, -4f, 4f, 0f)
            for (v in vibration) {
                offset.animateTo(v, animationSpec = tween(40))
            }
            onComplete()
        }
    }

    return with(LocalDensity.current) { offset.value.toDp() }
}


/*
@Composable
fun ShakingScreen(modifier: Modifier = Modifier) {
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .offset(x = shakeOffset)
    ){
        Button(
            onClick = {
                viewModel.doSomething(
                    onSuccess = {},
                    onFailure = {
                        shakeTrigger = true // trigger shake
                    }
                )
            }
        ){
            Text("Do Something")
        }
    }
}

 */