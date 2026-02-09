package com.gabriel.employeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import kotlinx.coroutines.launch

import com.gabriel.employeeapp.data.model.Student
import com.gabriel.employeeapp.data.remote.StudentApi
import com.gabriel.employeeapp.data.repository.StudentRepositoryImpl
import com.gabriel.employeeapp.ui.theme.StudentAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentAppTheme {
                StudentApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(application))
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val filteredStudents = students.filter { student ->
        searchQuery.isEmpty() || 
        student.firstName.contains(searchQuery, ignoreCase = true) ||
        student.lastName.contains(searchQuery, ignoreCase = true) ||
        student.email.contains(searchQuery, ignoreCase = true) ||
        student.department.contains(searchQuery, ignoreCase = true) ||
        student.studentNumber.contains(searchQuery, ignoreCase = true)
    }
    
    LaunchedEffect(Unit) {
        viewModel.fetchStudents()
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Student Management System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search students...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading students...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "Unknown error",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchStudents() }) {
                            Text("Retry")
                        }
                    }
                }
            } else if (filteredStudents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No students found" else "No students available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredStudents) { student ->
                        StudentCard(
                            student = student,
                            isSelected = selectedStudent?.id == student.id,
                            onEdit = {
                                selectedStudent = student
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.deleteStudent(student)
                                scope.launch {
                                    snackbarHostState.showSnackbar("${student.firstName} ${student.lastName} deleted")
                                }
                            },
                            onClick = {
                                selectedStudent = if (selectedStudent?.id == student.id) null else student
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        StudentDialog(
            title = "Add New Student",
            student = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { firstName, lastName, email, department, studentNumber ->
                val newStudent = Student(
                    id = 0,
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    department = department,
                    studentNumber = studentNumber
                )
                viewModel.addStudent(newStudent)
                scope.launch {
                    snackbarHostState.showSnackbar("$firstName $lastName added successfully")
                }
                showAddDialog = false
            }
        )
    }
    
    if (showEditDialog && selectedStudent != null) {
        StudentDialog(
            title = "Edit Student",
            student = selectedStudent,
            onDismiss = { 
                showEditDialog = false
                selectedStudent = null
            },
            onConfirm = { firstName, lastName, email, department, studentNumber ->
                val updatedStudent = selectedStudent!!.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    department = department,
                    studentNumber = studentNumber
                )
                viewModel.updateStudent(updatedStudent)
                scope.launch {
                    snackbarHostState.showSnackbar("${updatedStudent.firstName} ${updatedStudent.lastName} updated successfully")
                }
                showEditDialog = false
                selectedStudent = null
            }
        )
    }
}

@Composable
fun StudentCard(
    student: Student,
    isSelected: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "elevation"
    )
    
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = MaterialTheme.shapes.medium)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 2.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (student.imageUrl != null && student.imageUrl.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "${student.firstName} ${student.lastName}",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondary 
                                else MaterialTheme.colorScheme.primaryContainer, 
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${student.firstName.first()}${student.lastName.first()}".uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondary 
                                   else MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = "Student ID: ${student.studentNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.secondary 
                           else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = student.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) contentColor 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = student.department,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) contentColor 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Student",
                        tint = if (isSelected) MaterialTheme.colorScheme.secondary 
                              else MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Student",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDialog(
    title: String,
    student: Student?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var firstName by remember { mutableStateOf(student?.firstName ?: "") }
    var lastName by remember { mutableStateOf(student?.lastName ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var department by remember { mutableStateOf(student?.department ?: "") }
    var studentNumber by remember { mutableStateOf(student?.studentNumber ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = studentNumber,
                    onValueChange = { studentNumber = it },
                    label = { Text("Student Number") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(firstName, lastName, email, department, studentNumber)
                },
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && studentNumber.isNotBlank()
            ) {
                Text(if (student == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}