package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.DeviceInfo
import com.example.ui.theme.CyberCardBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberYellow

@Composable
fun DeviceStatusHeader(
    deviceInfo: DeviceInfo,
    isBusy: Boolean,
    onRefresh: () -> Unit,
    onRequestRoot: () -> Unit,
    onToggleSelinux: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("device_status_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // App Title & System Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberCyan, CyberPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Kernel Driver Core",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "DRIVER LOADER",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Superuser Kernel Engine • RT & QX",
                            color = CyberCyan.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    enabled = !isBusy,
                    modifier = Modifier.testTag("btn_refresh_status")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh System Info",
                        tint = CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Root Status & SELinux Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Root Status Badge
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !isBusy) { onRequestRoot() }
                        .testTag("badge_root_status"),
                    color = if (deviceInfo.isRootGranted) CyberGreen.copy(alpha = 0.15f) else CyberRed.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (deviceInfo.isRootGranted) CyberGreen.copy(alpha = 0.6f) else CyberRed.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (deviceInfo.isRootGranted) CyberGreen else CyberRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SUPERUSER (SU)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (deviceInfo.isRootGranted) "GRANTED (UID 0)" else "NOT ROOTED (SAFE)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (deviceInfo.isRootGranted) CyberGreen else CyberRed
                            )
                        }
                    }
                }

                // SELinux Status Badge
                val isPermissive = deviceInfo.selinuxMode.equals("Permissive", ignoreCase = true)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !isBusy) { onToggleSelinux() }
                        .testTag("badge_selinux_status"),
                    color = if (isPermissive) CyberYellow.copy(alpha = 0.15f) else Color(0xFF1E293B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPermissive) CyberYellow.copy(alpha = 0.6f) else CyberCardBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPermissive) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "SELinux Mode",
                            tint = if (isPermissive) CyberYellow else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SELINUX ENFORCE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = deviceInfo.selinuxMode.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPermissive) CyberYellow else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hardware & Kernel Telemetry Strip
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF090D12),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2430))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "KERNEL:",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = deviceInfo.kernelRelease.ifBlank { "Detecting..." },
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ARCH:",
                            color = CyberPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = deviceInfo.architecture,
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isBusy) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberCyan,
                        trackColor = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}
