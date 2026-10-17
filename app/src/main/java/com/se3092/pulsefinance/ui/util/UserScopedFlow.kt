package com.se3092.pulsefinance.ui.util

import com.se3092.pulsefinance.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Re-subscribes [block] whenever the signed-in user changes.
 *
 * ViewModels used to capture `currentUserId ?: "guest_kavindu"` once at construction, so a
 * sign-out/sign-in in the same process kept showing the previous user's data. This follows
 * the auth state instead and emits [whenSignedOut] while nobody is signed in.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> AuthRepository.userScopedFlow(
    whenSignedOut: T,
    block: (userId: String) -> Flow<T>
): Flow<T> = observeAuthState().flatMapLatest { uid ->
    if (uid == null) flowOf(whenSignedOut) else block(uid)
}
