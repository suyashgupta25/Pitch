package com.axioo.feed.ui.feed.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun rememberPlayerPool(capacity: Int = PitchPlayerPool.DEFAULT_CAPACITY): PitchPlayerPool {
    val context = LocalContext.current
    val pool = remember(capacity) { PitchPlayerPool.create(context, capacity) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(pool, lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> pool.pauseAll()
                    Lifecycle.Event.ON_STOP -> pool.release()
                    else -> Unit
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            pool.release()
        }
    }
    return pool
}
