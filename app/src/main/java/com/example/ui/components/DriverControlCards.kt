package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DriverStatus
import com.example.model.DriverType
import com.example.model.KernelVersionCatalog
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberYellow

@Composable
fun KernelSelectorCard(
    selectedKernel: String,
    detectedKernel: String,
    onSelectKernel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kernel_selector_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        border = BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Kernel Selector",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TARGET KERNEL PROFILE",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Detected: $detectedKernel",
                    color = CyberGreen,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expanded = true }
                        .testTag("dropdown_kernel_version"),
                    color = CyberSurfaceVariant,
                    border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedKernel,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Open dropdown",
                            tint = CyberCyan
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(CyberCardBg)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                ) {
                    KernelVersionCatalog.supportedVersions.forEach { version ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = version,
                                    color = if (version == selectedKernel) CyberCyan else Color.White,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = if (version == selectedKernel) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSelectKernel(version)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RtDriverCard(
    driverType: DriverType,
    status: DriverStatus,
    activeVersion: String?,
    isBusy: Boolean,
    onSelectType: (DriverType) -> Unit,
    onLoad: () -> Unit,
    onUnload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rt_driver_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        border = BorderStroke(
            1.dp,
            if (status == DriverStatus.ACTIVE) CyberGreen.copy(alpha = 0.8f) else CyberCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "RT Driver",
                            tint = CyberPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "RT DRIVER (REAL-TIME)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Low-latency scheduling & thread priority hook",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = when (status) {
                        DriverStatus.ACTIVE -> CyberGreen.copy(alpha = 0.2f)
                        DriverStatus.LOADING -> CyberYellow.copy(alpha = 0.2f)
                        DriverStatus.INACTIVE -> Color(0xFF1E293B)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        when (status) {
                            DriverStatus.ACTIVE -> CyberGreen
                            DriverStatus.LOADING -> CyberYellow
                            DriverStatus.INACTIVE -> Color.DarkGray
                        }
                    )
                ) {
                    Text(
                        text = when (status) {
                            DriverStatus.ACTIVE -> "ACTIVE"
                            DriverStatus.LOADING -> "LOADING..."
                            DriverStatus.INACTIVE -> "INACTIVE"
                        },
                        color = when (status) {
                            DriverStatus.ACTIVE -> CyberGreen
                            DriverStatus.LOADING -> CyberYellow
                            DriverStatus.INACTIVE -> Color.LightGray
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Driver Mode Selector (RTDev vs RTHook)
            Text(
                text = "DRIVER FLAVOR:",
                color = Color.LightGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(DriverType.RT_DEV, DriverType.RT_HOOK).forEach { type ->
                    val isSelected = driverType == type
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isBusy) { onSelectType(type) }
                            .testTag("tab_rt_${type.name.lowercase()}"),
                        color = if (isSelected) CyberPurple.copy(alpha = 0.25f) else CyberSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) CyberPurple else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CyberPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = type.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLoad,
                    enabled = !isBusy && status != DriverStatus.LOADING,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_load_rt_driver"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (status == DriverStatus.ACTIVE) "RE-LOAD RT" else "LOAD RT DRIVER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (status == DriverStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = onUnload,
                        enabled = !isBusy,
                        modifier = Modifier.testTag("btn_unload_rt_driver"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberRed
                        ),
                        border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Unload RT",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UNLOAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QxDriverCard(
    status: DriverStatus,
    selectedTouchRate: String,
    selectedGpuMode: String,
    isBusy: Boolean,
    onSelectTouchRate: (String) -> Unit,
    onSelectGpuMode: (String) -> Unit,
    onLoad: () -> Unit,
    onUnload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("qx_driver_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        border = BorderStroke(
            1.dp,
            if (status == DriverStatus.ACTIVE) CyberCyan.copy(alpha = 0.8f) else CyberCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "QX Driver",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "QX DRIVER (GAMING & TOUCH)",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Qualcomm GPU governor & 240Hz+ touch sampling",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                Surface(
                    color = when (status) {
                        DriverStatus.ACTIVE -> CyberCyan.copy(alpha = 0.2f)
                        DriverStatus.LOADING -> CyberYellow.copy(alpha = 0.2f)
                        DriverStatus.INACTIVE -> Color(0xFF1E293B)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        when (status) {
                            DriverStatus.ACTIVE -> CyberCyan
                            DriverStatus.LOADING -> CyberYellow
                            DriverStatus.INACTIVE -> Color.DarkGray
                        }
                    )
                ) {
                    Text(
                        text = when (status) {
                            DriverStatus.ACTIVE -> "ACTIVE"
                            DriverStatus.LOADING -> "LOADING..."
                            DriverStatus.INACTIVE -> "INACTIVE"
                        },
                        color = when (status) {
                            DriverStatus.ACTIVE -> CyberCyan
                            DriverStatus.LOADING -> CyberYellow
                            DriverStatus.INACTIVE -> Color.LightGray
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Touch Sampling Rate Options
            Text(
                text = "TOUCH SAMPLING RATE BOOST:",
                color = Color.LightGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("120Hz", "240Hz (Optimal)", "360Hz", "480Hz Ultra").forEach { rate ->
                    val isSelected = selectedTouchRate.startsWith(rate.substringBefore(" "))
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectTouchRate(rate) },
                        label = {
                            Text(
                                text = rate,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CyberCyan,
                            containerColor = CyberSurfaceVariant,
                            labelColor = Color.LightGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyberCyan else Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // GPU Tuning Options
            Text(
                text = "GPU SCHEDULER PROFILE:",
                color = Color.LightGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Performance", "Ultra Gaming", "Balance").forEach { mode ->
                    val isSelected = selectedGpuMode == mode
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectGpuMode(mode) },
                        label = {
                            Text(
                                text = mode,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.25f),
                            selectedLabelColor = CyberCyan,
                            containerColor = CyberSurfaceVariant,
                            labelColor = Color.LightGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyberCyan else Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLoad,
                    enabled = !isBusy && status != DriverStatus.LOADING,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_load_qx_driver"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF00363D)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (status == DriverStatus.ACTIVE) "RE-LOAD QX" else "LOAD QX DRIVER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (status == DriverStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = onUnload,
                        enabled = !isBusy,
                        modifier = Modifier.testTag("btn_unload_qx_driver"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberRed
                        ),
                        border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Unload QX",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UNLOAD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TurboLoadButton(
    isBusy: Boolean,
    onTurboLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onTurboLoad,
        enabled = !isBusy,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("btn_turbo_load_both"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(CyberPurple, CyberCyan)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚡ ONE-CLICK TURBO LOAD (RT + QX)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
