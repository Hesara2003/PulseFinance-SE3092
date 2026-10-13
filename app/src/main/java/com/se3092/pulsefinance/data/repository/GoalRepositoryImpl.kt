package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.GoalDao
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val firestoreDataSource: FirestoreDataSource
) : GoalRepository {

    /** Emits null until the user has created a goal — no goal is invented on their behalf. */
    override fun getPrimaryGoal(userId: String): Flow<SavingsGoal?> = cacheBackedFlow(
        tag = TAG,
        local = goalDao.getPrimaryGoal(userId).map { it?.toDomain() },
        sync = {
            firestoreDataSource.observeGoals(userId).collect { dtos ->
                goalDao.replaceAll(userId, dtos.map { GoalEntity.fromDomain(it.toDomain()) })
            }
        }
    )

    override suspend fun saveGoal(goal: SavingsGoal): Result<Unit> = runCatching {
        val prepared = goal.copy(id = goal.id.ifBlank { UUID.randomUUID().toString() })
        goalDao.insertGoal(GoalEntity.fromDomain(prepared))
        firestoreDataSource.saveGoal(prepared.userId, GoalDto.fromDomain(prepared))
    }

    override suspend fun updateSavedAmount(
        goalId: String,
        userId: String,
        newAmountLkr: Double
    ): Result<Unit> = runCatching {
        goalDao.updateSavedAmount(goalId, userId, newAmountLkr)
        firestoreDataSource.updateGoalSavedAmount(userId, goalId, newAmountLkr)
    }

    private companion object {
        const val TAG = "GoalRepository"
    }
}
