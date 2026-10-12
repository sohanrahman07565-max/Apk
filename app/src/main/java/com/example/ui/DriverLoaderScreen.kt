package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DeviceStatusHeader
import com.example.ui.components.KernelSelectorCard
import com.example.ui.components.QxDriverCard
import com.example.ui.components.RtDriverCard
import com.example.ui.components.SuccessDialog
import com.example.ui.components.TerminalConsole
import com.example.ui.components.TurboLoadButton
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverLoaderScreen(
    viewModel: DriverLoaderViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (uiState.deviceInfo.isRootGranted) CyberGreen else CyberCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DRIVER LOADER",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    Surface(
                        color = if (uiState.deviceInfo.isRootGranted) CyberGreen.copy(alpha = 0.2f) else Color(0xFF1E293B),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (uiState.deviceInfo.isRootGranted) CyberGreen else Color.Gray
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Root status",
                                tint = if (uiState.deviceInfo.isRootGranted) CyberGreen else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.deviceInfo.isRootGranted) "SU ACTIVE" else "NO SU",
                                color = if (uiState.deviceInfo.isRootGranted) CyberGreen else Color.LightGray,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberDarkBg,
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = CyberDarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                DeviceStatusHeader(
                    deviceInfo = uiState.deviceInfo,
                    isBusy = uiState.isBusy,
                    onRefresh = { viewModel.refreshDeviceInfo() },
                    onRequestRoot = { viewModel.requestRootAccess() },
                    onToggleSelinux = { viewModel.setSelinuxPermissive() }
                )
            }

            item {
                KernelSelectorCard(
                    selectedKernel = uiState.selectedKernelVersion,
                    detectedKernel = uiState.detectedKernelMatch,
                    onSelectKernel = { viewModel.onKernelVersionSelected(it) }
                )
            }

            item {
                TurboLoadButton(
                    isBusy = uiState.isBusy,
                    onTurboLoad = { viewModel.loadBothDrivers() }
                )
            }

            item {
                RtDriverCard(
                    driverType = uiState.selectedRtDriverType,
                    status = uiState.rtDriverStatus,
                    activeVersion = uiState.activeRtVersion,
                    isBusy = uiState.isBusy,
                    onSelectType = { viewModel.onRtDriverTypeSelected(it) },
                    onLoad = { viewModel.loadRtDriver() },
                    onUnload = { viewModel.unloadRtDriver() }
                )
            }

            item {
                QxDriverCard(
                    status = uiState.qxDriverStatus,
                    selectedTouchRate = uiState.selectedQxTouchRate,
                    selectedGpuMode = uiState.selectedQxGpuMode,
                    isBusy = uiState.isBusy,
                    onSelectTouchRate = { viewModel.onQxTouchRateSelected(it) },
                    onSelectGpuMode = { viewModel.onQxGpuModeSelected(it) },
                    onLoad = { viewModel.loadQxDriver() },
                    onUnload = { viewModel.unloadQxDriver() }
                )
            }

            // Terminal Console Section at the bottom
            item {
                TerminalConsole(
                    logs = uiState.terminalLogs,
                    commandInput = uiState.commandInput,
                    isBusy = uiState.isBusy,
                    onCommandInputChanged = { viewModel.onCommandInputChanged(it) },
                    onExecuteCommand = { cmd -> viewModel.executeCustomCommand(cmd) },
                    onClear = { viewModel.clearTerminal() }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (uiState.showSuccessDialog) {
            SuccessDialog(
                title = uiState.successTitle,
                details = uiState.successDetails,
                onDismiss = { viewModel.dismissSuccessDialog() }
            )
        }
    }
}
