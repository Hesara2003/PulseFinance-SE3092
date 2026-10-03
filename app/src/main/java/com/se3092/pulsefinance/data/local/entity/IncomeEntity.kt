package com.se3092.pulsefinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.se3092.pulsefinance.domain.model.Currency
import com.se3092.pulsefinance.domain.model.IncomeSource
import com.se3092.pulsefinance.domain.model.IncomeStream
import com.se3092.pulsefinance.domain.model.InvoiceStatus

@Entity(tableName = "income_streams")
data class IncomeEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val source: String,
    val title: String,
    val clientOrPlatform: String,
    val originalAmount: Double,
    val currency: String,
    val exchangeRateToLkr: Double,
    val amountInLkr: Double,
    val status: String,
    val dateReceivedOrExpected: Long,
    val isRecurringMonthly: Boolean,
    val notes: String
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
        fun fromDomain(domain: IncomeStream): IncomeEntity = IncomeEntity(
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
