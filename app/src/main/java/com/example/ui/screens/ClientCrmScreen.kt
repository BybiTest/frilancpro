package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClientProject
import com.example.model.ProjectStatus
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ClientCrmScreen(
    clientProjects: List<ClientProject>,
    onAddProject: (ClientProject) -> Unit,
    onStatusChange: (String, ProjectStatus) -> Unit
) {
    val colors = LocalAppColors.current
    val fontScale = LocalFontScale.current.scale
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<ProjectStatus?>(null) }

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val totalActivePipeline = clientProjects
        .filter { it.status != ProjectStatus.COMPLETED && it.status != ProjectStatus.ARCHIVED }
        .sumOf { it.budgetToman }

    val filteredProjects = if (selectedStatusFilter == null) {
        clientProjects
    } else {
        clientProjects.filter { it.status == selectedStatusFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pipeline Total Value Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, colors.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ارزش کل پروژه‌های در جریان خط لوله", color = colors.textSecondary, fontSize = (11 * fontScale).sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${numberFormat.format(totalActivePipeline)} تومان",
                                color = colors.primary,
                                fontSize = (20 * fontScale).sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900),
                            modifier = Modifier.testTag("add_client_project_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("پروژه جدید", fontSize = (12 * fontScale).sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("همه (${clientProjects.size})", fontSize = (11 * fontScale).sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary,
                        selectedLabelColor = Navy900,
                        containerColor = colors.surfaceVariant,
                        labelColor = colors.textSecondary
                    )
                )

                ProjectStatus.entries.forEach { status ->
                    val isSelected = selectedStatusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = status },
                        label = { Text(status.titleFa, fontSize = (11 * fontScale).sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colors.primary,
                            selectedLabelColor = Navy900,
                            containerColor = colors.surfaceVariant,
                            labelColor = colors.textSecondary
                        )
                    )
                }
            }
        }

        // Projects List
        items(filteredProjects) { project ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(project.projectTitle, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = (14 * fontScale).sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(project.status.colorHex).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(project.status.titleFa, color = Color(project.status.colorHex), fontSize = (11 * fontScale).sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("کارفرما: ${project.clientName}", color = colors.textSecondary, fontSize = (12 * fontScale).sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مبلغ توافقی: ${numberFormat.format(project.budgetToman)} ت", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = (12 * fontScale).sp)
                        Text("موعد تحویل: ${project.deadlineDate}", color = colors.textSecondary, fontSize = (11 * fontScale).sp)
                    }

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { project.progressPercent.coerceIn(0, 100) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(project.status.colorHex),
                        trackColor = colors.surfaceVariant
                    )

                    // Follow up reminder
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = colors.primary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("پیگیری بعدی: ${project.nextFollowUp}", color = colors.textPrimary, fontSize = (11 * fontScale).sp)
                    }

                    // Quick status change buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (project.status != ProjectStatus.COMPLETED) {
                            TextButton(
                                onClick = { onStatusChange(project.id, ProjectStatus.COMPLETED) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ثبت تسویه و پایان", color = Emerald500, fontSize = (11 * fontScale).sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var clientName by remember { mutableStateOf("") }
        var projectTitle by remember { mutableStateOf("") }
        var budgetInput by remember { mutableStateOf("15000000") }
        var deadline by remember { mutableStateOf("۱۰ آذر") }
        var budgetError by remember { mutableStateOf<String?>(null) }
        var formError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("افزودن پروژه جدید کارفرما", color = colors.textPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it; formError = null },
                        label = { Text("نام کارفرما یا شرکت") },
                        colors = appFieldColors(colors)
                    )
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it; formError = null },
                        label = { Text("عنوان پروژه") },
                        colors = appFieldColors(colors)
                    )
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = {
                            budgetInput = it
                                .replace("۰", "0").replace("۱", "1").replace("۲", "2")
                                .replace("۳", "3").replace("۴", "4").replace("۵", "5")
                                .replace("۶", "6").replace("۷", "7").replace("۸", "8")
                                .replace("۹", "9")
                                .filter { ch -> ch.isDigit() || ch == ',' || ch == '٬' || ch.isWhitespace() }
                            budgetError = null
                        },
                        label = { Text("مبلغ به تومان") },
                        isError = budgetError != null,
                        supportingText = { budgetError?.let { error -> Text(error) } },
                        colors = appFieldColors(colors)
                    )
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it; formError = null },
                        label = { Text("موعد تحویل") },
                        colors = appFieldColors(colors)
                    )
                    formError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val normalizedBudget = budgetInput
                            .replace(",", "").replace("٬", "").filterNot { it.isWhitespace() }
                        val parsedBudget = normalizedBudget.toLongOrNull()
                        when {
                            clientName.isBlank() || projectTitle.isBlank() -> {
                                formError = "نام کارفرما و عنوان پروژه را وارد کنید."
                            }
                            normalizedBudget.isBlank() || parsedBudget == null || parsedBudget <= 0L -> {
                                budgetError = "مبلغ معتبر و بزرگ‌تر از صفر وارد کنید."
                            }
                            deadline.isBlank() -> {
                                formError = "موعد تحویل را وارد کنید."
                            }
                            else -> {
                                onAddProject(
                                    ClientProject(
                                        clientName = clientName.trim(),
                                        projectTitle = projectTitle.trim(),
                                        budgetToman = parsedBudget,
                                        deadlineDate = deadline.trim(),
                                        status = ProjectStatus.IN_PROGRESS,
                                        nextFollowUp = "ارسال نمونه اولیه"
                                    )
                                )
                                showAddDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary, contentColor = Navy900)
                ) {
                    Text("ثبت پروژه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("انصراف", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface
        )
    }
}
