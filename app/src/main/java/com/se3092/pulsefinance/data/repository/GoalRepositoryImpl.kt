package com.se3092.pulsefinance.data.repository

import com.se3092.pulsefinance.data.local.dao.GoalDao
import com.se3092.pulsefinance.data.local.entity.GoalEntity
import com.se3092.pulsefinance.data.remote.dto.GoalDto
import com.se3092.pulsefinance.data.remote.firebase.FirestoreDataSource
import com.se3092.pulsefinance.domain.model.SavingsGoal
import com.se3092.pulsefinance.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao,
    private val firestoreDataSource: FirestoreDataSource,
    private val remoteWriter: RemoteWriter
) : GoalRepository {

    override fun getPrimaryGoal(userId: String): Flow<SavingsGoal?> = cacheBackedFlow(
        label = "goals",
        local = goalDao.getPrimaryGoal(userId),
        remote = firestoreDataSource.observeGoals(userId),
        persist = { dtos ->
            goalDao.replaceUserGoals(userId, dtos.map { GoalEntity.fromDomain(it.toDomain()) })
        }
    ).map { entity -> entity?.toDomain() }

    override suspend fun saveGoal(goal: SavingsGoal): Result<Unit> = runCatching {
        val prepared = goal.copy(id = goal.id.ifBlank { PRIMARY_GOAL_ID })
        goalDao.upsertGoal(GoalEntity.fromDomain(prepared))
        remoteWriter.enqueue("saveGoal") {
            firestoreDataSource.saveGoal(prepared.userId, GoalDto.fromDomain(prepared))
        }
    }

    override suspend fun updateSavedAmount(
        goalId: String,
        userId: String,
        newAmountLkr: Double
    ): Result<Unit> = runCatching {
        goalDao.updateSavedAmount(goalId, userId, newAmountLkr)
        remoteWriter.enqueue("updateGoalSavedAmount") {
            firestoreDataSource.updateGoalSavedAmount(userId, goalId, newAmountLkr)
        }
    }

    companion object {
        const val PRIMARY_GOAL_ID = "macbook_pro_m4_primary"
    }
}
