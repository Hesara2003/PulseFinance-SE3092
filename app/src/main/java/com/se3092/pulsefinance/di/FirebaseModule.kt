package com.se3092.pulsefinance.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /** 100 MB is plenty for a single user's ledger and keeps eviction out of the way. */
    private const val FIRESTORE_CACHE_SIZE_BYTES = 100L * 1024 * 1024

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        // Disk-backed cache: queued writes survive process death and listeners can serve
        // cached snapshots offline. Replaces the deprecated setPersistenceEnabled(true).
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(FIRESTORE_CACHE_SIZE_BYTES)
                    .build()
            )
            .build()
        firestore.firestoreSettings = settings
        return firestore
    }
}
