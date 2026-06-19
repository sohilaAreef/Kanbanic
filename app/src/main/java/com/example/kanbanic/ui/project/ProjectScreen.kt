package com.example.kanbanic.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kanbanic.data.model.Project
import com.example.kanbanic.ui.theme.PrimaryIndigo
import com.example.kanbanic.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    projects: List<Project>,
    onProjectClick: (String) -> Unit,
    onCreateProject: () -> Unit,
    onJoinProject: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Projects", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Log out",
                            tint = PrimaryIndigo
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateProject,
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (projects.isEmpty()) {
                EmptyProjectsState(onCreateProject, onJoinProject)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(projects) { project ->
                        ProjectItem(project = project, onClick = { onProjectClick(project.id) })
                    }
                    item {
                        OutlinedButton(
                            onClick = onJoinProject,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Join Project with Code")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectItem(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryIndigo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = project.name.take(1).uppercase(),
                    color = PrimaryIndigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = project.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = project.description, color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun EmptyProjectsState(onCreate: () -> Unit, onJoin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No projects yet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Create your first project or join a team", color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onCreate,
            modifier = Modifier.fillMaxWidth(0.7f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Create Project")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onJoin) {
            Text("Enter Invite Code")
        }
    }
}