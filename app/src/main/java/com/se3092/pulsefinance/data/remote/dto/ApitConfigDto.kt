package com.se3092.pulsefinance.data.remote.dto

import com.se3092.pulsefinance.domain.model.ApitTaxBand

/** Firestore `config/apitBands`: the APIT schedule, editable without an app release (FR-LK-2). */
data class ApitConfigDto(
    val effectiveFrom: String = "",
    val bands: List<ApitBandDto> = emptyList()
) {
    fun toDomain(): List<ApitTaxBand> = bands.map { it.toDomain() }
}

/** One band; a null [upperLkr] marks the open-ended top band. */
data class ApitBandDto(
    val lowerLkr: Double = 0.0,
    val upperLkr: Double? = null,
    val rate: Double = 0.0
) {
    fun toDomain(): ApitTaxBand = ApitTaxBand(
        lowerLimitLkr = lowerLkr,
        upperLimitLkr = upperLkr ?: Double.MAX_VALUE,
        taxRate = rate
    )
}
