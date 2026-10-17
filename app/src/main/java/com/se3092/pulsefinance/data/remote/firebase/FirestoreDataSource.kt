package com.se3092.pulsefinance.data.remote.firebase

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.se3092.pulsefinance.data.remote.dto.AccountDto
import com.se3092.pulsefinance.data.remote.dto.ExpenseDto
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.dto.IncomeDto
import com.se3092.pulsefinance.data.remote.dto.SubscriptionReviewDto
import com.se3092.pulsefinance.domain.model.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One emission of a Firestore query listener. [isFromCache] lets the caching layer avoid
 * wiping Room with an empty, not-yet-synced offline snapshot.
 */
data class RemoteSnapshot<T>(
    val items: List<T>,
    val isFromCache: Boolean
)

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(userId: String) = firestore.collection(USERS).document(userId)
    private fun userCollection(userId: String, name: String) = userDoc(userId).collection(name)

    private fun <T : Any> observeCollection(
        query: Query,
        type: Class<T>,
        label: String
    ): Flow<RemoteSnapshot<T>> = callbackFlow {
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Snapshot listener for $label failed", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(type)
                } catch (e: RuntimeException) {
                    Log.w(TAG, "Skipping malformed $label document ${doc.id}", e)
                    null
                }
            }
            Log.d(TAG, "$label snapshot: ${items.size} docs (fromCache=${snapshot.metadata.isFromCache})")
            trySend(RemoteSnapshot(items, snapshot.metadata.isFromCache))
        }
        awaitClose { listener.remove() }
    }

    private suspend fun <T : Any> write(collection: CollectionReference, docId: String, value: T, label: String) {
        try {
            collection.document(docId).set(value).await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write $label/$docId", e)
            throw e
        }
    }

    // ---------------- User document ----------------
    suspend fun upsertUserDocument(profile: UserProfile) {
        val data = mapOf(
            "uid" to profile.uid,
            "email" to profile.email,
            "displayName" to profile.displayName,
            "photoUrl" to profile.photoUrl,
            "provider" to profile.provider.name,
            "lastSignInAt" to FieldValue.serverTimestamp()
        )
        try {
            userDoc(profile.uid).set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upsert user document for ${profile.uid}", e)
            throw e
        }
    }

    suspend fun isScenarioSeeded(userId: String): Boolean {
        val snapshot = userDoc(userId).get().await()
        return snapshot.getBoolean(FIELD_SCENARIO_SEEDED) == true
    }

    suspend fun markScenarioSeeded(userId: String) {
        userDoc(userId).set(mapOf(FIELD_SCENARIO_SEEDED to true), SetOptions.merge()).await()
    }

    // ---------------- Expenses ----------------
    fun observeExpenses(userId: String): Flow<RemoteSnapshot<ExpenseDto>> = observeCollection(
        userCollection(userId, EXPENSES).orderBy("timestamp", Query.Direction.DESCENDING),
        ExpenseDto::class.java,
        EXPENSES
    )

    suspend fun saveExpense(userId: String, expense: ExpenseDto) {
        val docId = expense.id.ifBlank { UUID.randomUUID().toString() }
        write(userCollection(userId, EXPENSES), docId, expense.copy(id = docId, userId = userId), EXPENSES)
    }

    suspend fun deleteExpense(userId: String, expenseId: String) {
        userCollection(userId, EXPENSES).document(expenseId).delete().await()
    }

    // ---------------- Income Streams ----------------
    fun observeIncomeStreams(userId: String): Flow<RemoteSnapshot<IncomeDto>> = observeCollection(
        userCollection(userId, INCOME).orderBy("dateReceivedOrExpected", Query.Direction.DESCENDING),
        IncomeDto::class.java,
        INCOME
    )

    suspend fun saveIncomeStream(userId: String, income: IncomeDto) {
        val docId = income.id.ifBlank { UUID.randomUUID().toString() }
        write(userCollection(userId, INCOME), docId, income.copy(id = docId, userId = userId), INCOME)
    }

    suspend fun updateIncomeStatus(userId: String, incomeId: String, status: String) {
        userCollection(userId, INCOME).document(incomeId).update("status", status).await()
    }

    suspend fun deleteIncomeStream(userId: String, incomeId: String) {
        userCollection(userId, INCOME).document(incomeId).delete().await()
    }

    // ---------------- Savings Goals ----------------
    fun observeGoals(userId: String): Flow<RemoteSnapshot<GoalDto>> = observeCollection(
        userCollection(userId, GOALS),
        GoalDto::class.java,
        GOALS
    )

    suspend fun saveGoal(userId: String, goal: GoalDto) {
        val docId = goal.id.ifBlank { UUID.randomUUID().toString() }
        write(userCollection(userId, GOALS), docId, goal.copy(id = docId, userId = userId), GOALS)
    }

    suspend fun updateGoalSavedAmount(userId: String, goalId: String, amountLkr: Double) {
        userCollection(userId, GOALS).document(goalId).update("currentSavedLkr", amountLkr).await()
    }

    // ---------------- Accounts ----------------
    fun observeAccounts(userId: String): Flow<RemoteSnapshot<AccountDto>> = observeCollection(
        userCollection(userId, ACCOUNTS),
        AccountDto::class.java,
        ACCOUNTS
    )

    suspend fun saveAccount(userId: String, account: AccountDto) {
        val docId = account.id.ifBlank { UUID.randomUUID().toString() }
        write(userCollection(userId, ACCOUNTS), docId, account.copy(id = docId, userId = userId), ACCOUNTS)
    }

    suspend fun hasAccounts(userId: String): Boolean =
        !userCollection(userId, ACCOUNTS).limit(1).get().await().isEmpty

    suspend fun updateAccountBalance(userId: String, accountId: String, balanceLkr: Double) {
        userCollection(userId, ACCOUNTS).document(accountId).update("balanceLkr", balanceLkr).await()
    }

    // ---------------- Subscription Reviews ----------------
    fun observeSubscriptionReviews(userId: String): Flow<RemoteSnapshot<SubscriptionReviewDto>> = observeCollection(
        userCollection(userId, SUBSCRIPTION_REVIEWS),
        SubscriptionReviewDto::class.java,
        SUBSCRIPTION_REVIEWS
    )

    suspend fun saveSubscriptionReview(userId: String, review: SubscriptionReviewDto) {
        write(
            userCollection(userId, SUBSCRIPTION_REVIEWS),
            review.subscriptionKey,
            review.copy(userId = userId),
            SUBSCRIPTION_REVIEWS
        )
    }

    suspend fun deleteSubscriptionReview(userId: String, subscriptionKey: String) {
        userCollection(userId, SUBSCRIPTION_REVIEWS).document(subscriptionKey).delete().await()
    }

    private companion object {
        const val TAG = "FirestoreDataSource"
        const val USERS = "users"
        const val EXPENSES = "expenses"
        const val INCOME = "income_streams"
        const val GOALS = "goals"
        const val ACCOUNTS = "accounts"
        const val SUBSCRIPTION_REVIEWS = "subscription_reviews"
        const val FIELD_SCENARIO_SEEDED = "scenarioSeeded"
    }
}
