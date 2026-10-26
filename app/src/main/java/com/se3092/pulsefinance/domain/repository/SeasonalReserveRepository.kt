package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.SeasonalReserve
import kotlinx.coroutines.flow.Flow

interface SeasonalReserveRepository {
    /** Reserves keyed by [SeasonalReserve.peakKey]. */
    fun getReserves(userId: String): Flow<Map<String, SeasonalReserve>>
    fun setAside(userId: String, reserve: SeasonalReserve)
    fun release(userId: String, peakKey: String)
}
