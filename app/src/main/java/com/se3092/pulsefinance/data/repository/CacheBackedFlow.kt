package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.firebase.RemoteSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Room-first reactive stream with background Firestore synchronisation.
 *
 * 1. Emits whatever Room already holds immediately (offline / cold start).
 * 2. While collected, listens to [remote] and writes each server snapshot into Room via
 *    [persist]; Room then re-emits, so the UI only ever renders local data.
 * 3. The listener is tied to the collector, so it stops when the screen stops observing
 *    instead of leaking one listener per call.
 *
 * Empty snapshots served from Firestore's offline cache are skipped: they usually mean
 * "not synced yet", and mirroring them would wipe valid Room rows.
 */
fun <Local, Remote> cacheBackedFlow(
    label: String,
    local: Flow<Local>,
    remote: Flow<RemoteSnapshot<Remote>>,
    persist: suspend (List<Remote>) -> Unit
): Flow<Local> = channelFlow {
    launch {
        remote
            .catch { e -> Log.w(TAG, "$label remote sync stopped; serving Room cache", e) }
            .collect { snapshot ->
                if (snapshot.isFromCache && snapshot.items.isEmpty()) return@collect
                try {
                    persist(snapshot.items)
                } catch (e: Exception) {
                    Log.w(TAG, "$label failed to persist remote snapshot", e)
                }
            }
    }
    local.distinctUntilChanged().collect { send(it) }
}

private const val TAG = "CacheBackedFlow"
