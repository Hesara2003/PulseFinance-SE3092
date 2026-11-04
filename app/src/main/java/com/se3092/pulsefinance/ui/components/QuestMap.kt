package com.se3092.pulsefinance.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se3092.pulsefinance.domain.model.QuestLevel
import com.se3092.pulsefinance.domain.model.QuestProgress
import com.se3092.pulsefinance.ui.common.compactAmount
import com.se3092.pulsefinance.ui.common.lkr
import com.se3092.pulsefinance.ui.theme.Display
import com.se3092.pulsefinance.ui.theme.Dusk
import com.se3092.pulsefinance.ui.theme.OnAccent
import com.se3092.pulsefinance.ui.theme.InkHigh
import com.se3092.pulsefinance.ui.theme.InkHigher
import com.se3092.pulsefinance.ui.theme.InkRaised
import com.se3092.pulsefinance.ui.theme.Mist
import com.se3092.pulsefinance.ui.theme.Numeric
import com.se3092.pulsefinance.ui.theme.Saffron
import com.se3092.pulsefinance.ui.theme.SaffronSoft
import com.se3092.pulsefinance.ui.theme.Teal
import com.se3092.pulsefinance.ui.theme.WarmWhite
import kotlin.math.sin

private enum class NodeState { CLEARED, CURRENT, AHEAD }

private fun QuestProgress.stateOf(level: QuestLevel): NodeState = when {
    level.number <= clearedLevels -> NodeState.CLEARED
    level.number == clearedLevels + 1 -> NodeState.CURRENT
    else -> NodeState.AHEAD
}

/** "Level 3 of 10" or "Goal reached". */
private fun QuestProgress.levelLabel(): String =
    currentLevel?.let { "Level ${it.number} of $levelCount" } ?: "All $levelCount levels cleared"

// ---------------------------------------------------------------------------------------------
// Compact card for the dashboard
// ---------------------------------------------------------------------------------------------

@Composable
fun QuestCard(quest: QuestProgress, initials: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = InkRaised,
        border = BorderStroke(1.dp, Saffron.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MACBOOK QUEST",
                        color = Teal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                    Text(
                        text = quest.levelLabel(),
                        fontFamily = Display,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = WarmWhite
                    )
                    quest.currentLevel?.let {
                        Text(text = "Next checkpoint: ${lkr(it.thresholdLkr)} saved", fontSize = 13.sp, color = Mist)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Open quest", tint = Mist)
            }

            QuestTrack(quest = quest, initials = initials)

            Row(verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    if (quest.isComplete) {
                        Text("Fully funded", fontFamily = Display, fontWeight = FontWeight.Bold, color = Teal, fontSize = 16.sp)
                    } else {
                        Text(
                            text = lkr(quest.toClearCurrentLkr),
                            fontFamily = Numeric,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = WarmWhite
                        )
                        Text(
                            text = "to clear this level" + (quest.daysToClearAtPace?.let { " · about $it days at your pace" } ?: ""),
                            fontSize = 12.sp,
                            color = Mist
                        )
                    }
                }
                Text(
                    text = "${compactAmount(quest.savedLkr)} / ${compactAmount(quest.levels.last().thresholdLkr)}",
                    fontFamily = Numeric,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Dusk
                )
            }
        }
    }
}

/** A horizontal row of level nodes joined by a road, with the avatar on the current level. */
@Composable
private fun QuestTrack(quest: QuestProgress, initials: String) {
    val pulse = rememberPulse()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        val count = quest.levelCount
        val step = maxWidth / count
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height / 2
            val stepPx = size.width / count
            val firstX = stepPx / 2
            val lastX = size.width - stepPx / 2
            drawLine(InkHigher, Offset(firstX, y), Offset(lastX, y), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
            val reachedX = firstX + stepPx * (quest.clearedLevels - 1 + quest.levelProgress).coerceAtLeast(0f)
            if (reachedX > firstX) {
                drawLine(Saffron, Offset(firstX, y), Offset(reachedX.coerceAtMost(lastX), y), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
            }
        }
        quest.levels.forEach { level ->
            val center = step * (level.number - 1) + step / 2
            val state = quest.stateOf(level)
            val isFinal = level.number == count
            val nodeSize = when {
                state == NodeState.CURRENT -> 34.dp
                isFinal -> 28.dp
                else -> 14.dp
            }
            Box(
                modifier = Modifier
                    .offset(x = center - nodeSize / 2, y = (44.dp - nodeSize) / 2)
                    .size(nodeSize),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state == NodeState.CURRENT -> {
                        Canvas(Modifier.fillMaxSize()) {
                            drawCircle(Saffron.copy(alpha = 0.35f * (1 - pulse)), radius = size.minDimension / 2 * (0.8f + 0.4f * pulse))
                        }
                        ProfileAvatar(initials = initials, size = 26.dp)
                    }
                    isFinal -> FinalNode(size = nodeSize, reached = state == NodeState.CLEARED)
                    state == NodeState.CLEARED -> Box(Modifier.fillMaxSize().clip(CircleShape).background(Saffron))
                    else -> Box(
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(InkRaised)
                            .border(2.dp, InkHigher, CircleShape)
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Full level map for the goal screen
// ---------------------------------------------------------------------------------------------

/**
 * The goal as a winding road, read bottom (start) to top (the goal).
 * Each level is labelled with its real rupee checkpoint.
 */
@Composable
fun QuestMap(quest: QuestProgress, initials: String, modifier: Modifier = Modifier) {
    val pulse = rememberPulse()
    val rowHeight = 66.dp
    val rows = quest.levelCount + 1 // + the start
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(rowHeight * rows)
    ) {
        val width = maxWidth
        // Node centres: index 0 is the start (bottom), index n is level n.
        fun xFraction(i: Int): Float = 0.5f + 0.26f * sin(i * 1.05f)
        fun yOf(i: Int): Dp = rowHeight * (rows - i) - rowHeight / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            val points = (0 until rows).map { i -> Offset(size.width * xFraction(i), yOf(i).toPx()) }
            fun road(upTo: Int): Path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1..upTo) {
                    val a = points[i - 1]
                    val b = points[i]
                    val midY = (a.y + b.y) / 2
                    cubicTo(a.x, midY, b.x, midY, b.x, b.y)
                }
            }
            // Full road ahead (dashed), then the travelled part on top (solid saffron)
            drawPath(
                road(rows - 1),
                color = InkHigher,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f)))
            )
            if (quest.clearedLevels > 0) {
                drawPath(road(quest.clearedLevels), color = Saffron, style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round))
            }
        }

        // Start marker
        Box(
            modifier = Modifier
                .offset(x = width * xFraction(0) - 8.dp, y = yOf(0) - 8.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(Saffron)
        )
        MapLabel(
            title = "Start",
            subtitle = "LKR 0",
            nodeX = width * xFraction(0),
            y = yOf(0),
            mapWidth = width,
            emphasis = false
        )

        quest.levels.forEach { level ->
            val i = level.number
            val state = quest.stateOf(level)
            val isFinal = i == quest.levelCount
            val nodeSize = when {
                isFinal -> 56.dp
                state == NodeState.CURRENT -> 48.dp
                else -> 30.dp
            }
            val x = width * xFraction(i)
            Box(
                modifier = Modifier
                    .offset(x = x - nodeSize / 2, y = yOf(i) - nodeSize / 2)
                    .size(nodeSize),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isFinal -> FinalNode(size = nodeSize, reached = state == NodeState.CLEARED)
                    state == NodeState.CURRENT -> {
                        Canvas(Modifier.fillMaxSize()) {
                            drawCircle(Saffron.copy(alpha = 0.35f * (1 - pulse)), radius = size.minDimension / 2 * (0.75f + 0.5f * pulse))
                            drawCircle(Saffron, radius = size.minDimension / 2 * 0.78f, style = Stroke(width = 3.dp.toPx()))
                        }
                        ProfileAvatar(initials = initials, size = 32.dp)
                    }
                    state == NodeState.CLEARED -> Box(
                        modifier = Modifier.fillMaxSize().clip(CircleShape).background(Saffron),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Cleared", tint = OnAccent, modifier = Modifier.size(18.dp))
                    }
                    else -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(InkRaised)
                            .border(2.dp, InkHigher, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$i", fontFamily = Numeric, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Dusk)
                    }
                }
            }
            MapLabel(
                title = level.name,
                subtitle = when (state) {
                    NodeState.CURRENT -> "${lkr(quest.toClearCurrentLkr)} to go"
                    else -> lkr(level.thresholdLkr)
                },
                nodeX = x,
                y = yOf(i),
                mapWidth = width,
                emphasis = state == NodeState.CURRENT || isFinal,
                nodeRadius = nodeSize / 2
            )
        }
    }
}

/** Places a two-line label on whichever side of the node has more room. */
@Composable
private fun MapLabel(
    title: String,
    subtitle: String,
    nodeX: Dp,
    y: Dp,
    mapWidth: Dp,
    emphasis: Boolean,
    nodeRadius: Dp = 8.dp
) {
    val gap = nodeRadius + 10.dp
    val onRight = nodeX < mapWidth / 2
    val labelWidth = if (onRight) mapWidth - nodeX - gap else nodeX - gap
    Column(
        modifier = Modifier
            .offset(x = if (onRight) nodeX + gap else 0.dp, y = y - 20.dp)
            .width(labelWidth),
        horizontalAlignment = if (onRight) Alignment.Start else Alignment.End
    ) {
        Text(
            text = title,
            fontFamily = Display,
            fontWeight = if (emphasis) FontWeight.ExtraBold else FontWeight.SemiBold,
            fontSize = if (emphasis) 16.sp else 14.sp,
            color = if (emphasis) WarmWhite else Mist,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (onRight) TextAlign.Start else TextAlign.End
        )
        Text(
            text = subtitle,
            fontFamily = Numeric,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = if (emphasis) Teal else Dusk
        )
    }
}

/** The "boss" level: the goal itself, drawn as a laptop inside a saffron ring. */
@Composable
private fun FinalNode(size: Dp, reached: Boolean) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (reached) Brush.linearGradient(listOf(SaffronSoft, Saffron))
                else Brush.linearGradient(listOf(InkHigh, InkRaised))
            )
            .border(2.5.dp, Saffron, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.LaptopMac,
            contentDescription = "Goal",
            tint = if (reached) OnAccent else Saffron,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}

@Composable
private fun rememberPulse(): Float {
    val transition = rememberInfiniteTransition(label = "quest-pulse")
    val value by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "pulse"
    )
    return value
}

