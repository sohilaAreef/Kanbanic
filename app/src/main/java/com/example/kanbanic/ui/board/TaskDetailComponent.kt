package com.example.kanbanic.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanbanic.data.model.*
import com.example.kanbanic.ui.theme.BackgroundLight
import com.example.kanbanic.ui.theme.PrimaryIndigo
import com.example.kanbanic.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailBottomSheet(
    task: Task,
    onDismiss: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Task Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Title", color = TextSecondary, fontSize = 12.sp)
                    Text(task.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Description", color = TextSecondary, fontSize = 12.sp)
                    Text(task.description.ifEmpty { "No description." }, fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Priority", color = TextSecondary, fontSize = 12.sp)
                            Text(task.priority.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Importance", color = TextSecondary, fontSize = 12.sp)
                            Text(task.importance.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Assignee", color = TextSecondary, fontSize = 12.sp)
                    Text(task.assigneeId ?: "Unassigned", fontSize = 14.sp)

                    if (task.dueDate != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Due Date", color = TextSecondary, fontSize = 12.sp)
                        val date = Date(task.dueDate)
                        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        Text(format.format(date), fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Activity Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(task.activities) { log ->
                    ActivityItem(log)
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(task.comments) { comment ->
                    CommentItem(comment)
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            
            // Comment Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Write a comment...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = BackgroundLight,
                        focusedContainerColor = BackgroundLight
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onAddComment(commentText)
                            commentText = ""
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryIndigo, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ActivityItem(log: ActivityLog) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryIndigo))
        Spacer(modifier = Modifier.width(12.dp))
        Text(log.text, fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryIndigo.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(comment.userName.take(1).uppercase(), color = PrimaryIndigo, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundLight)
                .padding(12.dp)
        ) {
            Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(comment.text, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    members: List<User>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String, 
        desc: String, 
        priority: TaskPriority, 
        importance: TaskImportance,
        dueDate: Long?,
        assigneeId: String?,
        color: String?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var importance by remember { mutableStateOf(TaskImportance.IMPORTANT) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var assigneeId by remember { mutableStateOf<String?>(null) }
    var selectedCardColor by remember { mutableStateOf<Color?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedImportance by remember { mutableStateOf(false) }
    var expandedAssignee by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Priority", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedPriority = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(priority.name, fontSize = 12.sp)
                                }
                                DropdownMenu(expanded = expandedPriority, onDismissRequest = { expandedPriority = false }) {
                                    TaskPriority.entries.forEach { p ->
                                        DropdownMenuItem(text = { Text(p.name) }, onClick = { priority = p; expandedPriority = false })
                                    }
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Importance", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Box {
                                OutlinedButton(
                                    onClick = { expandedImportance = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(importance.name, fontSize = 12.sp)
                                }
                                DropdownMenu(expanded = expandedImportance, onDismissRequest = { expandedImportance = false }) {
                                    TaskImportance.entries.forEach { i ->
                                        DropdownMenuItem(text = { Text(i.name) }, onClick = { importance = i; expandedImportance = false })
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Assign To", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Box {
                        OutlinedButton(onClick = { expandedAssignee = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(members.find { it.id == assigneeId }?.name ?: "Unassigned")
                        }
                        DropdownMenu(expanded = expandedAssignee, onDismissRequest = { expandedAssignee = false }) {
                            DropdownMenuItem(text = { Text("Unassigned") }, onClick = { assigneeId = null; expandedAssignee = false })
                            members.forEach { user ->
                                DropdownMenuItem(text = { Text(user.name) }, onClick = { assigneeId = user.id; expandedAssignee = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Due Date", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dueDate?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it)) } ?: "Select Date")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Card Color", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                        val colors = listOf(null, Color(0xFFFDE0E0), Color(0xFFE0F2FE), Color(0xFFF0FDF4), Color(0xFFFFF7ED))
                        items(colors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color ?: Color.White)
                                    .border(if (selectedCardColor == color) 2.dp else 1.dp, if (selectedCardColor == color) PrimaryIndigo else Color.LightGray, CircleShape)
                                    .clickable { selectedCardColor = color }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onConfirm(
                        title, 
                        desc, 
                        priority, 
                        importance, 
                        dueDate, 
                        assigneeId, 
                        selectedCardColor?.let { String.format("#%06X", (0xFFFFFF and it.toArgb())) }
                    )
                }
            }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
