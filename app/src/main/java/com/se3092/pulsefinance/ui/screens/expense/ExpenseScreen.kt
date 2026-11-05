package com.se3092.pulsefinance.ui.screens.expense

import com.se3092.pulsefinance.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.se3092.pulsefinance.ui.components.EmptyStateView
import com.se3092.pulsefinance.ui.components.PulseNoticeHost

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // =========================================================================
            // Top Navigation & Branding Header
            // =========================================================================
            Spacer(modifier = Modifier.height(10.dp))
            AuditTopBar(initials = state.initials)

            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // Main Content Scrollable
            // =========================================================================
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item { AuditTitle() }

                if (state.flaggedCount > 0) item {
                    LeakAlertCard(flaggedCount = state.flaggedCount, flaggedMonthlyLkr = state.flaggedMonthlyLkr)
                }

                item {
                    AuditMetrics(
                        flaggedAnnualLkr = state.flaggedAnnualLkr,
                        shareOfGoalRemainingPct = state.shareOfGoalRemainingPct,
                        goalTitle = state.goalTitle
                    )
                }

                item { DetectedSubscriptionsHeader() }

                if (!state.isLoading && state.subscriptions.isEmpty()) item {
                    EmptyStateView(
                        title = "No subscriptions detected yet",
                        message = "Log gym fees, streaming or SaaS auto-debits with the \"Subscriptions / Auto-Debits\" category. Anything charged in the last 6 weeks shows up here with its yearly cost.",
                        icon = Icons.Default.AutoMode,
                        illustration = R.drawable.illustration_no_data
                    )
                }

                // Subscription cards, grouped by the account they are charged to (FR-SUB-6)
                state.subscriptionsByAccount.forEach { (accountName, subs) ->
                    item(key = "header_$accountName") {
                        AccountGroupHeader(accountName = accountName, isSecondaryAccount = subs.any { it.isSecondaryAccount })
                    }
                    items(
                        items = subs,
                        key = { it.key }
                    ) { sub ->
                        SubscriptionCard(
                            sub = sub,
                            onCancel = { viewModel.markCancelled(sub) },
                            onKeep = { viewModel.keepSubscription(sub) },
                            onUsageAnswer = { used -> viewModel.recordUsageAnswer(sub, used) }
                        )
                    }
                }

                // Cancel every flagged subscription at once
                if (state.subscriptions.isNotEmpty()) item {
                    CancelAllFlaggedButton(
                        flaggedCount = state.flaggedCount,
                        flaggedMonthlyLkr = state.flaggedMonthlyLkr,
                        onClick = viewModel::markAllFlaggedCancelled
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Clearance for bottom dock
                }
            }
        }

        PulseNoticeHost(
            notice = state.notice,
            onDismiss = viewModel::dismissNotice,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
