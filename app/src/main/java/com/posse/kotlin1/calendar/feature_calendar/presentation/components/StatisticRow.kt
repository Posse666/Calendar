package com.posse.kotlin1.calendar.feature_calendar.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticRow(
    name: String,
    stat: String,
    onStatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onStatClick,
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            Divider(
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxHeight()
                    .width(3.dp)
            )

            Box(contentAlignment = Alignment.CenterEnd){
                Text(
                    text = "000",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp).alpha(0f)
                )

                Text(
                    text = stat,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}