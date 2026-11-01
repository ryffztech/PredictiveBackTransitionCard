
package com.ryffztech.predictiveback.ui.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val ANIMATION_DURATION_MS = 380

@Composable
fun SwipeBackContainer(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    previousContent: (@Composable () -> Unit)? = null,
    content: @Composable (animateBack: () -> Unit) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var containerWidth by remember { mutableFloatStateOf(0f) }
    var isFinishing by remember { mutableStateOf(false) }

    val screenColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.onBackground

    fun finishBack() {
        if (!isFinishing) {
            isFinishing = true
            onBack()
        }
    }

    val animateBack: () -> Unit = {
        if (!isFinishing) {
            scope.launch {
                val target = containerWidth.takeIf { it > 0f } ?: 1000f

                offsetX.animateTo(
                    target,
                    tween(ANIMATION_DURATION_MS)
                )

                finishBack()
            }
        }
    }

    // Android system predictive back
    if (enabled) {
        PredictiveBackHandler {
            try {
                isFinishing = false

                it.collect { event ->
                    if (containerWidth > 0f) {
                        offsetX.snapTo(
                            containerWidth * event.progress
                        )
                    }
                }

                val target = containerWidth.takeIf { it > 0f } ?: 1000f

                offsetX.animateTo(
                    target,
                    tween(ANIMATION_DURATION_MS)
                )

                finishBack()

            } catch (e: CancellationException) {
                offsetX.animateTo(
                    0f,
                    tween(ANIMATION_DURATION_MS)
                )
                isFinishing = false
                throw e
            }
        }
    }

    val progress = if (containerWidth > 0f) {
        (offsetX.value / containerWidth).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                containerWidth = it.size.width.toFloat()
            }
    ) {

        // Previous screen: parallax + ZOOM IN
        if (offsetX.value > 0f && previousContent != null) {

            val scale = 0.92f + (0.08f * progress)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX =
                            -containerWidth * 0.30f * (1f - progress)

                        scaleX = scale
                        scaleY = scale
                    }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = screenColor,
                    contentColor = contentColor
                ) {
                    previousContent()
                }
            }
        }

        // Current screen
        val cornerRadius = (28f * progress).dp
        val cardShape = RoundedCornerShape(cornerRadius)

        Surface(
            color = screenColor,
            contentColor = contentColor,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX.value
                    shape = cardShape
                    clip = true
                    shadowElevation = 16.dp.toPx()
                    ambientShadowColor = Color.Black
                    spotShadowColor = Color.Black
                }
                .pointerInput(enabled, containerWidth) {

                    if (!enabled || containerWidth <= 0f) {
                        return@pointerInput
                    }

                    val velocityTracker = VelocityTracker()

                    detectHorizontalDragGestures(
                        onDragStart = {
                            velocityTracker.resetTracking()
                        },

                        onHorizontalDrag = { change, dragAmount ->
                            if (isFinishing) return@detectHorizontalDragGestures

                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position
                            )

                            if (offsetX.value > 0f || dragAmount > 0f) {
                                change.consume()

                                scope.launch {
                                    offsetX.snapTo(
                                        (offsetX.value + dragAmount)
                                            .coerceIn(0f, containerWidth)
                                    )
                                }
                            }
                        },

                        onDragEnd = {
                            if (isFinishing) return@detectHorizontalDragGestures

                            val velocity =
                                velocityTracker.calculateVelocity().x

                            scope.launch {
                                val shouldBack =
                                    offsetX.value > containerWidth * 0.25f ||
                                            velocity > 1000f

                                if (shouldBack) {
                                    offsetX.animateTo(
                                        containerWidth,
                                        tween(ANIMATION_DURATION_MS)
                                    )
                                    finishBack()
                                } else {
                                    offsetX.animateTo(
                                        0f,
                                        tween(ANIMATION_DURATION_MS)
                                    )
                                }
                            }
                        },

                        onDragCancel = {
                            scope.launch {
                                offsetX.animateTo(
                                    0f,
                                    tween(ANIMATION_DURATION_MS)
                                )
                            }
                        }
                    )
                }
        ) {
            content(animateBack)
        }
    }
}
