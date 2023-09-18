package com.posse.kotlin1.calendar.feature_calendar.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.common.domain.model.DrinkType
import com.posse.kotlin1.calendar.common.utils.DateTimeUtils
import com.posse.kotlin1.calendar.feature_calendar.domain.model.DayData
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.datetime.LocalDate
import resources.MR

@Composable
fun CalendarDaysComponent(
    weeks: List<List<DayData?>>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentDay by remember(weeks) { mutableStateOf(DateTimeUtils.today) }

    Column(modifier = modifier) {
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    val onDayClickAction: () -> Unit =
                        remember(day) { { day?.let { onDayClick(it.date) } } }

                    val enabled by remember(day) {
                        derivedStateOf {
                            val date = day?.date ?: return@derivedStateOf false
                            currentDay.toEpochDays() <= date.toEpochDays()
                        }
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                enabled = enabled,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onDayClickAction
                            )
                            .padding(4.dp)
                    ) {
                        day?.let { dayData ->
                            Image(
                                painter = painterResource(MR.images.shotglass_empty),
                                contentDescription = null,
                                colorFilter = if (dayData.date == currentDay) {
                                    ColorFilter.tint(MaterialTheme.colorScheme.primary)
                                } else null,
                                modifier = Modifier.fillMaxSize()
                            )

                            dayData.drinkType?.let {
                                Image(
                                    painter = painterResource(MR.images.shotglass_fill),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(
                                        color = MaterialTheme.colorScheme.primary.copy(
                                            alpha = when (it) {
                                                DrinkType.Full -> 1f
                                                DrinkType.Half -> 0.5f
                                            }
                                        )
                                    ),
                                    modifier = Modifier.fillMaxSize(0.97f)
                                )
                            }


                            val text = remember { dayData.date.dayOfMonth.toString() }

                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (dayData.drinkType != null) MaterialTheme.colorScheme.onPrimary
                                else LocalContentColor.current,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                        } ?: Spacer(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}