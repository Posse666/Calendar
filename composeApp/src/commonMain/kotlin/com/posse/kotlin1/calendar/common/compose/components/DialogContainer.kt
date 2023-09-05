package com.posse.kotlin1.calendar.common.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.posse.kotlin1.calendar.common.compose.utils.MultipleEventsCutter
import com.posse.kotlin1.calendar.common.compose.utils.get
import com.posse.kotlin1.calendar.common.compose.utils.getTextPadding
import com.posse.kotlin1.calendar.common.compose.utils.imePadding
import kotlinx.coroutines.launch

@Composable
fun DialogContainer(
    header: @Composable () -> Unit,
    confirmText: @Composable () -> Unit,
    confirmTextPadding: PaddingValues = ButtonDefaults.ContentPadding,
    confirmAction: () -> Unit,
    confirmEnabled: () -> Boolean,
    cancelText: String? = null,
    cancelAction: (() -> Unit)? = null,
    onDisposeRequest: () -> Unit,
    body: @Composable () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDisposeRequest
            )
            .draggable(
                rememberDraggableState {},
                orientation = Orientation.Vertical
            )
    ) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1f))

            val isDarkTheme = isSystemInDarkTheme()
            ShadowBox(
                elevationProvider = { if (isDarkTheme) 0.dp else 8.dp },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.imePadding()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .widthIn(max = maxWidth * 0.8f)
                        .heightIn(max = maxHeight * 0.8f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(
                            width = 1.dp,
                            color = if (isDarkTheme) MaterialTheme.colorScheme.onBackground
                            else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(16.dp)
                        ) {
                            header()
                        }

                        val scrollState = rememberScrollState()
                        val scope = rememberCoroutineScope()

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .verticalScroll(scrollState)
                                .draggable(
                                    rememberDraggableState {
                                        scope.launch {
                                            scrollState.scrollBy(-it)
                                        }
                                    },
                                    orientation = Orientation.Vertical
                                )
                        ) {
                            body()
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val multipleEventsCutter = remember { MultipleEventsCutter.get() }

                            cancelText?.let { cancel ->
                                OutlinedButton(
                                    onClick = { multipleEventsCutter.processEvent { cancelAction?.invoke() } },
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(8.dp, CircleShape)
                                ) {
                                    Text(
                                        text = cancel,
                                        style = MaterialTheme.typography.bodyLarge,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = getTextPadding())
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))
                            }

                            ConfirmButton(
                                confirmText = confirmText,
                                confirmAction = confirmAction,
                                confirmEnabled = confirmEnabled,
                                multipleEventsCutter = multipleEventsCutter,
                                confirmTextPadding = confirmTextPadding,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }

    AndroidBackButtonHandler(
        onBackRequest = onDisposeRequest
    )
}

@Composable
private fun ConfirmButton(
    confirmText: @Composable () -> Unit,
    confirmAction: () -> Unit,
    confirmEnabled: () -> Boolean,
    multipleEventsCutter: MultipleEventsCutter,
    confirmTextPadding: PaddingValues,
    modifier: Modifier
) {
    Button(
        onClick = { multipleEventsCutter.processEvent { confirmAction() } },
        enabled = confirmEnabled(),
        contentPadding = confirmTextPadding,
        modifier = modifier
    ) {
        confirmText()
    }
}