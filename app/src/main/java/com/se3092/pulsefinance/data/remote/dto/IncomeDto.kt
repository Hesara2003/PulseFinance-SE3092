package com.se3092.pulsefinance.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus

data class IncomeDto(
    @DocumentId var id: String = "",
    var userId: String = "",
    var source: String = "SALARY",
    var title: String = "",
    var clientOrPlatform: String = "",
    var originalAmount: Double = 0.0,
    var currency: String = "LKR",
    var exchangeRateToLkr: Double = 1.0,
    var amountInLkr: Double = 0.0,
    var status: String = "RECEIVED",
    var dateReceivedOrExpected: Long = System.currentTimeMillis(),
    @get:PropertyName("isRecurringMonthly") @set:PropertyName("isRecurringMonthly")
    var isRecurringMonthly: Boolean = false,
    var notes: String = ""
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
