package com.se3092.pulsefinance.data.remote.firebase

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.se3092.pulsefinance.data.remote.dto.AccountDto
import com.se3092.pulsefinance.data.remote.dto.ExpenseDto
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.dto.IncomeDto
import com.se3092.pulsefinance.domain.model.SubscriptionDecision
import com.se3092.pulsefinance.domain.model.SubscriptionReview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper over Firestore. Writes are fire-and-forget: Firestore applies them to its
 * local cache immediately (so snapshot listeners see them at once) and queues them for the
 * server while offline. Awaiting the Task would suspend until the server acknowledges,
 * which never happens offline.
 */
@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(userId: String) = firestore.collection("users").document(userId)

    private fun Task<Void>.logFailure(operation: String) {
        addOnFailureListener { Log.w(TAG, "Firestore $operation failed", it) }
    }

    // ---------------- Expenses ----------------
    fun observeExpenses(userId: String): Flow<List<ExpenseDto>> = callbackFlow {
        val listener = userDoc(userId).collection("expenses")
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

    fun saveExpense(userId: String, expense: ExpenseDto) {
        userDoc(userId).collection("expenses").document(expense.id)
            .set(expense.copy(userId = userId))
            .logFailure("saveExpense")
    }

    fun deleteExpense(userId: String, expenseId: String) {
        userDoc(userId).collection("expenses").document(expenseId)
            .delete()
            .logFailure("deleteExpense")
    }

    // ---------------- Income Streams ----------------
    fun observeIncomeStreams(userId: String): Flow<List<IncomeDto>> = callbackFlow {
        val listener = userDoc(userId).collection("income_streams")
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

    fun saveIncomeStream(userId: String, income: IncomeDto) {
        userDoc(userId).collection("income_streams").document(income.id)
            .set(income.copy(userId = userId))
            .logFailure("saveIncomeStream")
    }

    fun updateIncomeStatus(userId: String, incomeId: String, status: String, dateMillis: Long) {
        userDoc(userId).collection("income_streams").document(incomeId)
            .update(mapOf("status" to status, "dateReceivedOrExpected" to dateMillis))
            .logFailure("updateIncomeStatus")
    }

    fun deleteIncomeStream(userId: String, incomeId: String) {
        userDoc(userId).collection("income_streams").document(incomeId)
            .delete()
            .logFailure("deleteIncomeStream")
    }

    // ---------------- Savings Goals ----------------
    fun observeGoals(userId: String): Flow<List<GoalDto>> = callbackFlow {
        val listener = userDoc(userId).collection("goals")
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

    fun saveGoal(userId: String, goal: GoalDto) {
        userDoc(userId).collection("goals").document(goal.id)
            .set(goal.copy(userId = userId))
            .logFailure("saveGoal")
    }

    fun updateGoalSavedAmount(userId: String, goalId: String, amountLkr: Double) {
        userDoc(userId).collection("goals").document(goalId)
            .update("currentSavedLkr", amountLkr)
            .logFailure("updateGoalSavedAmount")
    }

    // ---------------- Accounts ----------------
    fun observeAccounts(userId: String): Flow<List<AccountDto>> = callbackFlow {
        val listener = userDoc(userId).collection("accounts")
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

    /** Server round-trip; throws when offline so callers never mistake "no cache" for "no accounts". */
    suspend fun hasAnyAccountOnServer(userId: String): Boolean =
        !userDoc(userId).collection("accounts").limit(1).get(Source.SERVER).await().isEmpty

    fun saveAccount(userId: String, account: AccountDto) {
        userDoc(userId).collection("accounts").document(account.id)
            .set(account.copy(userId = userId))
            .logFailure("saveAccount")
    }

    // ---------------- Subscription audit decisions ----------------
    // Stored as a map on the user document: subscriptionReviews.<key> = {decision, decidedAt}
    fun observeSubscriptionReviews(userId: String): Flow<Map<String, SubscriptionReview>> = callbackFlow {
        val listener = userDoc(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            @Suppress("UNCHECKED_CAST")
            val raw = snapshot?.get(FIELD_SUBSCRIPTION_REVIEWS) as? Map<String, Map<String, Any?>> ?: emptyMap()
            val reviews = raw.mapNotNull { (key, value) ->
                val decision = (value["decision"] as? String)
                    ?.let { runCatching { SubscriptionDecision.valueOf(it) }.getOrNull() }
                    ?: return@mapNotNull null
                val decidedAt = (value["decidedAt"] as? Number)?.toLong() ?: 0L
                key to SubscriptionReview(key, decision, decidedAt)
            }.toMap()
            trySend(reviews)
        }
        awaitClose { listener.remove() }
    }

    fun saveSubscriptionReview(userId: String, review: SubscriptionReview) {
        val payload = mapOf(
            FIELD_SUBSCRIPTION_REVIEWS to mapOf(
                review.key to mapOf(
                    "decision" to review.decision.name,
                    "decidedAt" to review.decidedAt
                )
            )
        )
        userDoc(userId).set(payload, SetOptions.merge()).logFailure("saveSubscriptionReview")
    }

    private companion object {
        const val TAG = "FirestoreDataSource"
        const val FIELD_SUBSCRIPTION_REVIEWS = "subscriptionReviews"
    }
}
