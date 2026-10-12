package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.DeviceInfo
import com.example.model.DriverStatus
import com.example.model.DriverType
import com.example.model.KernelVersionCatalog
import com.example.model.LogType
import com.example.model.TerminalLog
import com.example.service.RootShellExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DriverLoaderUiState(
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val selectedKernelVersion: String = "Auto (Detect Device)",
    val detectedKernelMatch: String = "4.19.157",
    val selectedRtDriverType: DriverType = DriverType.RT_DEV,
    val selectedRtPriority: String = "99",
    val selectedQxTouchRate: String = "240Hz",
    val selectedQxGpuMode: String = "Performance",
    val rtDriverStatus: DriverStatus = DriverStatus.INACTIVE,
    val qxDriverStatus: DriverStatus = DriverStatus.INACTIVE,
    val activeRtVersion: String? = null,
    val activeQxVersion: String? = null,
    val terminalLogs: List<TerminalLog> = emptyList(),
    val isBusy: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val successTitle: String = "",
    val successDetails: String = "",
    val commandInput: String = ""
)

class DriverLoaderViewModel(application: Application) : AndroidViewModel(application) {

    private val executor = RootShellExecutor(application)

    private val _uiState = MutableStateFlow(DriverLoaderUiState())
    val uiState: StateFlow<DriverLoaderUiState> = _uiState.asStateFlow()

    init {
        refreshDeviceInfo()
        log("Driver Loader Terminal initialized.", LogType.INFO)
        log("Ready to accept Superuser commands and load drivers.", LogType.INFO)
    }

    private fun log(text: String, type: LogType = LogType.OUTPUT) {
        val newLog = TerminalLog(
            timestamp = executor.currentTime(),
            text = text,
            type = type
        )
        _uiState.update { state ->
            val updated = (state.terminalLogs + newLog).takeLast(250)
            state.copy(terminalLogs = updated)
        }
    }

    fun refreshDeviceInfo() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            val info = executor.collectDeviceInfo()
            val bestMatch = KernelVersionCatalog.findBestMatch(info.kernelRelease)
            _uiState.update {
                it.copy(
                    deviceInfo = info,
                    detectedKernelMatch = bestMatch,
                    isBusy = false
                )
            }
            log("[SYSTEM] Device detected: ${info.brand} ${info.model} (Android ${info.androidVersion})", LogType.INFO)
            log("[KERNEL] Release: ${info.kernelRelease} -> Matched Profile: $bestMatch", LogType.INFO)
            if (info.isRootGranted) {
                log("[ROOT] Superuser Access: GRANTED (su at ${info.suBinaryPath ?: "PATH"})", LogType.SUCCESS)
            } else {
                log("[ROOT] Superuser not detected or denied. Simulation & Safe Mode active.", LogType.WARN)
            }
        }
    }

    fun requestRootAccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            log("[ACTION] Requesting Superuser permission...", LogType.COMMAND)
            val result = executor.executeCommand("id", preferRoot = true) { line, type ->
                log(line, type)
            }
            val hasRoot = result.isSuccess && result.output.any { it.contains("uid=0") }
            val updatedInfo = _uiState.value.deviceInfo.copy(isRootGranted = hasRoot)
            _uiState.update {
                it.copy(
                    deviceInfo = updatedInfo,
                    isBusy = false
                )
            }
            if (hasRoot) {
                log("[SUCCESS] Superuser permission granted! UID=0 (Root)", LogType.SUCCESS)
            } else {
                log("[WARN] Root permission not granted. Running with standard shell permissions.", LogType.WARN)
            }
        }
    }

    fun setSelinuxPermissive() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            val ok = executor.setSELinuxPermissive { line, type -> log(line, type) }
            if (ok) {
                _uiState.update {
                    it.copy(deviceInfo = it.deviceInfo.copy(selinuxMode = "Permissive"))
                }
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun onKernelVersionSelected(version: String) {
        _uiState.update { it.copy(selectedKernelVersion = version) }
        log("[CONFIG] Selected kernel profile target: $version", LogType.INFO)
    }

    fun onRtDriverTypeSelected(type: DriverType) {
        _uiState.update { it.copy(selectedRtDriverType = type) }
        log("[CONFIG] Selected RT driver type: ${type.displayName}", LogType.INFO)
    }

    fun onQxTouchRateSelected(rate: String) {
        _uiState.update { it.copy(selectedQxTouchRate = rate) }
        log("[CONFIG] QX Touch sampling rate set to: $rate", LogType.INFO)
    }

    fun onQxGpuModeSelected(mode: String) {
        _uiState.update { it.copy(selectedQxGpuMode = mode) }
        log("[CONFIG] QX GPU governor tuning set to: $mode", LogType.INFO)
    }

    fun onCommandInputChanged(text: String) {
        _uiState.update { it.copy(commandInput = text) }
    }

    fun loadRtDriver() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isBusy = true, rtDriverStatus = DriverStatus.LOADING) }

            val targetKernel = if (state.selectedKernelVersion.startsWith("Auto")) {
                state.detectedKernelMatch
            } else {
                state.selectedKernelVersion
            }

            val params = mapOf(
                "priority" to state.selectedRtPriority
            )

            val success = executor.loadDriver(
                driverType = state.selectedRtDriverType,
                targetKernel = targetKernel,
                parameters = params
            ) { line, type ->
                log(line, type)
            }

            if (success) {
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        rtDriverStatus = DriverStatus.ACTIVE,
                        activeRtVersion = targetKernel,
                        showSuccessDialog = true,
                        successTitle = "${state.selectedRtDriverType.displayName} Loaded!",
                        successDetails = "Status: ACTIVE (Kernel $targetKernel)\nMode: ${state.selectedRtDriverType.displayName}\nRT Priority: ${state.selectedRtPriority} (SCHED_FIFO)"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isBusy = false, rtDriverStatus = DriverStatus.INACTIVE)
                }
            }
        }
    }

    fun unloadRtDriver() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isBusy = true) }
            executor.unloadDriver(state.selectedRtDriverType) { line, type -> log(line, type) }
            _uiState.update {
                it.copy(
                    isBusy = false,
                    rtDriverStatus = DriverStatus.INACTIVE,
                    activeRtVersion = null
                )
            }
        }
    }

    fun loadQxDriver() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isBusy = true, qxDriverStatus = DriverStatus.LOADING) }

            val targetKernel = if (state.selectedKernelVersion.startsWith("Auto")) {
                state.detectedKernelMatch
            } else {
                state.selectedKernelVersion
            }

            val params = mapOf(
                "touch_rate" to state.selectedQxTouchRate,
                "gpu_mode" to state.selectedQxGpuMode
            )

            val success = executor.loadDriver(
                driverType = DriverType.QX_DRIVER,
                targetKernel = targetKernel,
                parameters = params
            ) { line, type ->
                log(line, type)
            }

            if (success) {
                _uiState.update {
                    it.copy(
                        isBusy = false,
                        qxDriverStatus = DriverStatus.ACTIVE,
                        activeQxVersion = targetKernel,
                        showSuccessDialog = true,
                        successTitle = "QX Driver Loaded Successfully!",
                        successDetails = "Status: ACTIVE (Kernel $targetKernel)\nTouch Boost: ${state.selectedQxTouchRate}\nGPU Profile: ${state.selectedQxGpuMode}\nLatency Hook: Injected"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isBusy = false, qxDriverStatus = DriverStatus.INACTIVE)
                }
            }
        }
    }

    fun unloadQxDriver() {
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            executor.unloadDriver(DriverType.QX_DRIVER) { line, type -> log(line, type) }
            _uiState.update {
                it.copy(
                    isBusy = false,
                    qxDriverStatus = DriverStatus.INACTIVE,
                    activeQxVersion = null
                )
            }
        }
    }

    fun loadBothDrivers() {
        viewModelScope.launch {
            log("[TURBO] Initiating Dual Driver Load (RT + QX)...", LogType.INFO)
            _uiState.update {
                it.copy(
                    isBusy = true,
                    rtDriverStatus = DriverStatus.LOADING,
                    qxDriverStatus = DriverStatus.LOADING
                )
            }

            val state = _uiState.value
            val targetKernel = if (state.selectedKernelVersion.startsWith("Auto")) {
                state.detectedKernelMatch
            } else {
                state.selectedKernelVersion
            }

            val rtOk = executor.loadDriver(
                driverType = state.selectedRtDriverType,
                targetKernel = targetKernel,
                parameters = mapOf("priority" to state.selectedRtPriority)
            ) { line, type -> log(line, type) }

            val qxOk = executor.loadDriver(
                driverType = DriverType.QX_DRIVER,
                targetKernel = targetKernel,
                parameters = mapOf(
                    "touch_rate" to state.selectedQxTouchRate,
                    "gpu_mode" to state.selectedQxGpuMode
                )
            ) { line, type -> log(line, type) }

            _uiState.update {
                it.copy(
                    isBusy = false,
                    rtDriverStatus = if (rtOk) DriverStatus.ACTIVE else DriverStatus.INACTIVE,
                    qxDriverStatus = if (qxOk) DriverStatus.ACTIVE else DriverStatus.INACTIVE,
                    activeRtVersion = if (rtOk) targetKernel else null,
                    activeQxVersion = if (qxOk) targetKernel else null,
                    showSuccessDialog = rtOk && qxOk,
                    successTitle = "All Drivers Loaded Successfully!",
                    successDetails = "✓ ${state.selectedRtDriverType.displayName} (v$targetKernel): ACTIVE\n✓ QX Driver (v$targetKernel): ACTIVE\nGaming performance & touch latency optimized."
                )
            }
        }
    }

    fun executeCustomCommand(command: String? = null) {
        val cmd = command ?: _uiState.value.commandInput.trim()
        if (cmd.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(commandInput = "", isBusy = true) }
            executor.executeCommand(cmd, preferRoot = true) { line, type ->
                log(line, type)
            }
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    fun clearTerminal() {
        _uiState.update { it.copy(terminalLogs = emptyList()) }
        log("[TERMINAL] Console buffer cleared.", LogType.INFO)
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }
}
