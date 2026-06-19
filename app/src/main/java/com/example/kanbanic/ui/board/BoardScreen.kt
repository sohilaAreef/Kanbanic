package com.example.kanbanic.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanbanic.data.model.Column
import com.example.kanbanic.data.model.Project
import com.example.kanbanic.data.model.Task
import com.example.kanbanic.data.model.TaskPriority
import com.example.kanbanic.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    project: Project,
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onAddColumn: () -> Unit,
    onInviteMember: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onMoveTask: (String, String) -> Unit,
    onUpdateBackground: (String) -> Unit,
    onUpdateColumnColor: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var draggingTask by remember { mutableStateOf<Task?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var columnBounds by remember { mutableStateOf(mapOf<String, androidx.compose.ui.geometry.Rect>()) }
    var showBgMenu by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val layoutDirection = LocalLayoutDirection.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }


    LaunchedEffect(draggingTask, dragOffset) {
        if (draggingTask != null) {
            while (true) {
                val threshold = with(density) { 70.dp.toPx() }
                val maxScrollSpeed = 25f 
                
                var scrollDelta = 0f
                if (dragOffset.x > screenWidthPx - threshold) {

                    val intensity = (dragOffset.x - (screenWidthPx - threshold)) / threshold
                    scrollDelta = maxScrollSpeed * intensity.coerceIn(0f, 1f)
                } else if (dragOffset.x < threshold) {

                    val intensity = (threshold - dragOffset.x) / threshold
                    scrollDelta = -maxScrollSpeed * intensity.coerceIn(0f, 1f)
                }

                if (scrollDelta != 0f) {
                    val multiplier = if (layoutDirection == LayoutDirection.Rtl) -1f else 1f
                    listState.dispatchRawDelta(scrollDelta * multiplier)
                }
                delay(16) 
            }
        }
    }

    val boardBgColor = project.background?.let { Color(android.graphics.Color.parseColor(it)) } ?: BackgroundLight

    Box(modifier = Modifier.fillMaxSize()) {
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
                        Box {
                            IconButton(onClick = { showBgMenu = true }) {
                                Icon(Icons.Default.Palette, contentDescription = "Background")
                            }
                            DropdownMenu(expanded = showBgMenu, onDismissRequest = { showBgMenu = false }) {
                                val colors = listOf("#F8F9FA", "#E3F2FD", "#F1F8E9", "#FFF3E0", "#F3E5F5")
                                colors.forEach { colorHex ->
                                    DropdownMenuItem(
                                        text = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(colorHex))).border(1.dp, Color.Gray, CircleShape))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Background ${colors.indexOf(colorHex) + 1}")
                                            }
                                        },
                                        onClick = { onUpdateBackground(colorHex); showBgMenu = false }
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { /* More options */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = null)
                        }
                    }
                )
            }
        ) { padding ->
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(boardBgColor),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(project.columns) { column ->
                    val columnTasks = tasks.filter { it.columnId == column.id }
                    BoardColumn(
                        column = column,
                        tasks = columnTasks,
                        onAddTask = { onAddTask(column.id) },
                        onTaskClick = onTaskClick,
                        onUpdateColor = { color -> onUpdateColumnColor(column.id, color) },
                        onDragStart = { task, offset ->
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            draggingTask = task
                            dragOffset = offset
                        },
                        onDrag = { dragAmount ->
                            dragOffset += dragAmount
                        },
                        onDragEnd = {
                            val targetColumnId = columnBounds.entries.find { it.value.contains(dragOffset) }?.key
                            if (targetColumnId != null && draggingTask?.columnId != targetColumnId) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMoveTask(draggingTask!!.id, targetColumnId)
                            }
                            draggingTask = null
                            dragOffset = Offset.Zero
                        },
                        onPositioned = { rect ->
                            columnBounds = columnBounds + (column.id to rect)
                        },
                        isDraggingOver = draggingTask != null && columnBounds[column.id]?.contains(dragOffset) == true,
                        draggingTaskId = draggingTask?.id
                    )
                }
                item {
                    AddColumnButton(onClick = onAddColumn)
                }
            }
        }


        draggingTask?.let { task ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                    .width(280.dp)
                    .graphicsLayer {
                        scaleX = 1.05f
                        scaleY = 1.05f
                        alpha = 0.85f
                    }
            ) {
                TaskCard(task = task, onClick = {})
            }
        }
    }
}

@Composable
fun BoardColumn(
    column: Column,
    tasks: List<Task>,
    onAddTask: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onUpdateColor: (String) -> Unit,
    onDragStart: (Task, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onPositioned: (androidx.compose.ui.geometry.Rect) -> Unit,
    isDraggingOver: Boolean,
    draggingTaskId: String?
) {
    var showColorMenu by remember { mutableStateOf(false) }
    val columnBgColor = column.color?.let { Color(android.graphics.Color.parseColor(it)) } ?: Color.Transparent

    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .onGloballyPositioned { layoutCoordinates ->
                val rect = androidx.compose.ui.geometry.Rect(
                    layoutCoordinates.positionInWindow(),
                    layoutCoordinates.size.run { androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat()) }
                )
                onPositioned(rect)
            }
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDraggingOver) Color.Black.copy(0.05f) else columnBgColor)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = column.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepPurple
                )
                Spacer(modifier = Modifier.width(8.dp))
                Badge(containerColor = PrimaryIndigo.copy(alpha = 0.1f)) {
                    Text(tasks.size.toString(), color = PrimaryIndigo)
                }
            }
            Row {
                Box {
                    IconButton(onClick = { showColorMenu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = PrimaryIndigo.copy(0.6f), modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showColorMenu, onDismissRequest = { showColorMenu = false }) {
                        val colors = listOf("#FFFFFF", "#FDE0E0", "#E0F2FE", "#F0FDF4", "#FFF7ED")
                        colors.forEach { colorHex ->
                            DropdownMenuItem(
                                text = { 
                                    Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(colorHex))).border(1.dp, Color.Gray, CircleShape))
                                },
                                onClick = { onUpdateColor(colorHex); showColorMenu = false }
                            )
                        }
                    }
                }
                IconButton(onClick = onAddTask, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryIndigo)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks) { task ->
                var itemPosition by remember { mutableStateOf(Offset.Zero) }
                
                Box(
                    modifier = Modifier
                        .onGloballyPositioned { itemPosition = it.positionInWindow() }
                        .alpha(if (draggingTaskId == task.id) 0f else 1f)
                        .pointerInput(task) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { offset ->
                                    onDragStart(task, itemPosition + offset)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDrag(dragAmount)
                                },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() }
                            )
                        }
                ) {
                    TaskCard(task = task, onClick = { onTaskClick(task) })
                }
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
    val isDone = task.columnId == "3" // Done column ID in mock
    val cardBgColor = task.color?.let { Color(android.graphics.Color.parseColor(it)) } 
        ?: (if (isDone) Color(0xFFF1F8E9) else Color.White)
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val priorityColor = when (task.priority) {
                TaskPriority.LOW -> LabelGreen
                TaskPriority.MEDIUM -> LabelBlue
                TaskPriority.HIGH -> LabelOrange
                TaskPriority.URGENT -> LabelRed
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDone) Color.Gray.copy(alpha = 0.2f) else priorityColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isDone) "COMPLETED" else task.priority.name, 
                        color = if (isDone) Color.Gray else priorityColor, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                if (!isDone && task.importance != com.example.kanbanic.data.model.TaskImportance.IMPORTANT) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(task.importance.name, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.title, 
                fontWeight = FontWeight.SemiBold, 
                fontSize = 14.sp,
                color = if (isDone) Color.Gray else Color.Unspecified,
                style = if (isDone) androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
            )
            
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
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isDone) Color.Gray else AccentIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.assigneeId?.take(1)?.uppercase() ?: "U", color = Color.White, fontSize = 10.sp)
                }
                
                if (task.dueDate != null) {
                    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Text(format.format(Date(task.dueDate)), fontSize = 10.sp, color = TextSecondary)
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
