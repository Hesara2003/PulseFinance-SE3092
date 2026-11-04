package com.se3092.pulsefinance.domain.repository

import com.se3092.pulsefinance.domain.model.ApitTaxBand
import kotlinx.coroutines.flow.Flow

interface TaxConfigRepository {
    /** Current APIT schedule; always emits a usable schedule, falling back to the bundled one. */
    fun observeApitBands(): Flow<List<ApitTaxBand>>
}
