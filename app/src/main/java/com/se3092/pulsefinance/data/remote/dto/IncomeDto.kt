package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus

data class IncomeDto(
    @DocumentId val id: String = "",
    val userId: String = "",
    val source: String = "SALARY",
    val title: String = "",
    val clientOrPlatform: String = "",
    val originalAmount: Double = 0.0,
    val currency: String = "LKR",
    val exchangeRateToLkr: Double = 1.0,
    val amountInLkr: Double = 0.0,
    val status: String = "RECEIVED",
    val dateReceivedOrExpected: Long = System.currentTimeMillis(),
    val isRecurringMonthly: Boolean = false,
    val notes: String = ""
) {
    fun toDomain(): IncomeStream = IncomeStream(
        id = id,
        userId = userId,
        source = try { IncomeSource.valueOf(source) } catch (e: Exception) { IncomeSource.SALARY },
        title = title,
        clientOrPlatform = clientOrPlatform,
        originalAmount = originalAmount,
        currency = try { Currency.valueOf(currency) } catch (e: Exception) { Currency.LKR },
        exchangeRateToLkr = exchangeRateToLkr,
        amountInLkr = amountInLkr,
        status = try { InvoiceStatus.valueOf(status) } catch (e: Exception) { InvoiceStatus.RECEIVED },
        dateReceivedOrExpected = dateReceivedOrExpected,
        isRecurringMonthly = isRecurringMonthly,
        notes = notes
    )

    companion object {
        fun fromDomain(domain: IncomeStream): IncomeDto = IncomeDto(
            id = domain.id,
            userId = domain.userId,
            source = domain.source.name,
            title = domain.title,
            clientOrPlatform = domain.clientOrPlatform,
            originalAmount = domain.originalAmount,
            currency = domain.currency.name,
            exchangeRateToLkr = domain.exchangeRateToLkr,
            amountInLkr = domain.amountInLkr,
            status = domain.status.name,
            dateReceivedOrExpected = domain.dateReceivedOrExpected,
            isRecurringMonthly = domain.isRecurringMonthly,
            notes = domain.notes
        )
    }
}
