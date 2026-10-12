package com.example.model

enum class DriverType(val displayName: String, val description: String) {
    RT_DEV("RTDev Driver", "Real-Time thread scheduler priority and latency optimizer"),
    RT_HOOK("RTHook Driver", "Kernel input reader hook & touch sampling engine"),
    QX_DRIVER("QX Driver", "Qualcomm/GPU low-latency touch response & gaming driver")
}

enum class DriverStatus {
    INACTIVE,
    LOADING,
    ACTIVE
}

enum class LogType {
    INFO,
    COMMAND,
    OUTPUT,
    SUCCESS,
    ERROR,
    WARN
}

data class TerminalLog(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val timestamp: String,
    val text: String,
    val type: LogType = LogType.OUTPUT
)

data class DeviceInfo(
    val brand: String = "",
    val model: String = "",
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val kernelRelease: String = "",
    val architecture: String = "",
    val isRootGranted: Boolean = false,
    val suBinaryPath: String? = null,
    val selinuxMode: String = "Unknown",
    val isSimulationMode: Boolean = false
)

object KernelVersionCatalog {
    val supportedVersions = listOf(
        "Auto (Detect Device)",
        "4.14.117",
        "4.14.141",
        "4.14.180",
        "4.14.186",
        "4.14.190",
        "4.19.81",
        "4.19.113",
        "4.19.157",
        "4.19.191",
        "5.4 (Generic)",
        "5.4b",
        "5.4c",
        "5.10 (GKI)",
        "5.15 (GKI)",
        "6.1 (GKI)",
        "6.6 (GKI)",
        "6.12.23",
        "6.12.58"
    )

    fun findBestMatch(kernelRelease: String): String {
        val clean = kernelRelease.trim()
        val found = supportedVersions.find { it != "Auto (Detect Device)" && clean.contains(it.substringBefore(" ")) }
        return found ?: "4.19.157"
    }
}
