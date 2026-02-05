package com.gabriel.employeeapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gabriel.employeeapp.data.model.Employee
import com.gabriel.employeeapp.ui.theme.EmployeeAppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.res.ResourcesCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmployeeAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    EmployeeScreen(
                        modifier = Modifier.padding((innerPadding))
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeScreen(viewModel: EmployeeViewModel = viewModel(), modifier: Modifier) {
    val Employees by viewModel.employees.collectAsState()

    if(Employees.isEmpty()){
        LoadingScreen()
        TestEmployeeList() // Fallback to test data
    } else {
        EmployeeList(Employees = Employees, modifier)
    }
}



@Composable
fun EmployeeList(Employees: List<Employee>, modifier: Modifier) {
    LazyColumn {
        items(Employees) { Employee ->
            EmployeeItem(employee = Employee)
        }
    }
}


@Composable
fun EmployeeItem(employee: Employee) {
    Row (Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.leo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
            )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text (
                text = "${employee.firstName} ${employee.lastName}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = employee.department,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = employee.email,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Text("Loading employees...")
}

@Composable
fun ErrorScreen(message: String) {
    Text("Error: $message")
}

@Composable
fun TestEmployeeList() {
    val testEmployees = listOf(
        Employee(1, "John", "Doe", "john@example.com", "Engineering"),
        Employee(2, "Jane", "Smith", "jane@example.com", "Management")
    )
    EmployeeList(testEmployees, Modifier)
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
