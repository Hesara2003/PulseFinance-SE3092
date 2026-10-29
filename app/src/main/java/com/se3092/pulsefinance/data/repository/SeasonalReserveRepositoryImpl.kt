package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SeasonalReserve
import com.se3092.pulsefinance.domain.repository.SeasonalReserveRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * At most a handful of reserves a year, only read by the dashboard, so like subscription
 * reviews they rely on Firestore's own offline cache rather than a Room table.
 */
@Singleton
class SeasonalReserveRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : SeasonalReserveRepository {

    override fun getReserves(userId: String): Flow<Map<String, SeasonalReserve>> =
        firestoreDataSource.observeSeasonalReserves(userId)
            .onStart { emit(emptyMap()) }
            .catch { e ->
                Log.w(TAG, "Seasonal reserve listener failed", e)
                emit(emptyMap())
            }

    override fun setAside(userId: String, reserve: SeasonalReserve) {
        firestoreDataSource.saveSeasonalReserve(userId, reserve)
    }

    override fun release(userId: String, peakKey: String) {
        firestoreDataSource.deleteSeasonalReserve(userId, peakKey)
    }

    private companion object {
        const val TAG = "SeasonalReserveRepo"
    }
}
