package com.se3092.pulsefinance.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.se3092.pulsefinance.data.remote.dto.AccountDto
import com.se3092.pulsefinance.data.remote.dto.ExpenseDto
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.dto.IncomeDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // ---------------- Expenses ----------------
    fun observeExpenses(userId: String): Flow<List<ExpenseDto>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("expenses")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(ExpenseDto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveExpense(userId: String, expense: ExpenseDto) {
        val docId = if (expense.id.isNotBlank()) expense.id else UUID.randomUUID().toString()
        val dtoWithId = expense.copy(id = docId, userId = userId)
        firestore.collection("users").document(userId)
            .collection("expenses").document(docId)
            .set(dtoWithId).await()
    }

    suspend fun deleteExpense(userId: String, expenseId: String) {
        firestore.collection("users").document(userId)
            .collection("expenses").document(expenseId)
            .delete().await()
    }

    // ---------------- Income Streams ----------------
    fun observeIncomeStreams(userId: String): Flow<List<IncomeDto>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("income_streams")
            .orderBy("dateReceivedOrExpected", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(IncomeDto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveIncomeStream(userId: String, income: IncomeDto) {
        val docId = if (income.id.isNotBlank()) income.id else UUID.randomUUID().toString()
        val dtoWithId = income.copy(id = docId, userId = userId)
        firestore.collection("users").document(userId)
            .collection("income_streams").document(docId)
            .set(dtoWithId).await()
    }

    suspend fun updateIncomeStatus(userId: String, incomeId: String, status: String) {
        firestore.collection("users").document(userId)
            .collection("income_streams").document(incomeId)
            .update("status", status).await()
    }

    suspend fun deleteIncomeStream(userId: String, incomeId: String) {
        firestore.collection("users").document(userId)
            .collection("income_streams").document(incomeId)
            .delete().await()
    }

    // ---------------- Savings Goals ----------------
    fun observeGoals(userId: String): Flow<List<GoalDto>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("goals")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(GoalDto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveGoal(userId: String, goal: GoalDto) {
        val docId = if (goal.id.isNotBlank()) goal.id else "macbook_pro_m4_primary"
        val dtoWithId = goal.copy(id = docId, userId = userId)
        firestore.collection("users").document(userId)
            .collection("goals").document(docId)
            .set(dtoWithId).await()
    }

    suspend fun updateGoalSavedAmount(userId: String, goalId: String, amountLkr: Double) {
        firestore.collection("users").document(userId)
            .collection("goals").document(goalId)
            .update("currentSavedLkr", amountLkr).await()
    }

    // ---------------- Accounts ----------------
    fun observeAccounts(userId: String): Flow<List<AccountDto>> = callbackFlow {
        val listener = firestore.collection("users").document(userId)
            .collection("accounts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(AccountDto::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveAccount(userId: String, account: AccountDto) {
        val docId = if (account.id.isNotBlank()) account.id else UUID.randomUUID().toString()
        val dtoWithId = account.copy(id = docId, userId = userId)
        firestore.collection("users").document(userId)
            .collection("accounts").document(docId)
            .set(dtoWithId).await()
    }
}
