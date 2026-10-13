package com.se3092.pulsefinance.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pushes local writes to Firestore without blocking the caller.
 *
 * A Firestore write task only completes once the server acknowledges it, so awaiting it
 * offline would freeze the calling UI flow. Repositories write Room first; Firestore queues
 * the mutation in its disk cache and replays it when connectivity returns.
 */
@Singleton
class RemoteWriter @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun enqueue(label: String, block: suspend () -> Unit) {
        scope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.w(TAG, "Remote write '$label' failed; Room keeps the local copy", e)
            }
        }
    }

    private companion object {
        const val TAG = "RemoteWriter"
    }
}
