package com.se3092.pulsefinance.ui.common

import com.se3092.pulsefinance.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Re-subscribes [block] whenever the signed-in user changes and emits [signedOut] when there
 * is no user — so a ViewModel can never read or write data under a made-up user id.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> AuthRepository.whenSignedIn(signedOut: T, block: (userId: String) -> Flow<T>): Flow<T> =
    observeAuthState()
        .distinctUntilChanged()
        .flatMapLatest { userId -> if (userId == null) flowOf(signedOut) else block(userId) }
