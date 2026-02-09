package com.gabriel.employeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
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
    
    // State variables
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    
    // Observe students from ViewModel
    val students by viewModel.students.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Filter students based on search query
    val filteredStudents = students.filter { student ->
        searchQuery.isEmpty() || 
        student.firstName.contains(searchQuery, ignoreCase = true) ||
        student.lastName.contains(searchQuery, ignoreCase = true) ||
        student.email.contains(searchQuery, ignoreCase = true) ||
        student.department.contains(searchQuery, ignoreCase = true) ||
        student.studentNumber.contains(searchQuery, ignoreCase = true)
    }
    
    // Load initial data
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search students...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Content
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
                            onEdit = {
                                selectedStudent = student
                                showEditDialog = true
                            },
                            onDelete = {
                                viewModel.deleteStudent(student)
                                scope.launch {
                                    snackbarHostState.showSnackbar("${student.firstName} ${student.lastName} deleted")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Add Student Dialog
    if (showAddDialog) {
        StudentDialog(
            title = "Add New Student",
            student = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { firstName, lastName, email, department, studentNumber ->
                val newStudent = Student(
                    id = 0, // ID will be assigned by the server
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
    
    // Edit Student Dialog
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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = MaterialTheme.shapes.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 2.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (student.imageUrl != null && student.imageUrl.isNotEmpty()) {
                    // Note: You would typically use Coil or Glide for image loading
                    // For now, using a placeholder
                    // AsyncImage would be used here with proper Coil dependency
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "${student.firstName} ${student.lastName}",
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${student.firstName.first()}${student.lastName.first()}".uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Student Information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${student.firstName} ${student.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Student ID: ${student.studentNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = student.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = student.department,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action Buttons
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Student",
                        tint = MaterialTheme.colorScheme.primary
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