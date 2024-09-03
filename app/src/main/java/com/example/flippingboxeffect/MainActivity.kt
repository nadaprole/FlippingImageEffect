package com.example.flippingboxeffect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.flippingboxeffect.ui.theme.FlippingImageEffectTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlippingImageEffectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val imageList = listOf(
                        R.drawable.ice_image,
                        R.drawable.mountains,
                        R.drawable.road,
                        R.drawable.sea
                    )
                    FlippingImageList(images = imageList, innerPadding, modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun FlippingImageList(
    images: List<Int>,
    innerPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier
) {
    var offsetY by remember { mutableStateOf(0f) }
    var currentImageIndex by remember { mutableStateOf(0) }
    val animatable = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) } // State to control animation

    LaunchedEffect(currentImageIndex) {
        if (isAnimating) {
            animatable.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 600)
            )
            animatable.snapTo(0f) // Reset animation state after flip
            isAnimating = false // Reset the animation control state
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    offsetY += dragAmount
                    if (abs(offsetY) > 200) { // Threshold for flip
                        offsetY = 0f
                        currentImageIndex = (currentImageIndex + 1) % images.size

                        // Trigger animation after user interaction
                        isAnimating = true
                        coroutineScope.launch {
                            animatable.snapTo(0f)
                        }
                    }
                }
            }
    ) {
        ImageCard(image = images[currentImageIndex], flipAnim = animatable.value)
    }
}

@Composable
fun ImageCard(image: Int, flipAnim: Float) {
    val imageModifier = Modifier
        .fillMaxSize()
        .graphicsLayer(
            rotationX = flipAnim,
            transformOrigin = TransformOrigin.Center,
            cameraDistance = 8f // Adjust camera distance to get a better flip effect
        )

    Image(
        painter = painterResource(id = image),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = imageModifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFlippingImageList() {
    val images = listOf(
        R.drawable.sea,
        R.drawable.ice_image,
        R.drawable.mountains,
        R.drawable.road
    )
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            FlippingImageList(images = images, modifier = Modifier)
        }
    }
}