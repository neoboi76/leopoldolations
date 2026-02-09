package com.gabriel.employeeapp.data.model

data class Student(
    val id: Long? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val department: String,
    val studentNumber: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val imageUrl: String? = null
)