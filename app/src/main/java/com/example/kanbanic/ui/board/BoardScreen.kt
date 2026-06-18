package com.example.kanbanic.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanbanic.data.model.Column
import com.example.kanbanic.data.model.Project
import com.example.kanbanic.data.model.Task
import com.example.kanbanic.data.model.TaskPriority
import com.example.kanbanic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    project: Project,
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onAddColumn: () -> Unit,
    onInviteMember: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(project.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Team Board", fontSize = 12.sp, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onInviteMember) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Invite", tint = PrimaryIndigo)
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(project.columns) { column ->
                val columnTasks = tasks.filter { it.columnId == column.id }
                BoardColumn(
                    column = column,
                    tasks = columnTasks,
                    onAddTask = { onAddTask(column.id) },
                    onTaskClick = onTaskClick
                )
            }
            item {
                AddColumnButton(onClick = onAddColumn)
            }
        }
    }
}

@Composable
fun BoardColumn(
    column: Column,
    tasks: List<Task>,
    onAddTask: () -> Unit,
    onTaskClick: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = column.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Badge(containerColor = PrimaryIndigo.copy(alpha = 0.1f)) {
                    Text(tasks.size.toString(), color = PrimaryIndigo)
                }
            }
            IconButton(onClick = onAddTask) {
                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryIndigo)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                TaskCard(task = task, onClick = { onTaskClick(task) })
            }
            item {
                TextButton(
                    onClick = onAddTask,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task")
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val priorityColor = when (task.priority) {
                TaskPriority.LOW -> LabelGreen
                TaskPriority.MEDIUM -> LabelBlue
                TaskPriority.HIGH -> LabelOrange
                TaskPriority.URGENT -> LabelRed
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(priorityColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(task.priority.name, color = priorityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            
            if (task.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar placeholder
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AccentIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Text("U", color = Color.White, fontSize = 10.sp)
                }
                
                if (task.dueDate != null) {
                    Text("Dec 24", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun AddColumnButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add Column")
    }
}
