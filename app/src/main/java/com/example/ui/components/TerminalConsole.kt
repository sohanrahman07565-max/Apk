package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LogType
import com.example.model.TerminalLog
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberTerminalBg
import com.example.ui.theme.CyberYellow
import com.example.ui.theme.TerminalTextCyan
import com.example.ui.theme.TerminalTextGreen
import com.example.ui.theme.TerminalTextRed
import com.example.ui.theme.TerminalTextWhite
import com.example.ui.theme.TerminalTextYellow
import kotlinx.coroutines.launch

@Composable
fun TerminalConsole(
    logs: List<TerminalLog>,
    commandInput: String,
    isBusy: Boolean,
    onCommandInputChanged: (String) -> Unit,
    onExecuteCommand: (String?) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Auto-scroll to latest log
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("terminal_console_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberTerminalBg),
        border = BorderStroke(1.dp, Color(0xFF1E293B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Terminal Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberYellow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyberRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Terminal",
                        tint = CyberGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "root@android:/#",
                        color = CyberGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Copy output button
                    IconButton(
                        onClick = {
                            val allLogsText = logs.joinToString("\n") { "[${it.timestamp}] ${it.text}" }
                            clipboardManager.setText(AnnotatedString(allLogsText))
                            Toast.makeText(context, "Terminal output copied!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp).testTag("btn_copy_terminal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Console Output",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Clear button
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(32.dp).testTag("btn_clear_terminal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear Terminal",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Diagnostic Command Chips
            val quickChips = listOf("uname -a", "id", "lsmod", "getenforce", "cat /proc/version", "uptime")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickChips.forEach { cmd ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable(enabled = !isBusy) { onExecuteCommand(cmd) }
                            .testTag("chip_cmd_$cmd"),
                        color = Color(0xFF131B24),
                        border = BorderStroke(1.dp, Color(0xFF223246))
                    ) {
                        Text(
                            text = cmd,
                            color = CyberCyan.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Terminal Logs Screen Window
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 260.dp),
                color = Color(0xFF030508),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF15202B))
            ) {
                SelectionContainer {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .testTag("terminal_logs_list"),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(logs, key = { it.id }) { logItem ->
                            TerminalLogLine(log = logItem)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Interactive Command Prompt Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commandInput,
                    onValueChange = onCommandInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_terminal_command"),
                    placeholder = {
                        Text(
                            text = "Enter su command (e.g. lsmod, uname)...",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF0C121A),
                        unfocusedContainerColor = Color(0xFF090D14)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onExecuteCommand(null) })
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { onExecuteCommand(null) },
                    enabled = !isBusy && commandInput.isNotBlank(),
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (commandInput.isNotBlank()) CyberCyan else Color(0xFF1E293B))
                        .testTag("btn_send_command")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Execute Command",
                        tint = if (commandInput.isNotBlank()) Color(0xFF00363D) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalLogLine(log: TerminalLog) {
    val textColor = when (log.type) {
        LogType.SUCCESS -> TerminalTextGreen
        LogType.ERROR -> TerminalTextRed
        LogType.COMMAND -> TerminalTextCyan
        LogType.WARN -> TerminalTextYellow
        LogType.INFO -> Color(0xFF38BDF8)
        LogType.OUTPUT -> TerminalTextWhite
    }

    val prefix = when (log.type) {
        LogType.SUCCESS -> "✓ "
        LogType.ERROR -> "✗ "
        LogType.COMMAND -> "> "
        LogType.WARN -> "! "
        LogType.INFO -> "ℹ "
        LogType.OUTPUT -> "  "
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${log.timestamp} ",
            color = Color(0xFF475569),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "$prefix${log.text}",
            color = textColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp,
            fontWeight = if (log.type == LogType.SUCCESS || log.type == LogType.COMMAND) FontWeight.Bold else FontWeight.Normal
        )
    }
}
