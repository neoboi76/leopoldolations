package com.gabriel.employeeapp.data.repository
import android.app.Application
import com.gabriel.employeeapp.data.remote.EmployeeApi

class EmployeeRepositoryImpl constructor (
    private val api: EmployeeApi,
    private val appContext: Application
){
}