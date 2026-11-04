package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.remote.dto.ApitBandDto
import com.se3092.pulsefinance.data.remote.dto.ApitConfigDto
import com.se3092.pulsefinance.domain.model.ApitTaxBand
import org.junit.Assert.assertEquals
import org.junit.Test

class TaxConfigRepositoryImplTest {

    @Test
    fun `missing config falls back to bundled bands`() {
        assertEquals(ApitTaxBand.DEFAULTS, TaxConfigRepositoryImpl.bandsOrDefault(null))
        assertEquals(ApitTaxBand.DEFAULTS, TaxConfigRepositoryImpl.bandsOrDefault(ApitConfigDto()))
    }

    @Test
    fun `valid config is used, sorted, with an open-ended top band`() {
        val config = ApitConfigDto(
            effectiveFrom = "2026-04-01",
            bands = listOf(
                ApitBandDto(lowerLkr = 150_000.0, upperLkr = null, rate = 0.10),
                ApitBandDto(lowerLkr = 0.0, upperLkr = 150_000.0, rate = 0.0)
            )
        )

        val bands = TaxConfigRepositoryImpl.bandsOrDefault(config)

        assertEquals(
            listOf(
                ApitTaxBand(0.0, 150_000.0, 0.0),
                ApitTaxBand(150_000.0, Double.MAX_VALUE, 0.10)
            ),
            bands
        )
    }

    @Test
    fun `invalid config falls back to bundled bands`() {
        val withGap = ApitConfigDto(
            bands = listOf(
                ApitBandDto(lowerLkr = 0.0, upperLkr = 100_000.0, rate = 0.0),
                ApitBandDto(lowerLkr = 120_000.0, upperLkr = null, rate = 0.06)
            )
        )
        val percentRate = ApitConfigDto(bands = listOf(ApitBandDto(lowerLkr = 0.0, upperLkr = null, rate = 6.0)))

        assertEquals(ApitTaxBand.DEFAULTS, TaxConfigRepositoryImpl.bandsOrDefault(withGap))
        assertEquals(ApitTaxBand.DEFAULTS, TaxConfigRepositoryImpl.bandsOrDefault(percentRate))
    }
}
