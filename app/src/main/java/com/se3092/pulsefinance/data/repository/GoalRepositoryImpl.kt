package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.GoalDao
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.GoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val firestoreDataSource: FirestoreDataSource
) : GoalRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getPrimaryGoal(userId: String): Flow<SavingsGoal?> {
        repositoryScope.launch { syncGoals(userId) }
        return goalDao.getPrimaryGoal(userId).map { entity ->
            entity?.toDomain() ?: SavingsGoal(
                id = "macbook_pro_m4_primary",
                userId = userId,
                title = "MacBook Pro M4",
                targetAmountLkr = 490000.0,
                currentSavedLkr = 11200.0
            )
        }
    }

    override suspend fun saveGoal(goal: SavingsGoal): Result<Unit> {
        return try {
            val goalId = if (goal.id.isNotBlank()) goal.id else "macbook_pro_m4_primary"
            val preparedGoal = goal.copy(id = goalId)

            goalDao.insertGoal(GoalEntity.fromDomain(preparedGoal))
            firestoreDataSource.saveGoal(goal.userId, GoalDto.fromDomain(preparedGoal))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun updateSavedAmount(
        goalId: String,
        userId: String,
        newAmountLkr: Double
    ): Result<Unit> {
        return try {
            goalDao.updateSavedAmount(goalId, userId, newAmountLkr)
            firestoreDataSource.updateGoalSavedAmount(userId, goalId, newAmountLkr)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncGoals(userId: String) {
        repositoryScope.launch {
            try {
                firestoreDataSource.observeGoals(userId).collect { dtos ->
                    val firstDto = dtos.firstOrNull()
                    if (firstDto != null) {
                        goalDao.insertGoal(GoalEntity.fromDomain(firstDto.toDomain()))
                    } else {
                        // Initialize default MacBook goal for Kavindu
                        val defaultGoal = SavingsGoal(
                            id = "macbook_pro_m4_primary",
                            userId = userId,
                            title = "MacBook Pro M4",
                            targetAmountLkr = 490000.0,
                            currentSavedLkr = 11200.0
                        )
                        saveGoal(defaultGoal)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
