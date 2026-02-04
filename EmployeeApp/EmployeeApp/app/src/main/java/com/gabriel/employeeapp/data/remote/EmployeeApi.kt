package com.gabriel.employeeapp.data.remote

import com.gabriel.employeeapp.data.model.Employee
import retrofit2.http.GET

interface EmployeeApi {
    @GET("/api/employee")
    suspend fun getEmployees(): List<Employee>
}