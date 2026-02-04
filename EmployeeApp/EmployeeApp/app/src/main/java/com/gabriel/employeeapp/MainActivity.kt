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
    Row (Modifier.padding(all = 100.dp)) {
        Image(
            painter = painterResource(R.drawable.leo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(3.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        Spacer(modifier = Modifier.width(8.dp))
        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )
        Column(modifier = Modifier.clickable{isExpanded = !isExpanded} ) {
            Text (
                text = employee.name,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme. typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
            ) {
                Text(
                    text = employee.description,
                    modifier = Modifier.padding(all = 4.dp),
                    maxLines = if(isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }


}

@Composable
fun LoadingScreen() {
    Text("Loading...")
}

@Composable
fun ErrorScreen(message: String) {
    Text("Error: $message")
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
