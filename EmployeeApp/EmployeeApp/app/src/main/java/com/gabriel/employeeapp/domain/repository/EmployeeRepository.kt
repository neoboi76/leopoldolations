package com.gabriel.employeeapp.domain.repository

import com.gabriel.employeeapp.data.model.Employee

interface EmployeeRepository {
    fun doNetworkCall()
    fun getEmployees(): List<Employee>
}