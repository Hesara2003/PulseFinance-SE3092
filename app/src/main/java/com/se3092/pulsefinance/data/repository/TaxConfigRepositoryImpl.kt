package com.se3092.pulsefinance.data.repository

import android.util.Log
import com.se3092.pulsefinance.data.remote.dto.ApitConfigDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.ApitTaxBand
import com.se3092.pulsefinance.domain.repository.TaxConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

/**
 * APIT bands live in Firestore so a budget revision needs no app release (FR-LK-2).
 * Firestore's disk cache keeps the last-read schedule offline; the bundled bands are used
 * only before the first read, or when the document is missing or invalid.
 */
@Singleton
class TaxConfigRepositoryImpl @Inject constructor(
    private val firestoreDataSource: FirestoreDataSource
) : TaxConfigRepository {

    override fun observeApitBands(): Flow<List<ApitTaxBand>> =
        firestoreDataSource.observeApitConfig()
            .map(::bandsOrDefault)
            .onStart { emit(ApitTaxBand.DEFAULTS) }
            .catch { e ->
                Log.w(TAG, "APIT config listener failed", e)
                emit(ApitTaxBand.DEFAULTS)
            }
            .distinctUntilChanged()

    companion object {
        private const val TAG = "TaxConfigRepo"

        internal fun bandsOrDefault(config: ApitConfigDto?): List<ApitTaxBand> {
            val bands = config?.toDomain()?.sortedBy { it.lowerLimitLkr } ?: return ApitTaxBand.DEFAULTS
            return if (ApitTaxBand.isValidSchedule(bands)) bands else ApitTaxBand.DEFAULTS
        }
    }
}
