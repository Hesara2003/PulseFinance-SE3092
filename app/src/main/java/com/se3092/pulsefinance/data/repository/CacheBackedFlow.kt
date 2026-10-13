package com.se3092.pulsefinance.data.repository

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

/**
 * Offline-first read: the UI always observes the Room cache ([local]), while [sync] mirrors
 * Firestore into Room for exactly as long as someone is collecting. When the collector goes
 * away (screen left, user signed out) the Firestore listener is removed with it.
 */
internal fun <T> cacheBackedFlow(
    tag: String,
    local: Flow<T>,
    sync: suspend () -> Unit
): Flow<T> = channelFlow {
    launch {
        try {
            sync()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Network or permission failure: keep serving whatever the cache holds.
            Log.w(tag, "Remote sync stopped; serving cached data", e)
        }
    }
    local.collect { send(it) }
}
